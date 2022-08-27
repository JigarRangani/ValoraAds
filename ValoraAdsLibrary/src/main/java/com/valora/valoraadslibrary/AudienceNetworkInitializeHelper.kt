package com.valora.valoraadslibrary

import android.content.Context
import android.util.Log
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.AudienceNetworkAds.InitListener
import com.facebook.ads.AudienceNetworkAds.InitResult
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener


class AudienceNetworkInitializeHelper : InitListener {
    override fun onInitialized(result: InitResult) {
        Log.d(AudienceNetworkAds.TAG, result.message)
        adSdkInitListener.fbInit(true)
    }

    companion object {
        /**
         * It's recommended to call this method from Application.onCreate().
         * Otherwise you can call it from all Activity.onCreate()
         * methods for Activities that contain ads.
         *
         * @param context Application or Activity.
         */
        lateinit var adSdkInitListener: AdSdkInitListener
        fun initialize(context: Context,adSdkInitListener: AdSdkInitListener) {
            this.adSdkInitListener = adSdkInitListener
            if (!AudienceNetworkAds.isInitialized(context)) {
                /*  if (Buil) {
                AdSettings.turnOnSDKDebugger(context);
            }*/
                AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(AudienceNetworkInitializeHelper())
                    .initialize()
            }

            MobileAds.initialize(context,
                OnInitializationCompleteListener {
                    adSdkInitListener.adMobInit(true)
                })
        }


    }
}