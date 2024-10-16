package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.databinding.FragmentProfileBinding
import com.abrebo.tabletennishub.ui.viewmodel.ProfileViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    private lateinit var viewModel:ProfileViewModel
    private lateinit var auth:FirebaseAuth
    private lateinit var user:User
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:ProfileViewModel by viewModels()
        viewModel=temp
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/8444388881"
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
        binding.linearLayoutAddFriend.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_addFriendFragment)
        }
        binding.textViewRequest.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_requestsFragment)
        }
        binding.textViewFriends.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_friendsFragment)
        }
        binding.linearLayoutSettings.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        binding.textViewPersonalInformationChange.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_myInformationFragment)
        }
        viewModel.map.observe(viewLifecycleOwner){
            binding.textViewNameFamily.text=it["userName"].toString()
            binding.textViewEmail.text=it["email"].toString()
            user=User(it["id"].toString(),it["nameFamily"].toString(),it["userName"].toString(),it["email"].toString())
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }

}