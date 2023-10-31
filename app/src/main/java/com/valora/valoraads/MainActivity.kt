package com.valora.valoraads

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.valora.valoraads.databinding.CommanLoaderBinding
import com.valora.valoraadslibrary.AdMasterClass
import com.valora.valoraadslibrary.InterAdmobClass
import com.valora.valoraadslibrary.loadNativeAdmob
import com.valora.valoraadslibrary.setTestDevice
import com.valora.valoraadslibrary.showInterOnDemand
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
        setTestDevice("dbbb9f30-a99a-41c5-9bb0-c34e768beda9")
        LoadNative()
    }

    private fun LoadNative() {
        spinnerStart(this,"loading...")
        showInterOnDemand("CAROUSEL_IMG_SQUARE_APP_INSTALL#158551802111767_158553288778285",dialog,{
            Log.e("data","inter shown")

        },{
            Log.e("data","inter shown222")
        })

    }

    var dialog: android.app.AlertDialog? = null
    fun spinnerStart(context: Context?, text: String?) {
        try {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
            }
            val binding: CommanLoaderBinding =
                CommanLoaderBinding.inflate(LayoutInflater.from(context))
            binding.tvMsg.setText(text)
            Glide.with(context!!).asGif().load(R.raw.loading)
                .into(binding.imgLoader)
            dialog = android.app.AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setCancelable(false)
                .create()
            dialog!!.show()
        } catch (e: Exception) {
        }
    }


    fun spinnerStop() {
        if (dialog != null) {
            if (dialog!!.isShowing()) {
                try {
                    dialog!!.dismiss()
                } catch (e: java.lang.Exception) {
                }
            }
        }
    }


}