package com.abrebo.tabletennishub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.abrebo.tabletennishub.databinding.ActivityMainPageBinding
import com.abrebo.tabletennishub.utils.configureSystemBarInsets
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainPageActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding:ActivityMainPageBinding
    private lateinit var bannerAdView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSystemBarInsets(binding.root)
        MobileAds.initialize(this) {}
        bannerAdView = AdView(this).apply {
            adUnitId = "ca-app-pub-4667560937795938/1898700671"
            setAdSize(AdSize.BANNER)
        }
        binding.adBanner.addView(bannerAdView)
        bannerAdView.loadAd(AdRequest.Builder().build())

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

    }
    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        return navController.navigateUp()
    }

    override fun onPause() {
        if (::bannerAdView.isInitialized) bannerAdView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::bannerAdView.isInitialized) bannerAdView.resume()
    }

    override fun onDestroy() {
        if (::bannerAdView.isInitialized) bannerAdView.destroy()
        super.onDestroy()
    }
}
