package com.valora.valoraadslibrary

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.ads.*
import java.util.ArrayList

class NativeListAdHolder(internal var nativeAdLayout: NativeAdLayout) :
    RecyclerView.ViewHolder(nativeAdLayout) {

    fun setData(adMasterClass: AdMasterClass,frequency:Int,position:Int,context: Context) {
       val  ad = adMasterClass.updateAdItem(frequency, position)
         var mvAdMedia: MediaView = nativeAdLayout.findViewById(R.id.native_ad_media)
         var ivAdIcon: MediaView = nativeAdLayout.findViewById(R.id.native_ad_icon)
         var tvAdTitle: TextView = nativeAdLayout.findViewById(R.id.native_ad_title)
         var tvAdBody: TextView = nativeAdLayout.findViewById(R.id.native_ad_body)
         var tvAdSocialContext: TextView =
            nativeAdLayout.findViewById(R.id.native_ad_social_context)
        var tvAdSponsoredLabel: TextView =
            nativeAdLayout.findViewById(R.id.native_ad_sponsored_label)
        var btnAdCallToAction: Button =
            nativeAdLayout.findViewById(R.id.native_ad_call_to_action)
         var adChoicesContainer: LinearLayout =
            nativeAdLayout.findViewById(R.id.ad_choices_container)
        adChoicesContainer.removeAllViews()

        ad.let { nonNullAd ->
            tvAdTitle.text = nonNullAd.advertiserName
            tvAdBody.text = nonNullAd.adBodyText
            tvAdSocialContext.text = nonNullAd.adSocialContext
            tvAdSponsoredLabel.setText(R.string.sponsored)
            btnAdCallToAction.text = nonNullAd.adCallToAction
            btnAdCallToAction.visibility =
                if (nonNullAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
            val adOptionsView = AdOptionsView(context, nonNullAd, nativeAdLayout)
            adChoicesContainer.addView(adOptionsView, 0)

            val clickableViews = ArrayList<View>()
            clickableViews.add(ivAdIcon)
            clickableViews.add(mvAdMedia)
            clickableViews.add(btnAdCallToAction)
            nonNullAd.registerViewForInteraction(
                nativeAdLayout, mvAdMedia, ivAdIcon, clickableViews)
        }

    }

    fun setData2(ad:NativeAd){
        var mvAdMedia: MediaView = nativeAdLayout.findViewById(R.id.native_ad_media)
        var ivAdIcon: MediaView = nativeAdLayout.findViewById(R.id.native_ad_icon)
        var tvAdTitle: TextView = nativeAdLayout.findViewById(R.id.native_ad_title)
        var tvAdBody: TextView = nativeAdLayout.findViewById(R.id.native_ad_body)
        var tvAdSocialContext: TextView =
            nativeAdLayout.findViewById(R.id.native_ad_social_context)
        var tvAdSponsoredLabel: TextView =
            nativeAdLayout.findViewById(R.id.native_ad_sponsored_label)
        var btnAdCallToAction: Button =
            nativeAdLayout.findViewById(R.id.native_ad_call_to_action)
        var adChoicesContainer: LinearLayout =
            nativeAdLayout.findViewById(R.id.ad_choices_container)
        adChoicesContainer.removeAllViews()

        ad.let { nonNullAd ->
            tvAdTitle.text = nonNullAd.advertiserName
            tvAdBody.text = nonNullAd.adBodyText
            tvAdSocialContext.text = nonNullAd.adSocialContext
            tvAdSponsoredLabel.setText(R.string.sponsored)
            btnAdCallToAction.text = nonNullAd.adCallToAction
            btnAdCallToAction.visibility =
                if (nonNullAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
            val adOptionsView = AdOptionsView(mvAdMedia.context, nonNullAd, nativeAdLayout)
            adChoicesContainer.addView(adOptionsView, 0)

            val clickableViews = ArrayList<View>()
            clickableViews.add(ivAdIcon)
            clickableViews.add(mvAdMedia)
            clickableViews.add(btnAdCallToAction)
            nonNullAd.registerViewForInteraction(
                nativeAdLayout, mvAdMedia, ivAdIcon, clickableViews)
        }
    }

}