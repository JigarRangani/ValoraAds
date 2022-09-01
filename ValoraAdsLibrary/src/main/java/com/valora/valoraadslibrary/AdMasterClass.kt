package com.valora.valoraadslibrary

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.facebook.ads.*

public class AdMasterClass:AdSdkInitListener {

    public var isFbInit = false
    public var isAdMobInit = false

    public var nativeAdsManager: NativeAdsManager? = null

    public var adItems: MutableList<NativeAd> = arrayListOf()

    fun updateAdItem(frequency:Int,position:Int):NativeAd{
        val ad: NativeAd?

        if (adItems.size > position / frequency) {
            ad = adItems[position / frequency]
        } else {
            ad = nativeAdsManager!!.nextNativeAd()
            if (ad != null && !ad.isAdInvalidated) {
                adItems.add(ad)
            } else {
                Log.w(AdMasterClass::class.java.simpleName, "Ad is invalidated!")
            }
        }
        return ad!!
    }

    public fun getInflatedView(parent:ViewGroup):NativeListAdHolder{
        val inflater =  LayoutInflater.from(parent.context).inflate(R.layout.native_ad_unit, parent, false) as
                NativeAdLayout
        return NativeListAdHolder(inflater)
    }

    public fun initTheAd(context: Context){
        AudienceNetworkInitializeHelper.initialize(context,this)
    }

    override fun fbInit(result: Boolean) {
        isFbInit = result
    }

    override fun adMobInit(result: Boolean) {
        isAdMobInit = result
    }

    fun setNativeAdapter(context: Context,id:String,noAds:Int,nativeAdLoadListener: NativeAdLoadListener){
        AdSettings.addTestDevice("cdbba8e2-7ced-49f5-8e8c-2de7d2dc5057");
        nativeAdsManager = NativeAdsManager(context, id, noAds)
        nativeAdsManager!!.loadAds()
        nativeAdsManager!!.setListener(object : NativeAdsManager.Listener {
            override fun onAdsLoaded() {
                Log.e("Native Ad","Native Ad Loaded SuccessFully")
                nativeAdLoadListener.onNativeLoaded()
            }

            override fun onAdError(p0: AdError?) {
                nativeAdLoadListener.onNativeLoadError()
                Log.e("Native Ad","Native Ad Load Error = ${p0!!.errorMessage}")
            }

        })
    }

}