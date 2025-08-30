package com.isaevapps.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationDataSource @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    private val request = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5_000L
    )
        .setMinUpdateIntervalMillis(5_000L)
        .setWaitForAccurateLocation(true)
        .build()

    @SuppressLint("MissingPermission")
    fun flow(): Flow<Result<Location, LocationError>> = callbackFlow {
        if (!isLocationEnabled()) {
            trySend(Result.Error(LocationError.NOT_AVAILABLE))
        }

        val cb = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(Result.Success(Location(it.latitude, it.longitude)))
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable && !isLocationEnabled()) {
                    trySend(Result.Error(LocationError.NOT_AVAILABLE))
                }
            }
        }
        client.requestLocationUpdates(request, cb, null)
        awaitClose { client.removeLocationUpdates(cb) }
    }

    fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(lm)
    }
}