package com.futurion.apps.mindmingle

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdManager(
    private val context: Context,
    private val adUnitId: String
) {
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun loadAd(onAdLoaded: (() -> Unit)? = null, onAdFailed: (() -> Unit)? = null) {
        if (isLoading || rewardedAd != null) {
            // Already loading or loaded
            return
        }

        isLoading = true
        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("RewardedAdManager", "Rewarded ad loaded.")
                    rewardedAd = ad
                    isLoading = false
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("RewardedAdManager", "Failed to load rewarded ad: ${error.message}")
                    rewardedAd = null
                    isLoading = false
                    onAdFailed?.invoke()
                }
            }
        )
    }

    fun showAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: (() -> Unit)? = null
    ) {
        if (rewardedAd == null) {
            Log.w("RewardedAdManager", "Rewarded ad not loaded yet.")
            onAdDismissed?.invoke()
            return
        }

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("RewardedAdManager", "Rewarded ad dismissed.")
                rewardedAd = null // Ad consumed, needs reload
                onAdDismissed?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e("RewardedAdManager", "Failed to show rewarded ad: ${error.message}")
                rewardedAd = null
                onAdDismissed?.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("RewardedAdManager", "Rewarded ad showed fullscreen content.")
            }
        }

        rewardedAd?.show(activity, OnUserEarnedRewardListener {
            Log.d("RewardedAdManager", "User earned reward.")
            onUserEarnedReward()
        })
    }
}
