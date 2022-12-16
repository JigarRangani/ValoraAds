package com.valora.valoraadslibrary

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener


open class FbInterClass {

    companion object {
        const val TAG = "FB_Inter"

        @Volatile
        private var instance: FbInterClass? = null

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
                instance ?: FbInterClass().also { instance = it }
            }
    }

    private var admobInterAd: InterstitialAd? = null
    private var adFailedCounter = 0
    private var isAdLoaded = false
    var listenerImpMain: (() -> Unit)? = null

    lateinit var listener: (Boolean) -> Unit
    lateinit var loadListener: (Boolean) -> Unit

    private fun isAdLoaded() = (isAdLoaded && (admobInterAd != null))

    var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
        override fun onInterstitialDisplayed(ad: Ad) {
            // Interstitial ad displayed callback
            isAdLoaded = false
            isInterstitialShown = false
            listener.invoke(false)
            Log.e(TAG, "Interstitial ad displayed.")
        }

        override fun onInterstitialDismissed(ad: Ad) {
            // Interstitial dismissed callback
            Log.e(TAG, "Interstitial ad dismissed.")
            admobInterAd = null
            isAdLoaded = false
            isInterstitialShown = false
            listener.invoke(true)
            listenerImpMain?.invoke()
        }

        override fun onError(ad: Ad?, adError: AdError) {
            // Ad error callback
            Log.e(TAG, "Interstitial ad failed to load: " + adError.errorMessage)
            Log.e(TAG, "onAdFailedToLoad - $ad")
            admobInterAd = null
            isAdLoaded = false
            isInterstitialShown = false

            loadListener.invoke(false)
        }

        override fun onAdLoaded(ad: Ad) {
            // Interstitial ad is loaded and ready to be displayed
            Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!")
            // Show the ad
            isAdLoaded = true
            isInterstitialShown = false
            Log.e(TAG, "Loaded")
            loadListener.invoke(true)
        }

        override fun onAdClicked(ad: Ad) {
            // Ad clicked callback
            isInterstitialShown = true
            Log.d(TAG, "Interstitial ad clicked!")
        }

        override fun onLoggingImpression(ad: Ad) {
            // Ad impression logged callback
            isInterstitialShown = true
            Log.d(TAG, "Interstitial ad impression logged!")
        }
    }

    fun loadInterstitialAd(
        context: Context,
        adInterId: String,
        listener: (Boolean) -> Unit
    ) {
        this.loadListener = listener
        admobInterAd = InterstitialAd(context, adInterId)
        admobInterAd?.let {
            it.loadAd(
                it.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build()
            );
        }

    }

    fun showInterstitialAd(
        activity: Activity,
        listener: (Boolean) -> Unit,
        listenerImp: (() -> Unit)? = null
    ) {
        this.listenerImpMain = listenerImp
        this.listener = listener
        if (isAdLoaded) {
            admobInterAd?.show()
        } else {
            activity.runOnUiThread { listener.invoke(false) }
        }
    }

    fun loadAndShowInter(
        activity: Activity,
        adInterId: String,
        dialog: Dialog? = null,
        listener: () -> Unit,
        listenerImp: (() -> Unit)? = null
    ) {
        var isTimeUp = false
        var isAdShow = false
        afterDelay(waitingTimeForAd) {
            if (!activity.isDestroyed && !activity.isFinishing)
                if (dialog?.isShowing == true) {
                    try {
                        dialog.dismiss()
                    } catch (e: IllegalArgumentException) {
                        // Do nothing.
                    } catch (e: Exception) {
                        // Do nothing.
                    }
                }
            isTimeUp = true
            if (!isAdShow)
                activity.runOnUiThread { listener.invoke() }
        }
        Log.e(TAG, "isAdLoaded ${isAdLoaded()}")
        if (isAdLoaded()) {
            Log.e(TAG, "Already Loaded")
            if (!activity.isDestroyed && !activity.isFinishing)
                if (dialog?.isShowing == true) {
                    try {
                        dialog.dismiss()
                    } catch (e: IllegalArgumentException) {
                        // Do nothing.
                    } catch (e: Exception) {
                        // Do nothing.
                    }
                }
            if (!isTimeUp)
                showInterstitialAd(activity, {
                    isAdShow = true
                    activity.runOnUiThread { listener.invoke() }
                },listenerImp)
        } else {
            dialog?.show()
            loadInterstitialAd(activity, adInterId) {
                Log.e(TAG, "Load Ad")
                if (!activity.isDestroyed && !activity.isFinishing)
                    if (dialog?.isShowing == true) {
                        try {
                            dialog.dismiss()
                        } catch (e: IllegalArgumentException) {
                            // Do nothing.
                        } catch (e: Exception) {
                            // Do nothing.
                        }
                    }
                if (!isTimeUp)
                    showInterstitialAd(activity, {
                        Log.e(TAG, "isAdShown $it")
                        activity.runOnUiThread { listener.invoke() }
                    })
            }
        }
    }


}