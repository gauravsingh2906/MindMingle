package com.futurion.apps.mathmingle

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class GoogleRewardedAdManager(
    private val context: Context,
    private val adUnitId: String
) {

    // ✅ Queue to store preloaded ads (keep 2 for backup)
    private val adQueue: Queue<RewardedAd> = LinkedList()
    private var isLoading = false
    private var pendingShow: (() -> Unit)? = null

    init {
        MobileAds.initialize(context) {}
        preloadAds() // Preload 2 ads immediately
    }

    // -----------------------------
    // 🔹 Public Methods
    // -----------------------------

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onClosed: () -> Unit
    ) {
        // Check real internet connection before showing ad
//        if (!isInternetAvailable(context)) {
//            Toast.makeText(
//                context,
//                "No working internet connection. Please check your connection or data plan.",
//                Toast.LENGTH_SHORT
//            ).show()
//            onClosed()
//            return
//        }

        // If an ad is ready, show it immediately
        val ad = adQueue.poll()
        if (ad != null) {
            showLoadedAd(activity, ad, onUserEarnedReward, onClosed)
            preloadAds() // Ensure at least 2 ads are always preloaded
        } else {
            // Show loading dialog while trying to load a new ad
            val loadingDialog = createLoadingDialog(activity)
            loadingDialog.show()
            pendingShow = {
                showRewardedAd(activity, onUserEarnedReward, onClosed)
            }

            preloadAds { success ->
                loadingDialog.dismiss()
                if (!success) {
                    Toast.makeText(context, "Currently no ad is available. Please try again later.", Toast.LENGTH_SHORT).show()
                    onClosed()
                }
            }
        }
    }

    // -----------------------------
    // 🔹 Private Methods
    // -----------------------------

    // Preload ads until queue has 2 ads
    private fun preloadAds(onLoaded: ((Boolean) -> Unit)? = null) {
        if (isLoading) {
            onLoaded?.invoke(adQueue.isNotEmpty())
            return
        }

        if (adQueue.size >= 2) {
            onLoaded?.invoke(true)
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("GoogleRewardedAdManager", "Rewarded Ad Loaded")
                adQueue.add(ad)
                isLoading = false
                onLoaded?.invoke(true)

                // If user was waiting to show ad
                pendingShow?.invoke()
                pendingShow = null

                // Preload next if queue < 2
                if (adQueue.size < 2) {
                    preloadAds()
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(
                    "GoogleRewardedAdManager",
                    "Failed to load Ad: ${adError.code} - ${adError.message}"
                )
                isLoading = false
                onLoaded?.invoke(false)

                // Retry loading after 5 seconds
                Handler(Looper.getMainLooper()).postDelayed({ preloadAds() }, 5000)
            }
        })
    }

    // Show a loaded ad
    private fun showLoadedAd(
        activity: Activity,
        ad: RewardedAd,
        onUserEarnedReward: () -> Unit,
        onClosed: () -> Unit
    ) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("GoogleRewardedAdManager", "Ad dismissed")
                onClosed()
                preloadAds()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e("GoogleRewardedAdManager", "Failed to show ad: ${adError.message}")
                Toast.makeText(context,
                    "Failed to show ad. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
                onClosed()
                preloadAds()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("GoogleRewardedAdManager", "Ad showed fullscreen")
            }
        }

        ad.show(activity) { rewardItem: RewardItem ->
            Log.d("GoogleRewardedAdManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            onUserEarnedReward()
        }
    }

    // Simple loading dialog
    private fun createLoadingDialog(activity: Activity): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val progressBar = ProgressBar(activity)
        builder.setView(progressBar)
        builder.setCancelable(false)
        return builder.create()
    }

    // -----------------------------
    // 🔹 Internet Check
    // -----------------------------
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val connected = networkInfo != null && networkInfo.isConnected
        if (!connected) return false

        // Real connection check
        return try {
            val urlc = URL("https://www.google.com").openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Android")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1500
            urlc.connect()
            urlc.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
}
