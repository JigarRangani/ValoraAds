package com.valora.valoraadslibrary

import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.ads.*
import java.util.ArrayList

class FBNativeAdAdapter private constructor(private val mParam: Param) : RecyclerViewAdapterWrapper(
    mParam.adapter
) {
    private val adItems: MutableList<NativeAd>

    init {
        adItems = ArrayList()
    }
    private fun assertConfig() {
        if (mParam.gridLayoutManager != null) {
            //if user set span ads
            val nCol = mParam.gridLayoutManager!!.spanCount
            require(mParam.adItemInterval % nCol == 0) {
                String.format(
                    "The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)",
                    mParam.adItemInterval,
                    nCol
                )
            }
        }
    }

    private fun convertAdPosition2OrgPosition(position: Int): Int {
        return position - (position + 1) / (mParam.adItemInterval + 1)
    }

    override fun getItemCount(): Int {
        val realCount = super.getItemCount()
        return realCount + realCount / mParam.adItemInterval
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else super.getItemViewType(
            convertAdPosition2OrgPosition(
                position
            )
        )
    }

    private fun isAdPosition(position: Int): Boolean {
        return (position + 1) % (mParam.adItemInterval + 1) == 0
    }

    private fun onBindAdViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val adHolder = holder as NativeListAdHolder

        val ad: NativeAd?

        if (adItems.size > position / DEFAULT_AD_ITEM_INTERVAL) {
            ad = adItems[position / DEFAULT_AD_ITEM_INTERVAL]
        } else {
            ad = mParam.adManageClass!!.nativeAdsManager!!.nextNativeAd()
            if (ad != null && !ad.isAdInvalidated) {
                adItems.add(ad)
            } else {
                Log.w(FBNativeAdAdapter::class.java.simpleName, "Ad is invalidated!")
            }
        }
        ad?.let { adHolder.setData2(it) }
       /* if (mParam.forceReloadAdOnBind || !adHolder.loaded) {
            val nativeAd = NativeAd(adHolder.context, mParam.facebookPlacementId)
            nativeAd.setAdListener(object : NativeAdListener {
                override fun onMediaDownloaded(ad: Ad) {}
                override fun onError(ad: Ad, adError: AdError) {
                    adHolder.nativeAdContainer.visibility = View.GONE
                }

                override fun onAdLoaded(ad: Ad) {
                    if (ad !== nativeAd) {
                        return
                    }
                    adHolder.nativeAdContainer.visibility = View.VISIBLE


                    // Set the Text.
                    // adHolder.nativeAdTitle.setText(nativeAd.getAdTitle());
                    adHolder.nativeAdTitle.text = nativeAd.advertiserName
                    adHolder.nativeAdSocialContext.text = nativeAd.adSocialContext
                    adHolder.nativeAdBody.text = nativeAd.adBodyText
                    adHolder.nativeAdCallToAction.text = nativeAd.adCallToAction

                    // Download and display the cover image.
//                    adHolder.nativeAdMedia.setNativeAd(nativeAd);

                    // Add the AdChoices icon
                    val adChoicesView = AdOptionsView(adHolder.context, nativeAd, null)
                    adHolder.adChoicesContainer.removeAllViews()
                    adHolder.adChoicesContainer.addView(adChoicesView)

                    // Register the Title and CTA button to listen for clicks.
                    adHolder.nativeAdMedia.addView(adHolder.nativeAdTitle)
                    adHolder.nativeAdMedia.addView(adHolder.nativeAdCallToAction)
                    nativeAd.registerViewForInteraction(
                        adHolder.nativeAdContainer,
                        adHolder.nativeAdMedia,
                        adHolder.nativeAdIcon
                    )
                    adHolder.loaded = true
                }

                override fun onAdClicked(ad: Ad) {}
                override fun onLoggingImpression(ad: Ad) {}
            })
            nativeAd.loadAd()
        }*/
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder,position)
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position))
        }
    }

    private fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater =  LayoutInflater.from(parent.context).inflate(R.layout.native_ad_unit, parent, false) as
                NativeAdLayout
        return NativeListAdHolder(inflater)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else super.onCreateViewHolder(parent, viewType)
    }

    private fun setSpanAds() {
        if (mParam.gridLayoutManager == null) {
            return
        }
        val spl = mParam.gridLayoutManager!!.spanSizeLookup
        mParam.gridLayoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isAdPosition(position)) {
                    spl.getSpanSize(position)
                } else 1
            }
        }
    }

    private class Param {
        var facebookPlacementId: String? = null
        var adapter: RecyclerView.Adapter<*>? = null
        var adManageClass:AdMasterClass? = null
        var adItemInterval = 0
        var forceReloadAdOnBind = false

        @LayoutRes
        var itemContainerLayoutRes = 0

        @IdRes
        var itemContainerId = 0
        var gridLayoutManager: GridLayoutManager? = null
    }

    class Builder private constructor(private val mParam: Param) {
        fun adItemInterval(interval: Int): Builder {
            mParam.adItemInterval = interval
            return this
        }
        fun adHelperClass(adMasterClass: AdMasterClass):Builder{
            mParam.adManageClass = adMasterClass
            return this
        }

        fun adLayout(@LayoutRes layoutContainerRes: Int, @IdRes itemContainerId: Int): Builder {
            mParam.itemContainerLayoutRes = layoutContainerRes
            mParam.itemContainerId = itemContainerId
            return this
        }

        fun build(): FBNativeAdAdapter {
            return FBNativeAdAdapter(mParam)
        }

        fun enableSpanRow(layoutManager: GridLayoutManager?): Builder {
            mParam.gridLayoutManager = layoutManager
            return this
        }

        fun forceReloadAdOnBind(forced: Boolean): Builder {
            mParam.forceReloadAdOnBind = forced
            return this
        }

        companion object {
            fun with(placementId: String?, wrapped: RecyclerView.Adapter<*>?): Builder {
                val param = Param()
                param.facebookPlacementId = placementId
                param.adapter = wrapped

                //default value
                param.adItemInterval = DEFAULT_AD_ITEM_INTERVAL
                param.forceReloadAdOnBind = true
                return Builder(param)
            }
        }
    }

   /* private class AdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var nativeAdIcon: ImageView
        var nativeAdTitle: TextView
        var nativeAdMedia: MediaView
        var nativeAdSocialContext: TextView
        var nativeAdBody: TextView
        var nativeAdCallToAction: Button
        var adChoicesContainer: LinearLayout
        var nativeAdContainer: LinearLayout
        var loaded: Boolean
        val context: Context
            get() = nativeAdContainer.context

        init {
            nativeAdContainer = view.findViewById(R.id.fb_native_ad_container)
            nativeAdIcon = view.findViewById(R.id.native_ad_icon)
            nativeAdTitle = view.findViewById(R.id.native_ad_title)
            nativeAdMedia = view.findViewById(R.id.native_ad_media)
            nativeAdSocialContext = view.findViewById(R.id.native_ad_social_context)
            nativeAdBody = view.findViewById(R.id.native_ad_body)
            nativeAdCallToAction = view.findViewById(R.id.native_ad_call_to_action)
            adChoicesContainer = view.findViewById(R.id.ad_choices_container)
            loaded = false
        }
    }*/

    companion object {
        private const val TYPE_FB_NATIVE_ADS = 900
        private const val DEFAULT_AD_ITEM_INTERVAL = 10
    }

    init {
        assertConfig()
        setSpanAds()
    }
}