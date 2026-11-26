package com.abrebo.tabletennishub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import android.view.View
import com.abrebo.tabletennishub.ads.AdsManager
import com.abrebo.tabletennishub.databinding.ActivityMainPageBinding
import com.abrebo.tabletennishub.utils.configureSystemBarInsets
import com.google.android.gms.ads.AdView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainPageActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding:ActivityMainPageBinding
    private var bannerAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureSystemBarInsets(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBannerForDestination(destination.id)
        }
        AdsManager.updateConsent(this) {
            if (!isFinishing && !isDestroyed) updateBannerForDestination(navController.currentDestination?.id)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        return navController.navigateUp()
    }

    private fun updateBannerForDestination(destinationId: Int?) {
        val container = binding.adBanner
        val showOnThisScreen = destinationId == R.id.mainFragment && AdsManager.isAdsReady
        container.visibility = if (showOnThisScreen) View.VISIBLE else View.GONE
        val existingBanner = bannerAdView
        if (showOnThisScreen && existingBanner == null) {
            bannerAdView = AdsManager.loadBanner(this, container)
        }
        if (showOnThisScreen) bannerAdView?.resume() else bannerAdView?.pause()
    }

    fun onAdsConsentChanged() {
        if (!AdsManager.isAdsReady) {
            bannerAdView?.destroy()
            bannerAdView = null
            binding.adBanner.removeAllViews()
        }
        updateBannerForDestination(navController.currentDestination?.id)
    }

    override fun onPause() {
        AdsManager.setForeground(false)
        bannerAdView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        AdsManager.setForeground(true)
        if (navController.currentDestination?.id == R.id.mainFragment) bannerAdView?.resume()
    }

    override fun onDestroy() {
        bannerAdView?.destroy()
        bannerAdView = null
        super.onDestroy()
    }
}
