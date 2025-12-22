package com.luzian.adistance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.maplibre.android.geometry.LatLng

/**
 * Represents a single marker with a latitude and longitude.
 *
 * @property latLng The current position of the marker.
 * @constructor Creates a [Marker] at the given [latitude] and [longitude]
 */
class Marker(latitude: Double, longitude: Double) {
    var latLng by mutableStateOf(LatLng(latitude, longitude))
}

/**
 * Manages a list of [Marker] objects and a trash stack for undo functionality.
 *
 * @property onChange Callback invoked whenever the marker list changes.
 */
class Markers(private val onChange: () -> Unit) {
    private val markers = mutableStateListOf<Marker>()
    private val trash = mutableListOf<Marker>()
    private val trashLimit = 10

    operator fun iterator(): Iterator<Marker> {
        return markers.iterator()
    }

    fun add(latLng: LatLng) {
        markers.add(Marker(latLng.latitude, latLng.longitude))
        onChange()
    }

    fun removeLast() {
        if (markers.isNotEmpty()) {
            if (trash.size < trashLimit) {
                trash.add(markers.last())
            } else {
                trash.removeAt(0)
            }
            markers.removeAt(markers.size - 1)
            onChange()
        }
    }

    fun restoreLast() {
        if (trash.isNotEmpty()) {
            markers.add(trash.last())
            trash.removeAt(trash.size - 1)
            onChange()
        }
    }

    fun clear() {
        markers.removeAll(markers)
        trash.removeAll(trash)
        onChange()
    }

    fun move(marker: Marker, latLng: LatLng) {
        marker.latLng = latLng
        onChange()
    }

    fun last(): Marker {
        return if (markers.isNotEmpty()) {
            markers.last()
        } else {
            Marker(0.0, 0.0)
        }
    }

    fun toList(): List<LatLng> {
        return markers.map { it.latLng }
    }
}
