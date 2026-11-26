package com.abrebo.tabletennishub.ads

/** Pure ad frequency and retry rules, kept independent of SDK and Android lifecycle state. */
internal object AdPolicy {
    const val INTERSTITIAL_COOLDOWN_MS = 24L * 60L * 60L * 1000L
    private val retryDelaysMs = longArrayOf(5_000L, 30_000L, 120_000L)

    fun canShowInterstitial(
        canRequestAds: Boolean,
        sdkInitialized: Boolean,
        appInForeground: Boolean,
        alreadyShowing: Boolean,
        adLoaded: Boolean,
        lastShownAt: Long,
        now: Long
    ): Boolean = canRequestAds &&
        sdkInitialized &&
        appInForeground &&
        !alreadyShowing &&
        adLoaded &&
        !isCoolingDown(lastShownAt, now)

    fun isCoolingDown(lastShownAt: Long, now: Long): Boolean {
        if (lastShownAt <= 0L) return false
        if (now < lastShownAt) return true
        return now - lastShownAt < INTERSTITIAL_COOLDOWN_MS
    }

    fun retryDelayMs(attempt: Int): Long? = retryDelaysMs.getOrNull(attempt)
}
