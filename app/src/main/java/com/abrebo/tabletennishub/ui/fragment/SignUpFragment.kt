package com.abrebo.tabletennishub.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.databinding.FragmentSignUpBinding
import com.abrebo.tabletennishub.ui.viewmodel.SignUpViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private lateinit var nameFamily:String
    private lateinit var userName:String
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var passwordRetry:String
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val temp:SignUpViewModel by viewModels()
        viewModel=temp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentSignUpBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BackPressUtils.setBackPressCallback(this, viewLifecycleOwner)
        binding.logInTextButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_signUpFragment_to_logInFragment)
        }

        binding.signUpButton.setOnClickListener {
            signUp()
        }

    }

    private fun signUp(){
        nameFamily = binding.nameFamilyText.text.toString()
        userName = binding.userNameText.text.toString().lowercase()
        email = binding.emailText.text.toString()
        password = binding.passwordText.text.toString()
        passwordRetry = binding.passwordRetryText.text.toString()

        if (isNotEmptyUserInput(nameFamily, userName, email, password, passwordRetry)) {
            binding.progressBar.visibility = View.VISIBLE

            viewModel.checkUserNameAvailability(userName) { isAvailable ->
                if (isAvailable) {
                    val userObject=User("",nameFamily,userName,email)
                    viewModel.createUserWithEmailAndPassword(auth,email,password,userObject,binding.progressBar)
                    clearText()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.Usernamealreadyexists),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), requireContext().getString(R.string.Pleasefilloutallfieldscompletely), Toast.LENGTH_SHORT).show()
        }
    }
    private fun isNotEmptyUserInput(nameFamily: String, userName: String, email: String, password: String, passwordRetry: String):Boolean {
        return nameFamily.isNotEmpty() && userName.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && passwordRetry.isNotEmpty() && password==passwordRetry
    }

    @SuppressLint("SetTextI18n")
    private fun clearText(){
        binding.nameFamilyText.setText("")
        binding.userNameText.setText("")
        binding.emailText.setText("")
        binding.passwordText.setText("")
        binding.passwordRetryText.setText("")
    }
}