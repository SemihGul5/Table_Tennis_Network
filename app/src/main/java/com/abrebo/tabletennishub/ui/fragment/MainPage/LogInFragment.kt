package com.abrebo.tabletennishub.ui.fragment.MainPage

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.MainPageActivity
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentLogInBinding
import com.abrebo.tabletennishub.ui.viewmodel.MainPage.LogInViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LogInFragment : Fragment() {
    private var resetTimer: CountDownTimer? = null
    private var resetRequestInFlight = false
    private val cooldownMillis = 24 * 60 * 60 * 1000L
    private val preferences by lazy { requireContext().getSharedPreferences("password_reset", 0) }
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LogInViewModel
    private lateinit var binding:FragmentLogInBinding
    private lateinit var email:String
    private lateinit var password:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val temp: LogInViewModel by viewModels()
        viewModel=temp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentLogInBinding.inflate(inflater, container, false)
return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BackPressUtils.setBackPressCallback(this, viewLifecycleOwner)
        binding.signUpTextButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        if (auth.currentUser != null) {
            val intent = Intent(requireContext(),MainPageActivity::class.java)
            startActivity(intent)
        }
        binding.logInButton.setOnClickListener {
            logIn(it)
        }
        binding.textViewForgotPassword.setOnClickListener {
            forgotPasswordClicked()
        }
        updateResetCooldown()



    }

    private fun forgotPasswordClicked() {
        if (remainingCooldown() > 0L || resetRequestInFlight) {
            updateResetCooldown()
            return
        }
        email = binding.userEmailText.text.toString()
        if (email.isNotEmpty()){
            resetRequestInFlight = true
            binding.textViewForgotPassword.isEnabled = false
            viewModel.checkRegisteredEmail(email) { isRegistered ->
                if (!isAdded) return@checkRegisteredEmail
                when (isRegistered) {
                    true -> {
                        preferences.edit().putLong("daily_cooldown_until", System.currentTimeMillis() + cooldownMillis).apply()
                        updateResetCooldown()
                        viewModel.sendPasswordResetEmail(auth, email) {
                            resetRequestInFlight = false
                            if (remainingCooldown() <= 0L) binding.textViewForgotPassword.isEnabled = true
                        }
                    }
                    false -> {
                        resetRequestInFlight = false
                        binding.textViewForgotPassword.isEnabled = true
                        Toast.makeText(requireContext(), R.string.password_reset_email_not_registered, Toast.LENGTH_SHORT).show()
                    }
                    null -> {
                        resetRequestInFlight = false
                        binding.textViewForgotPassword.isEnabled = true
                        Toast.makeText(requireContext(), R.string.password_reset_email_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(requireContext(),requireContext().getString(R.string.Enteryouremail),Toast.LENGTH_SHORT).show()
        }
    }

    private fun remainingCooldown(): Long =
        (preferences.getLong("daily_cooldown_until", 0L) - System.currentTimeMillis()).coerceAtLeast(0L)

    private fun updateResetCooldown() {
        resetTimer?.cancel()
        val remaining = remainingCooldown()
        if (remaining <= 0L) {
            binding.resetCooldownGroup.visibility = View.GONE
            binding.textViewForgotPassword.isEnabled = !resetRequestInFlight
            return
        }
        binding.resetCooldownGroup.visibility = View.VISIBLE
        binding.textViewForgotPassword.isEnabled = false
        binding.resetCooldownProgress.max = cooldownMillis.toInt()
        resetTimer = object : CountDownTimer(remaining, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished + 999L) / 1000L
                binding.resetCooldownProgress.progress = millisUntilFinished.toInt()
                binding.resetCooldownText.text = getString(
                    R.string.password_reset_cooldown,
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60
                )
            }
            override fun onFinish() {
                binding.resetCooldownGroup.visibility = View.GONE
                binding.textViewForgotPassword.isEnabled = !resetRequestInFlight
            }
        }.start()
    }

    override fun onDestroyView() {
        resetTimer?.cancel()
        resetTimer = null
        super.onDestroyView()
    }

    private fun logIn(it:View) {
        email = binding.userEmailText.text.toString()
        password = binding.userPasswordText.text.toString()
        binding.progressBar.visibility = View.VISIBLE
        if (email.isNotEmpty() && password.isNotEmpty()){
            viewModel.signInWithEmailAndPassword(auth,email,password,binding.progressBar,it)
        }else{
            Toast.makeText(requireContext(),requireContext().getString(R.string.Enteryouremailandpassword),Toast.LENGTH_SHORT).show()
        }
    }
}
