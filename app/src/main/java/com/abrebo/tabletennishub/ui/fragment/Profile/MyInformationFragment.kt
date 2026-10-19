package com.abrebo.tabletennishub.ui.fragment.Profile

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMyInformationBinding
import com.abrebo.tabletennishub.ui.viewmodel.Profile.SettingsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInformationFragment : Fragment() {
    private var resetTimer: CountDownTimer? = null
    private var resetRequestInFlight = false
    private val cooldownMillis = 24 * 60 * 60 * 1000L
    private val resetPreferences by lazy { requireContext().getSharedPreferences("password_reset", 0) }
    private lateinit var binding:FragmentMyInformationBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp: SettingsViewModel by viewModels()
        viewModel=temp
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyInformationBinding.inflate(inflater, container, false)
return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePasswordResetCooldown()

        viewModel.map.observe(viewLifecycleOwner){map->
            binding.nameFamilyText.setText(map["nameFamily"].toString())
            binding.userNameText.setText(map["userName"].toString())
            binding.emailText.setText(map["email"].toString())
        }

        binding.userNameText.setOnClickListener {
            val navDirection=MyInformationFragmentDirections.actionMyInformationFragmentToUpdateUserNameFragment(PageType.USER_NAME)
            Navigation.findNavController(it).navigate(navDirection)
        }
        binding.nameFamilyText.setOnClickListener {
            val navDirection=MyInformationFragmentDirections.actionMyInformationFragmentToUpdateUserNameFragment(PageType.NAME_FAMILY)
            Navigation.findNavController(it).navigate(navDirection)
        }
        binding.passwordUpdateButton.setOnClickListener {
            if (remainingPasswordResetCooldown() > 0L || resetRequestInFlight) {
                updatePasswordResetCooldown()
                return@setOnClickListener
            }
            val email = auth.currentUser?.email
            if (email.isNullOrBlank()) {
                Toast.makeText(requireContext(), R.string.password_reset_email_failed, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetRequestInFlight = true
            binding.passwordUpdateButton.isEnabled = false
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (!isAdded) return@addOnCompleteListener
                resetRequestInFlight = false
                if (task.isSuccessful){
                    resetPreferences.edit()
                        .putLong("daily_cooldown_until", System.currentTimeMillis() + cooldownMillis)
                        .apply()
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.password_reset_email_sent),
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.password_reset_email_failed),
                        Toast.LENGTH_SHORT).show()

                }
                updatePasswordResetCooldown()
            }
        }
    }

    private fun remainingPasswordResetCooldown(): Long =
        (resetPreferences.getLong("daily_cooldown_until", 0L) - System.currentTimeMillis())
            .coerceAtLeast(0L)

    private fun updatePasswordResetCooldown() {
        resetTimer?.cancel()
        val remaining = remainingPasswordResetCooldown()
        binding.passwordUpdateButton.isEnabled = remaining <= 0L && !resetRequestInFlight
        if (remaining <= 0L) {
            binding.passwordResetCooldownGroup.visibility = View.GONE
            return
        }

        binding.passwordResetCooldownGroup.visibility = View.VISIBLE
        binding.passwordResetCooldownProgress.max = cooldownMillis.toInt()
        resetTimer = object : CountDownTimer(remaining, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished + 999L) / 1000L
                binding.passwordResetCooldownProgress.progress = millisUntilFinished.toInt()
                binding.passwordResetCooldownText.text = getString(
                    R.string.password_reset_cooldown,
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60
                )
            }

            override fun onFinish() {
                binding.passwordResetCooldownGroup.visibility = View.GONE
                binding.passwordUpdateButton.isEnabled = !resetRequestInFlight
            }
        }.start()
    }

    override fun onDestroyView() {
        resetTimer?.cancel()
        resetTimer = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }
}
