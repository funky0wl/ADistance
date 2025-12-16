package com.luzian.adistance

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.google.android.gms.location.*
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.maps.Style
import org.ramani.compose.*

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
class MainActivity : ComponentActivity() {

    private var locationPermGranted by mutableStateOf(false)
    val locationPropertiesState: MutableState<LocationRequestProperties?> =
        mutableStateOf(LocationRequestProperties(interval = 5000, fastestInterval = 5000))


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermissions()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {

            }
        }

        setContent {
            MainScreen()
        }
    }

    @Suppress("MissingPermission")
    private fun requestLocationPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locationPermGranted = true
                    locationPropertiesState.value = LocationRequestProperties(interval = 5000, fastestInterval = 5000)
                    startLocationUpdates()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    locationPermGranted = true
                    locationPropertiesState.value = LocationRequestProperties(interval = 5000, fastestInterval = 5000)
                    startLocationUpdates()
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            mainLooper
        )
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    @Composable
    private fun MainScreen() {
        val styleUrl = remember { "asset://style.json" }
        val styleBuilder = remember { Style.Builder().fromUri(styleUrl) }
        val symbolCenter = remember { mutableStateOf(LatLng(46.0, 4.8)) }

        val cameraMode = rememberSaveable { mutableIntStateOf(CameraMode.TRACKING) }
        val locationProperties = remember { locationPropertiesState }

        val cameraPosition = remember {
            mutableStateOf(
                CameraPosition(
                    target = LatLng(46.0, 4.8),
                    zoom = 14.0,
                )
            )
        }

        val locationStyling = remember {
            LocationStyling(
                enablePulse = false,
                bearingTintColor = Color.BLUE
            )
        }

        key(locationPermGranted) {
            MapLibre(
                modifier = Modifier.fillMaxSize(),
                styleBuilder = styleBuilder,
                cameraPosition = cameraPosition.value,
                cameraMode = cameraMode,
                locationRequestProperties = locationProperties.value!!,
                locationStyling = locationStyling
            ) {
                Symbol(
                    center = symbolCenter.value,
                    isDraggable = true,
                    onSymbolDragged = { center -> symbolCenter.value = center }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
