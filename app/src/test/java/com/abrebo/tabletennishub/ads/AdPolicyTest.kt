package com.abrebo.tabletennishub.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdPolicyTest {
    @Test
    fun interstitialRequiresConsentInitializationForegroundAndLoadedAd() {
        val allowed = AdPolicy.canShowInterstitial(
            canRequestAds = true,
            sdkInitialized = true,
            appInForeground = true,
            alreadyShowing = false,
            adLoaded = true,
            lastShownAt = 0L,
            now = 1L
        )
        assertTrue(allowed)

        assertFalse(canShow(canRequestAds = false))
        assertFalse(canShow(sdkInitialized = false))
        assertFalse(canShow(appInForeground = false))
        assertFalse(canShow(alreadyShowing = true))
        assertFalse(canShow(adLoaded = false))
    }

    @Test
    fun interstitialCooldownIsRollingAndHandlesClockMovingBackwards() {
        val lastShownAt = 1_000_000L
        assertTrue(AdPolicy.isCoolingDown(lastShownAt, lastShownAt))
        assertTrue(
            AdPolicy.isCoolingDown(
                lastShownAt,
                lastShownAt + AdPolicy.INTERSTITIAL_COOLDOWN_MS - 1L
            )
        )
        assertFalse(
            AdPolicy.isCoolingDown(
                lastShownAt,
                lastShownAt + AdPolicy.INTERSTITIAL_COOLDOWN_MS
            )
        )
        assertTrue(AdPolicy.isCoolingDown(lastShownAt, lastShownAt - 1L))
        assertFalse(AdPolicy.isCoolingDown(0L, lastShownAt))
    }

    @Test
    fun interstitialRetriesAreDelayedAndFinite() {
        assertEquals(5_000L, AdPolicy.retryDelayMs(0))
        assertEquals(30_000L, AdPolicy.retryDelayMs(1))
        assertEquals(120_000L, AdPolicy.retryDelayMs(2))
        assertEquals(null, AdPolicy.retryDelayMs(3))
    }

    private fun canShow(
        canRequestAds: Boolean = true,
        sdkInitialized: Boolean = true,
        appInForeground: Boolean = true,
        alreadyShowing: Boolean = false,
        adLoaded: Boolean = true
    ): Boolean = AdPolicy.canShowInterstitial(
        canRequestAds = canRequestAds,
        sdkInitialized = sdkInitialized,
        appInForeground = appInForeground,
        alreadyShowing = alreadyShowing,
        adLoaded = adLoaded,
        lastShownAt = 0L,
        now = 1L
    )
}
