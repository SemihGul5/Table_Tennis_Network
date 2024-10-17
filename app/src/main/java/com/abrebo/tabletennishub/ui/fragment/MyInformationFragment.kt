package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMyInformationBinding
import com.abrebo.tabletennishub.ui.viewmodel.SettingsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInformationFragment : Fragment() {
    private lateinit var binding:FragmentMyInformationBinding
    private lateinit var viewModel:SettingsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:SettingsViewModel by viewModels()
        viewModel=temp
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyInformationBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/9757470555"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            auth.sendPasswordResetEmail(auth.currentUser?.email!!).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.password_reset_email_sent),
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.password_reset_email_failed),
                        Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }
}