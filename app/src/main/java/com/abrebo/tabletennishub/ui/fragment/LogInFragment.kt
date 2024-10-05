package com.abrebo.tabletennishub.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.MainPageActivity
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentLogInBinding
import com.abrebo.tabletennishub.ui.viewmodel.LogInViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.installations.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.security.AccessController


@AndroidEntryPoint
class LogInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel:LogInViewModel
    private lateinit var binding:FragmentLogInBinding
    private lateinit var email:String
    private lateinit var password:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val temp:LogInViewModel by viewModels()
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
            Toast.makeText(requireContext(),"Email'i girin",Toast.LENGTH_SHORT).show()
        }
    }

    private fun logIn(it:View) {
        email = binding.userEmailText.text.toString()
        password = binding.userPasswordText.text.toString()
        binding.progressBar.visibility = View.VISIBLE
        if (email.isNotEmpty() && password.isNotEmpty()){
            viewModel.signInWithEmailAndPassword(auth,email,password,binding.progressBar,it)
        }else{
            Toast.makeText(requireContext(),"Email ve şifreyi girin",Toast.LENGTH_SHORT).show()
        }
    }
}