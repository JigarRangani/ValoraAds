package com.valora.valoraads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.valora.valoraadslibrary.AdMasterClass
import com.valora.valoraadslibrary.InterAdmobClass
import com.valora.valoraadslibrary.loadNativeAdmob
import java.util.Arrays

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AdMasterClass().initTheAd(this)

        MobileAds.initialize(this) {

        }


        //Admob Interstitial
        InterAdmobClass.adLoadAuto = true
        InterAdmobClass.adFailedAttempts = 3

        LoadNative()
    }

    private fun LoadNative() {


        loadNativeAdmob(
            findViewById(R.id.nativeLayout),
            /*sharedPreferenceManager.remoteConfigData.nativeHome*/
            "ca-app-pub-3940256099942544/2247696110",
            com.valora.valoraadslibrary.R.layout.custom_ad_large,
            {
                Log.e("Home Native", "show on ")
            },
            {
                Log.e("Home Native", "show on2 " + it)
            })
    }


}