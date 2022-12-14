package com.valora.valoraadslibrary

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.google.android.gms.ads.*

fun Context.showAdmobBanner(
    adId: String, bannerLayout: FrameLayout,
    successListener: (() -> Unit)? = null,
    failedListener: ((String) -> Unit)? = null
) {
    val adaptiveAds = AdaptiveAds(this)
    val adView = AdView(this)
    adView.adUnitId = adId
    bannerLayout.addView(adView)

    adView.adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.e("Admob_Banner", "onAdLoaded - Banner")
            successListener?.invoke()
        }

        override fun onAdFailedToLoad(p0: LoadAdError) {
            super.onAdFailedToLoad(p0)
            Log.e("Admob_Banner", "onAdFailedToLoad - $p0")
            failedListener?.invoke(p0.toString())
        }
    }
    val testDevices = ArrayList<String>()
    testDevices.add(AdRequest.DEVICE_ID_EMULATOR)

    val requestConfiguration = RequestConfiguration.Builder()
        .setTestDeviceIds(testDevices)
        .build()

    MobileAds.setRequestConfiguration(requestConfiguration)

    adView.setAdSize(adaptiveAds.adSize)
    adView.loadAd(AdRequest.Builder().build())
}