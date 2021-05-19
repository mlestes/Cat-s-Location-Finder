package com.coolcats.catslocationfinder.util

import android.location.Location
import android.location.LocationListener

class LocListener(private val locationDelegate: LocationDelegate): LocationListener {

    interface LocationDelegate {
        fun provideLocation(location: Location)
    }

    override fun onLocationChanged(location: Location) {
        locationDelegate.provideLocation(location)
    }
}