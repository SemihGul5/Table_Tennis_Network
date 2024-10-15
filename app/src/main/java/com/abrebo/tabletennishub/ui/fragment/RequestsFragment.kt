package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentRequestsBinding
import com.abrebo.tabletennishub.ui.adapter.RequestsPagerAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestsFragment : Fragment() {
    private lateinit var binding:FragmentRequestsBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentRequestsBinding.inflate(inflater, container, false)
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

        val adapter = RequestsPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // TabLayout ve ViewPager arasında bağ kur
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) requireContext().getString(R.string.IncomingRequests) else
                requireContext().getString(R.string.SentRequests)
        }.attach()
    }

}