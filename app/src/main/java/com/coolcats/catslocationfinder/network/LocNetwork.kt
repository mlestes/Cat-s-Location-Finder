package com.coolcats.catslocationfinder.network

import android.graphics.Bitmap
import android.media.Image
import com.coolcats.catslocationfinder.model.GeocodeResult
import com.coolcats.catslocationfinder.model.NearbyResult
import com.coolcats.catslocationfinder.util.Konstants.Companion.API_KEY
import com.coolcats.catslocationfinder.util.Konstants.Companion.BASE_URL
import com.coolcats.catslocationfinder.util.Konstants.Companion.GEO_PATH
import com.coolcats.catslocationfinder.util.Konstants.Companion.NEARBY_PATH
import com.coolcats.catslocationfinder.util.Konstants.Companion.PHOTO_PATH
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class LocNetwork {

    private val locationNetwork = createRetrofit().create(LocationEndPoint::class.java)

    fun createRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    suspend fun getAddressAsync(latlng: String): Deferred<GeocodeResult> {
        return locationNetwork.getAddress(latlng, API_KEY)
    }

    suspend fun getNearbyLocationsAsync(
        type: String,
        location: String,
        radius: Int
    ): Deferred<NearbyResult> {
        return locationNetwork.getNearbyLocations(type, location, radius, API_KEY)
    }

    suspend fun getPhotoAsync(
        ref: String,
        height: Int
    ): Deferred<Bitmap> {
        return locationNetwork.getPlacePhoto(ref, height, API_KEY)
    }

    interface LocationEndPoint {
        @GET(GEO_PATH)
        fun getAddress(
            @Query("latlng") latlng: String,
            @Query("key") key: String
        ): Deferred<GeocodeResult>

        @GET(NEARBY_PATH)
        fun getNearbyLocations(
            @Query("keyword") type: String,
            @Query("location") location: String,
            @Query("radius") radius: Int,
            @Query("key") key: String
        ): Deferred<NearbyResult>

        @GET(PHOTO_PATH)
        fun getPlacePhoto(
            @Query("photoreference") ref: String,
            @Query("maxheight") height: Int,
            @Query("key") key: String
        ): Deferred<Bitmap>
    }

}