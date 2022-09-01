package com.valora.valoraads

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.valora.valoraadslibrary.AdMasterClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AdMasterClass().initTheAd(this)
    }
}