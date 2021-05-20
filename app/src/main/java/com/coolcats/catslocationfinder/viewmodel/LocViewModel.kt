package com.coolcats.catslocationfinder.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolcats.catslocationfinder.model.Result
import com.coolcats.catslocationfinder.model.ResultX
import com.coolcats.catslocationfinder.network.LocNetwork
import com.coolcats.catslocationfinder.util.Status
import com.coolcats.catslocationfinder.util.Utilities.Companion.myLog
import com.coolcats.catslocationfinder.util.Utilities.Companion.toFormatString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocViewModel : ViewModel() {

    val statusData = MutableLiveData<Status>()
    val nearbyData: MutableLiveData<List<ResultX>> = MutableLiveData()
    val geoData = MutableLiveData<Result>()
    private val network = LocNetwork()
    private var job: Job? = null

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }

    fun getMyLocation(location: Location) {
        statusData.value = Status.LOADING
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = network.getAddressAsync(location.toFormatString()).await()
                geoData.postValue(result.results[0])
                statusData.postValue(Status.SUCCESS)

            } catch (e: Exception) {
                myLog(e.localizedMessage)
                statusData.postValue(Status.ERROR)

            }
        }

    }

    fun getNearbyPlaces(type: String, location: Location, radius: Int) {
        statusData.value = Status.LOADING
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val result =
                    network.getNearbyLocationsAsync(type, location.toFormatString(), radius).await()
                nearbyData.postValue(result.results)
                statusData.postValue(Status.SUCCESS)
            } catch (e: java.lang.Exception) {
                myLog(e.localizedMessage)
                statusData.postValue(Status.ERROR)
            }
        }
    }

}