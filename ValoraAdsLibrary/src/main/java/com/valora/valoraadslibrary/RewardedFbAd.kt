package com.valora.valoraadslibrary

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import com.facebook.ads.*


class RewardedFbAd {
    companion object {
        const val TAG = "Fb_Reward"

        @Volatile
        private var instance: RewardedFbAd? = null

        @JvmStatic
        var waitingTimeForAd = 8000L

        @JvmStatic
        var isInterstitialShown = false

        @JvmStatic
        var adFailedAttempts = 3

        @JvmStatic
        var adLoadAuto = false

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RewardedFbAd().also { instance = it }
            }

    }


    var adListener: (Boolean) -> Unit? = {}
    private var fbRewardedAd: RewardedInterstitialAd? = null
    private var adFailedCounter = 0
    private var isAdLoaded = false

    fun isAdLoaded() = (isAdLoaded && (fbRewardedAd != null))
    fun setDeviceId(){
        AdSettings.addTestDevice("cdbba8e2-7ced-49f5-8e8c-2de7d2dc5057");
    }

    fun loadFbRewardAd(
        activity: Activity,
        adInterId: String,
        listener: (Boolean) -> Unit,
        listenerImp: (() -> Unit)? = null
    ) {
        fbRewardedAd?.destroy()
        fbRewardedAd = null
        fbRewardedAd = RewardedInterstitialAd(activity, adInterId)
        val rewardedInterstitialAdListener: RewardedInterstitialAdListener =
            object : RewardedInterstitialAdListener {
                override fun onError(ad: Ad?, error: AdError) {
                    // Rewarded interstitial ad failed to load
                    fbRewardedAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    adFailedCounter++
                    if (adFailedCounter < adFailedAttempts) {
                        afterDelay(waitingTimeForAd) {
                            loadFbRewardAd(activity, adInterId, listener, listenerImp)
                        }
                    }
                    listener.invoke(false)
                    Log.e(
                        TAG,
                        "Rewarded interstitial ad failed to load: " + error.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    isAdLoaded = true
                    isInterstitialShown = false
                    Log.e(TAG, "Loaded")
                    listener.invoke(true)
                    // Rewarded interstitial ad is loaded and ready to be displayed
                    Log.d(TAG, "Rewarded interstitial ad is loaded and ready to be displayed!")
                }

                override fun onAdClicked(ad: Ad) {
                    // Rewarded interstitial ad clicked
                    Log.d(TAG, "Rewarded interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Rewarded Interstitial ad impression - the event will fire when the
                    // interstitial starts playing
                    isInterstitialShown = true
                    listenerImp?.invoke()
                    Log.d(TAG, "Rewarded interstitial ad impression logged!")
                }

                override fun onRewardedInterstitialCompleted() {
                    // Rewarded Interstitial View Complete - the interstitial has been played to the end.
                    // You can use this event to initialize your reward
                    Log.d(TAG, "Rewarded interstitial completed!")
                    activity.runOnUiThread { adListener.invoke(true) }
                    // Call method to give reward
                    // giveReward();
                }

                override fun onRewardedInterstitialClosed() {
                    // The Rewarded Interstitial ad was closed - this can occur during the interstitial
                    // by closing the app, or closing the end card.
                    fbRewardedAd = null
                    isAdLoaded = false
                    isInterstitialShown = false
                    if (adLoadAuto) {
                        loadFbRewardAd(activity, adInterId,listener,listenerImp)
                    }
                    activity.runOnUiThread { adListener.invoke(false) }
                    activity.runOnUiThread { listener.invoke(true) }
                    Log.d(TAG, "Rewarded interstitial ad closed!")
                }
            }
        fbRewardedAd!!.loadAd(
            fbRewardedAd!!.buildLoadAdConfig()
                .withAdListener(rewardedInterstitialAdListener)
                .build()
        )
    }

    fun showFbRewardAd(
        activity: Activity,
        listener: (Boolean) -> Unit,

    ) {
        adListener = listener
        if (isAdLoaded) {
            fbRewardedAd?.show(
                fbRewardedAd!!.buildShowAdConfig()
                    .withAppOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .build());
        } else {
            activity.runOnUiThread { listener.invoke(false) }
        }
    }

}