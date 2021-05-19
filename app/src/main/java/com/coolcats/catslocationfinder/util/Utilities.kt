package com.coolcats.catslocationfinder.util

import android.location.Location
import android.util.Log

class Utilities {

    companion object {
        fun myLog(message: String) = Log.d("MY_TAG", message)
        fun Location.toFormatString(): String = "${this.latitude},${this.longitude}"
    }
}
