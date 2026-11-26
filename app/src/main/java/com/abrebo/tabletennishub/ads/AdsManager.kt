package com.abrebo.tabletennishub.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import com.abrebo.tabletennishub.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/** Centralizes consent gating, ad unit selection, loading and conservative frequency limits. */
object AdsManager {
    private const val PREFS_NAME = "advertising"
    private const val PREF_LAST_INTERSTITIAL = "last_interstitial_shown_at"
    private const val MIN_INTERSTITIAL_INTERVAL_MS = 24L * 60L * 60L * 1000L
    private const val MAX_INTERSTITIAL_LOAD_RETRIES = 3

    private const val RELEASE_BANNER_ID = "ca-app-pub-4667560937795938/1898700671"
    private const val RELEASE_INTERSTITIAL_ID = "ca-app-pub-4667560937795938/6959455665"
    private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/9214589741"
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    private val handler = Handler(Looper.getMainLooper())
    private var appContext: Context? = null
    private val initializationCallbacks = mutableListOf<() -> Unit>()
    private var consentInformation: ConsentInformation? = null
    private var sdkInitialized = false
    private var sdkInitializing = false
    private var canRequestAds = false
    private var appInForeground = false
    private var interstitialAd: InterstitialAd? = null
    private var interstitialLoading = false
    private var interstitialShowing = false
    private var interstitialRetryCount = 0
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

    /** Refreshes UMP consent on each authenticated app entry, then initializes the ads SDK once. */
    fun updateConsent(activity: Activity, onComplete: () -> Unit) {
        val info = UserMessagingPlatform.getConsentInformation(activity.applicationContext)
        appContext = activity.applicationContext
        consentInformation = info
        val params = ConsentRequestParameters.Builder().build()
        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    finishConsent(info, onComplete)
                }
            },
            {
                // UMP retains the last valid consent decision when this network update fails.
                finishConsent(info, onComplete)
            }
        )
    }

    private fun finishConsent(info: ConsentInformation, onComplete: () -> Unit) {
        canRequestAds = info.canRequestAds()
        if (!canRequestAds) {
            onComplete()
            return
        }
        if (sdkInitialized) {
            onComplete()
            return
        }
        if (sdkInitializing) {
            initializationCallbacks += onComplete
            return
        }

        sdkInitializing = true
        initializationCallbacks += onComplete
        MobileAds.initialize(appContext ?: return) {
            sdkInitializing = false
            sdkInitialized = true
            initializationCallbacks.toList().also { initializationCallbacks.clear() }.forEach { it() }
        }
    }

    fun showPrivacyOptions(activity: Activity, onComplete: () -> Unit = {}) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            canRequestAds = consentInformation?.canRequestAds() == true
            if (!canRequestAds) {
                interstitialAd = null
                interstitialRetryRunnable?.let(handler::removeCallbacks)
                interstitialRetryRunnable = null
            }
            onComplete()
        }
    }

    fun setForeground(isForeground: Boolean) {
        appInForeground = isForeground
        if (!isForeground) {
            interstitialRetryRunnable?.let(handler::removeCallbacks)
            interstitialRetryRunnable = null
        }
    }

    /** Adds at most one anchored adaptive banner to the dedicated, reserved container. */
    fun loadBanner(activity: Activity, container: FrameLayout): AdView? {
        if (!canRequestAds || !sdkInitialized || container.childCount > 0) return null

        val adView = AdView(activity).apply {
            adUnitId = bannerUnitId
            setAdSize(AdSize.BANNER)
        }
        container.addView(
            adView,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        adView.loadAd(AdRequest.Builder().build())
        return adView
    }

    /** Called only when a user enters the match form; requests never originate in view rebuilds. */
    fun preloadInterstitial(context: Context) {
        if (!canRequestAds || !sdkInitialized || !appInForeground || interstitialAd != null || interstitialLoading) return
        if (isInterstitialCoolingDown(context)) return
        interstitialRetryRunnable?.let(handler::removeCallbacks)
        interstitialRetryRunnable = null
        interstitialLoading = true
        InterstitialAd.load(
            context.applicationContext,
            interstitialUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialLoading = false
                    interstitialRetryCount = 0
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialLoading = false
                    interstitialAd = null
                    scheduleInterstitialRetry(context.applicationContext)
                }
            }
        )
    }

    private fun scheduleInterstitialRetry(context: Context) {
        if (!appInForeground || interstitialRetryCount >= MAX_INTERSTITIAL_LOAD_RETRIES) return
        val delayMs = when (interstitialRetryCount++) {
            0 -> 5_000L
            1 -> 30_000L
            else -> 120_000L
        }
        val retry = Runnable {
            interstitialRetryRunnable = null
            preloadInterstitial(context)
        }
        interstitialRetryRunnable = retry
        handler.postDelayed(retry, delayMs)
    }

    /** Shows only after a successful match save/update, at most once in a rolling 24-hour window. */
    fun showInterstitial(activity: Activity): Boolean {
        if (!canRequestAds || !sdkInitialized || !appInForeground || interstitialShowing) return false
        if (isInterstitialCoolingDown(activity)) return false
        val preferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val ad = interstitialAd ?: return false
        interstitialAd = null
        interstitialShowing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                preferences.edit().putLong(PREF_LAST_INTERSTITIAL, System.currentTimeMillis()).apply()
            }

            override fun onAdDismissedFullScreenContent() {
                interstitialShowing = false
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialShowing = false
            }
        }
        ad.show(activity)
        return true
    }

    private fun isInterstitialCoolingDown(context: Context): Boolean {
        val previousShownAt = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(PREF_LAST_INTERSTITIAL, 0L)
        if (previousShownAt <= 0L) return false
        val now = System.currentTimeMillis()
        return now < previousShownAt || now - previousShownAt < MIN_INTERSTITIAL_INTERVAL_MS
    }
}
