package com.coolcats.catslocationfinder.view.activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.coolcats.catslocationfinder.R
import com.coolcats.catslocationfinder.util.Konstants.Companion.API_KEY
import com.coolcats.catslocationfinder.util.Konstants.Companion.LOC_TYPES
import com.coolcats.catslocationfinder.util.Konstants.Companion.REQUEST_CODE
import com.coolcats.catslocationfinder.util.LocListener
import com.coolcats.catslocationfinder.util.Status
import com.coolcats.catslocationfinder.util.Utilities
import com.coolcats.catslocationfinder.view.adapter.LocAdapter
import com.coolcats.catslocationfinder.viewmodel.LocViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    LocAdapter.LocDelegate {

    private val viewModel: LocViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private lateinit var userLocation: Location
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the SDK
        Places.initialize(applicationContext, API_KEY)

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ArrayAdapter.createFromResource(
            this,
            R.array.loc_types,
            android.R.layout.simple_spinner_item
        )
            .also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                type_spinner.adapter = it
            }
        type_spinner.onItemSelectedListener = this

        val locAdapter = LocAdapter(this, placesClient)
        result_recyclerview.adapter = locAdapter

        viewModel.geoData.observe(this, {
            user_location_txt.text = it.formatted_address
        })
        viewModel.nearbyData.observe(this, {
            locAdapter.updateList(it)
        })
        viewModel.statusData.observe(this, {
            checkStatus(it)
        })
    }

    private fun checkStatus(status: Status?) {
        when (status) {
            Status.LOADING -> progress_indicator.visibility = View.VISIBLE
            Status.SUCCESS -> progress_indicator.visibility = View.GONE
            else -> {
                progress_indicator.visibility = View.GONE
                Snackbar.make(main_view, "An Error Occurred...", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("NewApi")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE)
            if (permissions[0] == ACCESS_FINE_LOCATION)
                if (grantResults[0] == PERMISSION_GRANTED)
                    registerListener()
                else {
                    if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                        requestPermission()
                    } else {
                        AlertDialog.Builder(
                            ContextThemeWrapper(this, R.style.ThemeOverlay_AppCompat)
                        )
                            .setTitle("Permission Needed!")
                            .setMessage("Location permissions are required in order to use this application. If you refuse to grant this permission, then uninstall the application.")
                            .setPositiveButton("Open Settings") { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.data = Uri.fromParts("package", packageName, null)
                                startActivity(intent)
                            }.create()
                            .show()
                    }
                }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            registerListener()
        else
            requestPermission()
    }

    @SuppressLint("MissingPermission")
    private fun registerListener() {
        locationManager.requestLocationUpdates(
            //Using NETWORK_PROVIDER since testing on physical device
            LocationManager.NETWORK_PROVIDER,
            1000L,
            5f,
            myLocListener
        )
    }

    private val myLocListener = LocListener(
        object : LocListener.LocationDelegate {
            override fun provideLocation(location: Location) {
                getLocation(location)
            }
        }
    )

    private fun getLocation(location: Location) {
        viewModel.getMyLocation(location)
        userLocation = location
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Utilities.myLog("Getting ${LOC_TYPES[position]}")
        if (this::userLocation.isInitialized)
            if (LOC_TYPES[position].isNotEmpty())
                viewModel.getNearbyPlaces(LOC_TYPES[position], userLocation, 10000)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        /* Do Nothing */
    }

    override fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}