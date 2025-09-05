package com.futurion.apps.mindmingle

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.LoadAdError

class GoogleRewardedAdManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null

    fun loadRewardedAd(adUnitId: String, onLoaded: (Boolean) -> Unit) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                onLoaded(true)
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
                onLoaded(false)
            }
        })
    }

    fun showRewardedAd(activity: Activity, onUserEarnedReward: () -> Unit, onClosed: () -> Unit) {
        rewardedAd?.show(activity) { rewardItem ->
            onUserEarnedReward()
            onClosed()
        }
        rewardedAd = null // Clear reference after showing
    }


}

