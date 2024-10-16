package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMainBinding
import com.abrebo.tabletennishub.ui.adapter.MatchAdapter
import com.abrebo.tabletennishub.ui.viewmodel.MainViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var viewModel:MainViewModel
    private lateinit var auth:FirebaseAuth
    private var currentUserName:String=""
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val temp: MainViewModel by viewModels()
        viewModel = temp
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail != null) {
            viewModel.getUserNameByEmail(currentUserEmail) { userName ->
                if (userName != null) {
                    currentUserName = userName
                    viewModel.getMatchesByUserName(currentUserName)
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentMainBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/5466871752"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        viewModel.loadInterstitialAd()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BackPressUtils.setBackPressCallback(this, viewLifecycleOwner)

        binding.floatingActionButton.setOnClickListener {
            viewModel.showInterstitialAd(requireActivity())
            Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_addMatchFragment)
        }

        viewModel.matches.observe(viewLifecycleOwner){
            val adapter=MatchAdapter(requireContext(),it,viewModel,auth)
            binding.matchRecyclerView.adapter=adapter
        }

    }

    override fun onResume() {
        super.onResume()
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail != null) {
            viewModel.getUserNameByEmail(currentUserEmail) { userName ->
                if (userName != null) {
                    currentUserName = userName
                    viewModel.getMatchesByUserName(currentUserName)
                }
            }
        }
    }

}