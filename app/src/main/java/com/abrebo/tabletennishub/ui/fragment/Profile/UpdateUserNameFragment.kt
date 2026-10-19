package com.abrebo.tabletennishub.ui.fragment.Profile

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.databinding.FragmentUpdateUserNameBinding
import com.abrebo.tabletennishub.ui.viewmodel.Profile.SettingsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateUserNameFragment : Fragment() {
    private lateinit var binding: FragmentUpdateUserNameBinding
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var cooldownTimer: CountDownTimer? = null
    private var cooldownKey: String = ""
    private var updateInFlight = false
    private val cooldownMillis = 24 * 60 * 60 * 1000L
    private val preferences by lazy { requireContext().getSharedPreferences("profile_change_limits", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        viewModel.getUserInfo(auth.currentUser?.email.orEmpty())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUpdateUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageType = UpdateUserNameFragmentArgs.fromBundle(requireArguments()).PageType
        cooldownKey = if (pageType == PageType.USER_NAME) {
            "username_cooldown_until"
        } else {
            "name_family_cooldown_until"
        }

        if (pageType == PageType.USER_NAME) {
            binding.materialToolbar7.setTitle(R.string.ChangeUsername)
            binding.textInputLayoutUserName.hint = getString(R.string.newUsername)
        } else {
            binding.materialToolbar7.setTitle(R.string.ChangeFirstNameandLastName)
            binding.textInputLayoutUserName.hint = getString(R.string.NewFirstNameandLastName)
        }

        binding.userNameUpdateButton.setOnClickListener {
            if (!canSubmitProfileChange()) return@setOnClickListener
            val newValue = binding.userNameText.text?.toString()?.trim().orEmpty()
            if (newValue.isEmpty()) {
                showMessage(if (pageType == PageType.USER_NAME) R.string.Usernamecannotbeempty else R.string.FirstNameandLastNamecannotbeempty)
                return@setOnClickListener
            }

            val currentProfile = viewModel.map.value
            if (currentProfile == null) {
                showMessage(R.string.password_reset_email_failed)
                return@setOnClickListener
            }

            val currentNameFamily = currentProfile["nameFamily"].toString()
            val currentUserName = currentProfile["userName"].toString()
            val email = currentProfile["email"].toString()
            val id = currentProfile["id"].toString()

            if (pageType == PageType.USER_NAME) {
                checkAndUpdateUsername(id, currentNameFamily, currentUserName, email, newValue)
            } else {
                updateNameFamily(id, currentUserName, email, newValue)
            }
        }
        refreshProfileChangeCooldown()
    }

    private fun checkAndUpdateUsername(
        id: String,
        nameFamily: String,
        oldUserName: String,
        email: String,
        newUserName: String
    ) {
        updateInFlight = true
        binding.userNameUpdateButton.isEnabled = false
        viewModel.checkUserNameAvailability(newUserName) { isAvailable ->
            if (!isAdded) return@checkUserNameAvailability
            if (!isAvailable) {
                updateInFlight = false
                refreshProfileChangeCooldown()
                showMessage(R.string.Usernamealreadyexists)
                return@checkUserNameAvailability
            }

            val user = User(id, nameFamily, newUserName, email)
            viewModel.updateUserData(user) { success ->
                if (!isAdded) return@updateUserData
                updateInFlight = false
                if (success) {
                    viewModel.updateUserDocumentId(oldUserName, newUserName)
                    viewModel.updateWinnerUserName(oldUserName, newUserName)
                    viewModel.updateUserHomeUserName(oldUserName, newUserName)
                    viewModel.updateUserAwayUserName(oldUserName, newUserName)
                    viewModel.updateUserNameInDocuments(oldUserName, newUserName)
                    viewModel.updateUserNameInFriendRequests(oldUserName, newUserName)
                    startProfileChangeCooldown()
                    showMessage(R.string.Usernamehasbeensuccessfullyupdated)
                } else {
                    refreshProfileChangeCooldown()
                    showMessage(R.string.password_reset_email_failed)
                }
            }
        }
    }

    private fun updateNameFamily(id: String, userName: String, email: String, newNameFamily: String) {
        updateInFlight = true
        binding.userNameUpdateButton.isEnabled = false
        viewModel.updateUserData(User(id, newNameFamily, userName, email)) { success ->
            if (!isAdded) return@updateUserData
            updateInFlight = false
            if (success) {
                startProfileChangeCooldown()
                showMessage(R.string.FirstNameandLastNamehavebeensuccessfullyupdated)
            } else {
                refreshProfileChangeCooldown()
                showMessage(R.string.password_reset_email_failed)
            }
        }
    }

    private fun canSubmitProfileChange(): Boolean {
        if (updateInFlight) return false
        if (remainingCooldown() > 0L) {
            refreshProfileChangeCooldown()
            return false
        }
        return true
    }

    private fun remainingCooldown(): Long =
        (preferences.getLong(cooldownKey, 0L) - System.currentTimeMillis()).coerceAtLeast(0L)

    private fun startProfileChangeCooldown() {
        preferences.edit().putLong(cooldownKey, System.currentTimeMillis() + cooldownMillis).apply()
        refreshProfileChangeCooldown()
    }

    private fun refreshProfileChangeCooldown() {
        cooldownTimer?.cancel()
        val remaining = remainingCooldown()
        binding.userNameUpdateButton.isEnabled = remaining <= 0L && !updateInFlight
        if (remaining <= 0L) {
            binding.profileChangeCooldownGroup.visibility = View.GONE
            return
        }

        binding.profileChangeCooldownGroup.visibility = View.VISIBLE
        binding.profileChangeCooldownProgress.max = cooldownMillis.toInt()
        cooldownTimer = object : CountDownTimer(remaining, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished + 999L) / 1000L
                binding.profileChangeCooldownProgress.progress = millisUntilFinished.toInt()
                binding.profileChangeCooldownText.text = getString(
                    R.string.retry_in,
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60
                )
            }

            override fun onFinish() {
                binding.profileChangeCooldownGroup.visibility = View.GONE
                binding.userNameUpdateButton.isEnabled = !updateInFlight
            }
        }.start()
    }

    private fun showMessage(messageResId: Int) {
        Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo(auth.currentUser?.email.orEmpty())
        if (cooldownKey.isNotEmpty()) refreshProfileChangeCooldown()
    }

    override fun onDestroyView() {
        cooldownTimer?.cancel()
        cooldownTimer = null
        super.onDestroyView()
    }
}
