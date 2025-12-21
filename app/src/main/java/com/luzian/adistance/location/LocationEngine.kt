package com.luzian.adistance.location

import android.app.PendingIntent
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.maplibre.android.location.engine.LocationEngine
import org.maplibre.android.location.engine.LocationEngineCallback
import org.maplibre.android.location.engine.LocationEngineRequest
import org.maplibre.android.location.engine.LocationEngineResult

/**
 * MapLibre Location Engine
 *
 * Implements the Google Play FusedLocationProviderClient
 */
class LocationEngine(context: Context) : LocationEngine {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val listeners = mutableMapOf<LocationEngineCallback<LocationEngineResult>, LocationCallback>()

    override fun getLastLocation(callback: LocationEngineCallback<LocationEngineResult>) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        callback.onSuccess(LocationEngineResult.create(location))
                    } else {
                        callback.onFailure(Exception("Last location unavailable"))
                    }
                }
                .addOnFailureListener { e ->
                    callback.onFailure(e)
                }
        } catch (e: SecurityException) {
            callback.onFailure(e)
        }
    }

    override fun requestLocationUpdates(
        request: LocationEngineRequest,
        callback: LocationEngineCallback<LocationEngineResult>,
        looper: Looper?
    ) {
        val priority = when (request.priority) {
            LocationEngineRequest.PRIORITY_HIGH_ACCURACY -> Priority.PRIORITY_HIGH_ACCURACY
            LocationEngineRequest.PRIORITY_BALANCED_POWER_ACCURACY -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            LocationEngineRequest.PRIORITY_LOW_POWER -> Priority.PRIORITY_LOW_POWER
            LocationEngineRequest.PRIORITY_NO_POWER -> Priority.PRIORITY_PASSIVE
            else -> Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationRequest = LocationRequest.Builder(priority, request.interval)
            .setMinUpdateIntervalMillis(request.fastestInterval)
            .setMaxUpdateDelayMillis(request.maxWaitTime)
            .setMinUpdateDistanceMeters(5f)
            .setMaxUpdateDelayMillis(15)
            .build()

        val listener = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val lastLocation = result.lastLocation
                if (lastLocation != null) {
                    callback.onSuccess(LocationEngineResult.create(lastLocation))
                }
            }
        }

        listeners[callback] = listener

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                listener,
                looper ?: Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            callback.onFailure(e)
        }
    }

    override fun requestLocationUpdates(
        request: LocationEngineRequest,
        pendingIntent: PendingIntent?
    ) {
        //Not yet implemented
    }

    override fun removeLocationUpdates(callback: LocationEngineCallback<LocationEngineResult>) {
        val listener = listeners.remove(callback)
        if (listener != null) {
            fusedLocationClient.removeLocationUpdates(listener)
        }
    }

    override fun removeLocationUpdates(pendingIntent: PendingIntent?) {
        //Not yet implemented
    }
}