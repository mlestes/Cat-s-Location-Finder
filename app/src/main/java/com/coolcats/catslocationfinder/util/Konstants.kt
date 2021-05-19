package com.coolcats.catslocationfinder.util

class Konstants {

    companion object {

        const val API_KEY = "AIzaSyASFeYMtwJ9O5t0Lc3VyjtLi18ZrUUlGYA"
        const val BASE_URL = "https://maps.googleapis.com/"
        const val GEO_PATH = "maps/api/geocode/json?"
        const val NEARBY_PATH = "maps/api/place/nearbysearch/json?"
        const val PHOTO_PATH = "maps/api/place/photo?"
        const val REQUEST_CODE = 600
        val LOC_TYPES = arrayListOf<String>(
            "",
            "Food",
            "Gym",
            "Shopping",
            "Government",
            "Medical",
            "Recreation",
            "Automotive",
            "Gas"
        )
    }
}