package com.luzian.adistance

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luzian.adistance.ui.theme.ADistanceTheme
import com.luzian.adistance.location.RequestLocationPermissions
import com.luzian.adistance.ui.CenteredDialog
import com.luzian.adistance.ui.IconButton
import com.luzian.adistance.ui.InputBar
import com.luzian.adistance.ui.InputField
import com.luzian.adistance.ui.Sidebar
import com.luzian.adistance.ui.TopBar
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.Polyline
import org.ramani.compose.Symbol
import java.util.Locale

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
class MainActivity : ComponentActivity() {

    private val distanceShort = mutableStateOf("")
    private val distanceLong = mutableStateOf("")

    private val map = Map(
        onUserLocationUpdate = {
            updateDistance()
        }
    )

    private val markers = Markers(
        onChange = {
            updateDistance()
    })

    private fun updateDistance() {
        val distance = (calcDistance(listOf(LatLng(map.userLocation.value.latitude, map.userLocation.value.longitude)) + markers.toList()) / 1000)
        distanceShort.value = String.format(Locale.US, "%.2f", distance)
        distanceLong.value = String.format(Locale.US, "%.6f", distance)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            RequestLocationPermissions {
                map.locationPermGranted = true
            }

            ADistanceTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        Box(modifier = Modifier.fillMaxSize()) {
            map(
                Modifier.fillMaxSize(),
                onMapClick = {
                    markers.add(it)
                },
            ) {

                //because of layering issues implement own location indicator
                Circle(
                    center = LatLng(
                        map.userLocation.value.latitude,
                        map.userLocation.value.longitude
                    ),
                    radius = 8f,
                    color = "#4285F4",
                    borderColor = "white",
                    borderWidth = 3f
                )

                Polyline(
                    points = listOf(LatLng(map.userLocation.value.latitude, map.userLocation.value.longitude)) + markers.toList(),
                    color = "black",
                    lineWidth = 4F
                )

                for (marker in markers) {
                    Symbol(
                        center = marker.latLng,
                        isDraggable = true,
                        onSymbolDragged = { center -> markers.move(marker, center) }
                    )
                }
            }

            var showInfoPopup by remember { mutableStateOf(false) }

            TopBar {
                IconButton(
                    icon = Icons.Default.Info,
                    contentDescription = "Info",
                    onClick = {
                        showInfoPopup = true
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                Text(text = distanceShort.value.ifEmpty { "0.00" } + " km")
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showInfoPopup) {
                CenteredDialog(
                    onDismissRequest = { showInfoPopup = false }
                )  {
                    Text("Distance Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current Location:", fontWeight = FontWeight.Bold)
                    Text("Lat: " + map.userLocation.value.latitude)
                    Text("Lon: " + map.userLocation.value.longitude)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Last Entered Location:", fontWeight = FontWeight.Bold)
                    Text("Lat: " + markers.last().latLng.latitude)
                    Text("Lon: " + markers.last().latLng.longitude)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Distance:", fontWeight = FontWeight.Bold)
                    Text(distanceLong.value.ifEmpty { "0.000000" } + " km")
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { showInfoPopup = false }) {
                        Text("Ok")
                    }
                }
            }

            val latitude = remember { mutableStateOf(String()) }
            val longitude = remember { mutableStateOf(String()) }
            val context = LocalContext.current

            InputBar(
                onSend = {
                    //TODO: move out
                    try {
                        val lat = latitude.value.replace(",", ".").toDouble()
                        val long = longitude.value.replace(",", ".").toDouble()
                        if (!(lat <= 90 && lat >= -90)) throw IllegalArgumentException()
                        if (!(long <= 180 && long >= -180)) throw IllegalArgumentException()
                        markers.add(LatLng(lat, long))
                        latitude.value = String()
                        longitude.value = String()
                    } catch(_: Exception) {
                        Toast.makeText(context, "Invalid format used.", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                InputField(
                    value = latitude.value,
                    onValueChange = {
                        latitude.value = it
                    },
                    placeholderText = "Latitude"
                )

                InputField(
                    value = longitude.value,
                    onValueChange = {
                        longitude.value = it
                    },
                    placeholderText = "Longitude"
                )
            }

            Sidebar {
                IconButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    onClick = {
                        markers.removeLast()
                    }
                )

                IconButton(
                    icon = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    onClick = {
                        markers.restoreLast()
                    }
                )

                IconButton(
                    icon = Icons.Default.DeleteForever,
                    contentDescription = "Delete",
                    onClick = {
                        markers.clear()
                    }
                )

                IconButton(
                    icon = Icons.Default.ZoomIn,
                    contentDescription = "ZoomIn",
                    onClick = {
                        map.cameraPosition.value = CameraPosition(map.cameraPosition.value).apply {
                            this.zoom = this.zoom?.plus(2.0)
                        }
                    }
                )

                IconButton(
                    icon = Icons.Default.ZoomOut,
                    contentDescription = "ZoomOut",
                    onClick = {
                        map.cameraPosition.value = CameraPosition(map.cameraPosition.value).apply {
                            this.zoom = this.zoom?.minus(2.0)
                        }
                    }
                )

                IconButton(
                    icon = Icons.Default.MyLocation,
                    contentDescription = "MyLocation",
                    onClick = {
                        map.cameraPosition.value = CameraPosition(map.cameraPosition.value).apply {
                            this.target = LatLng(
                                map.userLocation.value.latitude,
                                map.userLocation.value.longitude
                            )
                            this.zoom = 10.0
                        }
                    }
                )

            }
        }
    }
}