package com.valora.valoraadslibrary

import android.content.Context

public class AdMasterClass:AdSdkInitListener {

    public var isFbInit = false
    public var isAdMobInit = false

    public fun initTheAd(context: Context){
        AudienceNetworkInitializeHelper.initialize(context,this)
    }

    override fun fbInit(result: Boolean) {
        isFbInit = result
    }

    override fun adMobInit(result: Boolean) {
        isAdMobInit = result
    }
}