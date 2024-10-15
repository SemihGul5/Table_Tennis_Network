package com.abrebo.tabletennishub.ui.fragment

import android.content.Intent
import android.os.Bundle
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
import com.abrebo.tabletennishub.ui.viewmodel.LogInViewModel
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
class LogInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel:LogInViewModel
    private lateinit var binding:FragmentLogInBinding
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val temp:LogInViewModel by viewModels()
        viewModel=temp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentLogInBinding.inflate(inflater, container, false)
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
        binding.signUpTextButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified()) {
            val intent = Intent(requireContext(),MainPageActivity::class.java)
            startActivity(intent)
        }
        binding.logInButton.setOnClickListener {
            logIn(it)
        }
        binding.textViewForgotPassword.setOnClickListener {
            forgotPasswordClicked()
        }



    }

    private fun forgotPasswordClicked() {
        email = binding.userEmailText.text.toString()
        if (email.isNotEmpty()){
            viewModel.sendPasswordResetEmail(auth,email)
        }else{
            Toast.makeText(requireContext(),requireContext().getString(R.string.Enteryouremail),Toast.LENGTH_SHORT).show()
        }
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