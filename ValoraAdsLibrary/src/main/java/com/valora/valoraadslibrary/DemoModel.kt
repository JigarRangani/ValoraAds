package com.valora.valoraadslibrary

import android.content.Context
import android.widget.Toast

public class DemoModel {

    fun s(c: Context?, message: String?) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    }
}