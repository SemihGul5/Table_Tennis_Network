package com.abrebo.tabletennishub.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.abrebo.tabletennishub.BuildConfig
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.Executors

/** Centralizes consent gating, ad requests, frequency limits, and view cleanup. */
object AdsManager {
    private const val PREFS_NAME = "advertising"
    private const val PREF_LAST_INTERSTITIAL = "last_interstitial_shown_at"

    private const val RELEASE_APP_ID = "ca-app-pub-4667560937795938~6441574907"
    private const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    private const val RELEASE_BANNER_ID = "ca-app-pub-4667560937795938/1898700671"
    private const val RELEASE_INTERSTITIAL_ID = "ca-app-pub-4667560937795938/6959455665"
    private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    private val handler = Handler(Looper.getMainLooper())
    private val sdkInitializationExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "gma-sdk-initializer").apply { isDaemon = true }
    }
    private val initializationCallbacks = mutableListOf<() -> Unit>()

    private var appContext: Context? = null
    private var consentInformation: ConsentInformation? = null
    private var sdkInitialized = false
    private var sdkInitializing = false
    private var canRequestAds = false
    private var appInForeground = false
    private var interstitialAd: InterstitialAd? = null
    private var interstitialLoading = false
    private var interstitialShowing = false
    private var interstitialRetryCount = 0
    private var interstitialRequestGeneration = 0
    private var interstitialRetryRunnable: Runnable? = null

    val isAdsReady: Boolean
        get() = canRequestAds && sdkInitialized

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation?.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    private val bannerUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_BANNER_ID else RELEASE_BANNER_ID

    private val interstitialUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL_ID else RELEASE_INTERSTITIAL_ID

    private val applicationId: String
        get() = if (BuildConfig.DEBUG) TEST_APP_ID else RELEASE_APP_ID

    /** Refreshes UMP consent at app entry and initializes GMA only when requests are allowed. */
    fun updateConsent(activity: Activity, onComplete: () -> Unit) {
        if (activity.isFinishing || activity.isDestroyed) {
            onComplete()
            return
        }

        val info = UserMessagingPlatform.getConsentInformation(activity.applicationContext)
        appContext = activity.applicationContext
        consentInformation = info
        info.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                handler.post {
                    if (activity.isFinishing || activity.isDestroyed) {
                        finishConsent(info, onComplete)
                    } else {
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                            handler.post { finishConsent(info, onComplete) }
                        }
                    }
                }
            },
            {
                handler.post {
                    // UMP can use the consent state saved by its SDK when this refresh fails.
                    finishConsent(info, onComplete)
                }
            }
        )
    }

    private fun finishConsent(info: ConsentInformation, onComplete: () -> Unit) {
        canRequestAds = info.canRequestAds()
        if (!canRequestAds) {
            discardInterstitial()
            onComplete()
            return
        }
        initializeAds(onComplete)
    }

    private fun initializeAds(onComplete: () -> Unit) {
        if (sdkInitialized) {
            onComplete()
            return
        }

        initializationCallbacks += onComplete
        if (sdkInitializing) return

        val context = appContext
        if (context == null) {
            sdkInitializing = false
            completeInitializationCallbacks()
            return
        }

        sdkInitializing = true
        val requestConfiguration = RequestConfiguration.Builder()
            .setCoarseLocationCollectionEnabled(false)
            .build()
        val initializationConfig = InitializationConfig.Builder(applicationId)
            .setRequestConfiguration(requestConfiguration)
            .build()

        sdkInitializationExecutor.execute {
            try {
                MobileAds.initialize(context, initializationConfig) {
                    handler.post {
                        sdkInitializing = false
                        sdkInitialized = true
                        completeInitializationCallbacks()
                    }
                }
            } catch (_: RuntimeException) {
                handler.post {
                    sdkInitializing = false
                    completeInitializationCallbacks()
                }
            }
        }
    }

    private fun completeInitializationCallbacks() {
        initializationCallbacks.toList().also { initializationCallbacks.clear() }.forEach { it() }
    }

    fun showPrivacyOptions(activity: Activity, onComplete: () -> Unit = {}) {
        if (activity.isFinishing || activity.isDestroyed || !isPrivacyOptionsRequired) {
            onComplete()
            return
        }

        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            handler.post {
                val info = consentInformation
                canRequestAds = info?.canRequestAds() == true
                if (canRequestAds) {
                    initializeAds(onComplete)
                } else {
                    discardInterstitial()
                    onComplete()
                }
            }
        }
    }

    fun setForeground(isForeground: Boolean) {
        appInForeground = isForeground
        if (!isForeground) cancelInterstitialRetry()
    }

    /** Loads one standard 320x50 banner into a separately reserved screen slot. */
    fun loadBanner(
        activity: Activity,
        container: FrameLayout,
        onLoadFailed: () -> Unit = {}
    ): AdView? {
        if (!isAdsReady || !appInForeground || activity.isFinishing || activity.isDestroyed) return null
        if (container.childCount > 0 || container.width <= 0) return null

        val widthDp = (container.width / activity.resources.displayMetrics.density).toInt()
        if (widthDp < 320) return null

        val adSize = AdSize.BANNER
        container.layoutParams = container.layoutParams.apply {
            height = adSize.getHeightInPixels(activity) + container.paddingTop + container.paddingBottom
        }

        val adView = AdView(activity)
        container.addView(
            adView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )
        val request = BannerAdRequest.Builder(bannerUnitId, adSize).build()
        adView.loadAd(request, object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                handler.post {
                    if (!isAdsReady || !appInForeground || activity.isFinishing || activity.isDestroyed) {
                        if (adView.parent === container) {
                            adView.destroy()
                            container.removeView(adView)
                        }
                        onLoadFailed()
                        return@post
                    }
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                handler.post {
                    if (adView.parent === container) {
                        adView.destroy()
                        container.removeView(adView)
                    }
                    container.visibility = android.view.View.GONE
                    onLoadFailed()
                }
            }
        })
        return adView
    }

    /** Called only when the user opens a match form, never from view rebuilds. */
    fun preloadInterstitial(context: Context) {
        if (!isAdsReady || !appInForeground || interstitialAd != null || interstitialLoading) return
        if (interstitialRetryRunnable != null || isInterstitialCoolingDown(context)) return

        interstitialRetryCount = 0
        loadInterstitial(context.applicationContext)
    }

    private fun loadInterstitial(context: Context) {
        if (!isAdsReady || !appInForeground || interstitialLoading) return

        interstitialLoading = true
        val requestGeneration = ++interstitialRequestGeneration
        InterstitialAd.load(
            AdRequest.Builder(interstitialUnitId).build(),
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    handler.post {
                        if (requestGeneration != interstitialRequestGeneration) {
                            ad.destroy()
                            return@post
                        }
                        interstitialLoading = false
                        if (isAdsReady && appInForeground) {
                            interstitialRetryCount = 0
                            interstitialAd = ad
                        } else {
                            ad.destroy()
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    handler.post {
                        if (requestGeneration != interstitialRequestGeneration) return@post
                        interstitialLoading = false
                        scheduleInterstitialRetry(context)
                    }
                }
            }
        )
    }

    private fun scheduleInterstitialRetry(context: Context) {
        if (!appInForeground) return
        val delayMs = AdPolicy.retryDelayMs(interstitialRetryCount) ?: return
        interstitialRetryCount++
        val retry = Runnable {
            interstitialRetryRunnable = null
            loadInterstitial(context)
        }
        interstitialRetryRunnable = retry
        handler.postDelayed(retry, delayMs)
    }

    /** Cancels retries and discards a cached ad when the match form leaves the screen. */
    fun cancelPendingInterstitialPreload() {
        cancelInterstitialRetry()
        interstitialRequestGeneration++
        interstitialLoading = false
        interstitialRetryCount = 0
        interstitialAd?.destroy()
        interstitialAd = null
    }

    private fun cancelInterstitialRetry() {
        interstitialRetryRunnable?.let(handler::removeCallbacks)
        interstitialRetryRunnable = null
    }

    /** Shows only after a saved match, with a locally chosen 24-hour rolling frequency cap. */
    fun showInterstitial(activity: Activity): Boolean {
        if (activity.isFinishing || activity.isDestroyed) return false

        val preferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastShownAt = preferences.getLong(PREF_LAST_INTERSTITIAL, 0L)
        if (!AdPolicy.canShowInterstitial(
                canRequestAds = canRequestAds,
                sdkInitialized = sdkInitialized,
                appInForeground = appInForeground,
                alreadyShowing = interstitialShowing,
                adLoaded = interstitialAd != null,
                lastShownAt = lastShownAt,
                now = System.currentTimeMillis()
            )
        ) return false

        val ad = interstitialAd ?: return false
        interstitialAd = null
        interstitialShowing = true
        ad.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdShowedFullScreenContent() = Unit

            override fun onAdDismissedFullScreenContent() {
                handler.post {
                    interstitialShowing = false
                    ad.destroy()
                }
            }

            override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                handler.post {
                    interstitialShowing = false
                    ad.destroy()
                }
            }
        }

        return try {
            ad.show(activity)
            preferences.edit().putLong(PREF_LAST_INTERSTITIAL, System.currentTimeMillis()).apply()
            true
        } catch (_: RuntimeException) {
            interstitialShowing = false
            ad.destroy()
            false
        }
    }

    private fun discardInterstitial() {
        cancelInterstitialRetry()
        interstitialRequestGeneration++
        interstitialLoading = false
        interstitialRetryCount = 0
        interstitialAd?.destroy()
        interstitialAd = null
    }

    private fun isInterstitialCoolingDown(context: Context): Boolean {
        val lastShownAt = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(PREF_LAST_INTERSTITIAL, 0L)
        return AdPolicy.isCoolingDown(lastShownAt, System.currentTimeMillis())
    }
}
