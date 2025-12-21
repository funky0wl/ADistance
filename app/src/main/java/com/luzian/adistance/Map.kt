package com.luzian.adistance

import android.graphics.Color
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.luzian.adistance.location.LocationEngine
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationRequestProperties
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import org.ramani.compose.MapLibreComposable
import org.ramani.compose.Margins
import org.ramani.compose.UiSettings

/**
 * Manages a MapLibre map instance.
 *
 * Handles user location, camera position, ui settings and map click events
 */
@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
class Map(private val onUserLocationUpdate: () -> Unit) {
    private val styleUrl = "asset://style.json"
    var locationPermGranted by mutableStateOf(false)
    val userLocation = mutableStateOf(Location(null))
    val cameraMode = mutableIntStateOf(CameraMode.NONE)

    var cameraPosition = mutableStateOf(
        CameraPosition(
            zoom = 10.0,
            target = LatLng(userLocation.value.latitude, userLocation.value.longitude)
        )
    )

    /**
     * Composable function that displays a MapLibre map.
     *
     * @param modifier Modifier to apply to the MapLibre composable.
     * @param onMapClick Callback invoked with the LatLng when the user clicks on the map.
     * @param content MapLibreComposable content to overlay on the map.
     */
    @Composable
    operator fun invoke(
        modifier: Modifier,
        onMapClick: (LatLng) -> Unit = {},
        content: (@Composable @MapLibreComposable () -> Unit)? = null
    ) {
        val context = LocalContext.current
        val locationEngine = remember(context) { LocationEngine(context) }

        val styleBuilder = remember { Style.Builder().fromUri(styleUrl) }
        val locationProperties = remember { LocationRequestProperties(interval = 5000, fastestInterval = 5000) }

        val locationStyling = remember {
            LocationStyling(
                enablePulse = false,
                bearingTintColor = Color.TRANSPARENT,
                foregroundTintColor = Color.TRANSPARENT,
                backgroundTintColor = Color.TRANSPARENT,
                accuracyAlpha = 0f
            )
        }

        val uiSettings = remember {
            UiSettings(
                compassMargins = Margins(0, 400, 30, 0)
            )
        }

        LaunchedEffect(userLocation.value) {
            if (userLocation.value.latitude != 0.0 && userLocation.value.longitude != 0.0) {
                onUserLocationUpdate()

                cameraPosition.value = CameraPosition(cameraPosition.value).apply {
                    this.target = LatLng(userLocation.value.latitude, userLocation.value.longitude)
                }
            }
        }

        key(locationPermGranted) {
            MapLibre(
                modifier = modifier,
                styleBuilder = styleBuilder,
                uiSettings = uiSettings,
                cameraPosition = cameraPosition.value,
                cameraMode = cameraMode,
                locationRequestProperties = locationProperties,
                locationStyling = locationStyling,
                locationEngine = locationEngine as LocationEngine?,
                renderMode = RenderMode.GPS,
                userLocation = userLocation,
                onMapClick = onMapClick,
                content = content
            )
        }

    }

}