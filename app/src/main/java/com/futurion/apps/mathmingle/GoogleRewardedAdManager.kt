package com.futurion.apps.mathmingle

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem

class GoogleRewardedAdManager(
    private val context: Context,
    private val adUnitId: String
) {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    private var pendingShow: (() -> Unit)? = null

    init {
        // Initialize Mobile Ads SDK
        MobileAds.initialize(context) {}
        // Preload first ad
        loadRewardedAd()
    }

    private fun loadRewardedAd(onLoaded: ((Boolean) -> Unit)? = null) {
        if (isLoading || rewardedAd != null) {
            onLoaded?.invoke(rewardedAd != null)
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("GoogleRewardedAdManager", "Rewarded Ad Loaded")
                rewardedAd = ad
                isLoading = false
                onLoaded?.invoke(true)

                // If show was requested while loading, show it now
                pendingShow?.invoke()
                pendingShow = null
            }

            override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
                Log.e("GoogleRewardedAdManager", "Failed to load Ad: ${adError.message}")
                rewardedAd = null
                isLoading = false
                onLoaded?.invoke(false)
            }
        })
    }

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onClosed: () -> Unit
    ) {
        if (rewardedAd != null) {
            showLoadedAd(activity, onUserEarnedReward, onClosed)
        } else {
            // Ad not ready yet → load and show once ready
            pendingShow = { showLoadedAd(activity, onUserEarnedReward, onClosed) }
            loadRewardedAd()
        }
    }

    private fun showLoadedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onClosed: () -> Unit
    ) {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("GoogleRewardedAdManager", "Ad dismissed")
                rewardedAd = null
                loadRewardedAd()
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("GoogleRewardedAdManager", "Failed to show ad: ${adError.message}")
                rewardedAd = null
                loadRewardedAd()
                onClosed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("GoogleRewardedAdManager", "Ad showed fullscreen")
            }
        }

        rewardedAd?.show(activity) { rewardItem: RewardItem ->
            Log.d("GoogleRewardedAdManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            onUserEarnedReward()
        }

        rewardedAd = null
    }
}
