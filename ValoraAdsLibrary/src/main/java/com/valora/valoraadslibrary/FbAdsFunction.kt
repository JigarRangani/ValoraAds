package com.valora.valoraadslibrary

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.facebook.ads.AdSettings
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.*

fun afterDelay(delayInTime: Long, listener: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        listener.invoke()
    }, delayInTime)
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beInVisible() {
    visibility = View.INVISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun Context.verifyInstallerId(): Boolean {
    if (BuildConfig.DEBUG)
        return true
    val validInstallers = listOf("com.android.vending", "com.google.android.feedback")
    return getInstallerPackageName() != null && validInstallers.contains(getInstallerPackageName())
}

fun Activity.loadFbRewardAd(adId: String, listener: (Boolean) -> Unit) {
    RewardedFbAd.getInstance().loadFbRewardAd(this, adId, listener, null)
}

fun Activity.showFbRewardAd(listener: (Boolean) -> Unit) {
    RewardedFbAd.getInstance().showFbRewardAd(this, listener)
}

fun Activity.isRewardAdLoaded(): Boolean {
    return RewardedFbAd.getInstance().isAdLoaded()

}

fun Context.showAdmobBanner(
    adId: String, bannerLayout: FrameLayout,
) {
    AdSettings.addTestDevice("cdbba8e2-7ced-49f5-8e8c-2de7d2dc5057");
    val bannerAdView = AdView(this, adId, AdSize.BANNER_HEIGHT_50)
    bannerAdView.let { nonNullBannerAdView ->
        bannerLayout.addView(nonNullBannerAdView)
        nonNullBannerAdView.loadAd(
            nonNullBannerAdView.buildLoadAdConfig().withAdListener(object : AdListener {
                override fun onError(p0: Ad?, p1: AdError?) {
                    Log.e("Banner", "Banner failed to load: " + p1?.errorMessage)
                    bannerLayout.visibility = View.GONE
                }

                override fun onAdLoaded(p0: Ad?) {
                    Log.e("Banner", "Banner loaded: ")
                }

                override fun onAdClicked(p0: Ad?) {
                    
                }

                override fun onLoggingImpression(p0: Ad?) {
                    
                }

            }).build()
        )
    }

}

@Suppress("DEPRECATION")
fun Context.getInstallerPackageName(): String? {
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return packageManager.getInstallSourceInfo(packageName).installingPackageName
        return packageManager.getInstallerPackageName(packageName)
    }
    return null
}
