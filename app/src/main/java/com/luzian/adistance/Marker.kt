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

    /**
     * Allows iterating over the current list of markers.
     */
    operator fun iterator(): Iterator<Marker> {
        return markers.iterator()
    }


    /**
     * Adds a new marker at the specified position.
     *
     * @param latLng The coordinates for the new marker.
     */
    fun add(latLng: LatLng) {
        markers.add(Marker(latLng.latitude, latLng.longitude))
        onChange()
    }

    /**
     * Removes the last added marker and stores it in the trash for potential restoration.
     *
     * The trash has a limit of [trashLimit] items. If the limit is reached, the oldest
     * item in the trash is discarded.
     */
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

    /**
     * Restores the most recently removed marker from the trash.
     */
    fun restoreLast() {
        if (trash.isNotEmpty()) {
            markers.add(trash.last())
            trash.removeAt(trash.size - 1)
            onChange()
        }
    }

    /**
     * Removes all markers and clears the trash.
     */
    fun clear() {
        markers.removeAll(markers)
        trash.removeAll(trash)
        onChange()
    }

    /**
     * Updates the position of an existing marker.
     *
     * @param marker The marker to move.
     * @param latLng The new coordinates.
     */
    fun move(marker: Marker, latLng: LatLng) {
        marker.latLng = latLng
        onChange()
    }

    /**
     * Returns the last marker in the list.
     *
     * @return The last [Marker], or a dummy marker at (0.0, 0.0) if the list is empty.
     */
    fun last(): Marker {
        return if (markers.isNotEmpty()) {
            markers.last()
        } else {
            Marker(0.0, 0.0)
        }
    }

    /**
     * Returns a list of coordinates for all current markers.
     */
    fun toList(): List<LatLng> {
        return markers.map { it.latLng }
    }
}
