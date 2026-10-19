package com.abrebo.tabletennishub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abrebo.tabletennishub.databinding.ActivityMainBinding
import com.abrebo.tabletennishub.utils.configureSystemBarInsets
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bannerAdView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSystemBarInsets(binding.root)
        MobileAds.initialize(this) {}
        bannerAdView = AdView(this).apply {
            adUnitId = "ca-app-pub-4667560937795938/1898700671"
            setAdSize(AdSize.BANNER)
        }
        binding.adBanner.addView(bannerAdView)
        bannerAdView.loadAd(AdRequest.Builder().build())


    }

    override fun onDestroy() {
        if (::bannerAdView.isInitialized) bannerAdView.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        if (::bannerAdView.isInitialized) bannerAdView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::bannerAdView.isInitialized) bannerAdView.resume()
    }
}
