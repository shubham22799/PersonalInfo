package com.example.personalinfo.utility

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.location.*
//import com.google.android.gms.common.api.GoogleApiClient
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.LocationSettingsRequest
//import com.google.android.gms.location.LocationSettingsStatusCodes
import java.util.*


object SingleShotLocationProvider {

    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(context: Context, callback: LocationCallback) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isNetworkEnabled) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.e("TAGA", "permission required")
//                // TODO: Consider calling
//                //    Activity#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for Activity#requestPermissions for more details.
//                return
//            }

            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    callback.onNewLocationAvailable(
                        GPSCoordinates(
                            location.latitude,
                            location.longitude
                        )
                    )
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }, null)
        } else {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGPSEnabled) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback.onNewLocationAvailable(
                            GPSCoordinates(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }, null)
            }
        }
    }


    interface LocationCallback {
        fun onNewLocationAvailable(location: GPSCoordinates?)
    }

    // consider returning Location instead of this dummy wrapper class
    class GPSCoordinates {
        var longitude = -1f
        var latitude = -1f

        constructor(theLatitude: Float, theLongitude: Float) {
            longitude = theLongitude
            latitude = theLatitude
        }

        constructor(theLatitude: Double, theLongitude: Double) {
            longitude = theLongitude.toFloat()
            latitude = theLatitude.toFloat()
        }
    }


    fun getAddress(context: Context, latitude: Float, longitude: Float): String {
        val addresses: List<Address>
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude.toDouble(),
            longitude.toDouble(),
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address: String =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city: String = addresses[0].locality
        val state: String = addresses[0].adminArea
        val country: String = addresses[0].countryName
        val postalCode: String = addresses[0].postalCode
        val knownName: String = addresses[0].featureName
        return address
    }

//    fun enableLocationSettings(context: Context): Boolean {
//        var gps_enabled = false
//        var network_enabled = false
//        val isGPSDialogShown = booleanArrayOf(false)
//        val REQUEST_GPS = 2
//        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        try {
//            //assert(locationManager != null)
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        } catch (e: java.lang.Exception) {
//        }
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        } catch (e: java.lang.Exception) {
//        }
//        return if (!gps_enabled && !network_enabled) {
//            if (!isGPSDialogShown[0]) {
//                val googleApiClient = GoogleApiClient.Builder(context)
//                    .addApi(LocationServices.API)
//                    .build()
//                googleApiClient.connect()
//                val locationRequest = LocationRequest.create()
//                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//                val builder = LocationSettingsRequest.Builder()
//                    .addLocationRequest(locationRequest)
//                builder.setAlwaysShow(true)
//                val result =
//                    LocationServices.SettingsApi.checkLocationSettings(
//                        googleApiClient,
//                        builder.build()
//                    )
//                result.setResultCallback { _result ->
//                    val status = _result.status
//                    val state = _result.locationSettingsStates
//                    when (status.statusCode) {
//                        LocationSettingsStatusCodes.SUCCESS -> {
//                            Log.e("TAGA", "Ok Click")
//                        }
//                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
//                            status.startResolutionForResult(
//                                context as Activity, REQUEST_GPS
//                            )
//                            isGPSDialogShown[0] = true
//                        } catch (e: java.lang.Exception) {
//                        }
//                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                        }
//                    }
//                }
//            }
//            false
//        } else {
//            true
//        }
//    }

}
