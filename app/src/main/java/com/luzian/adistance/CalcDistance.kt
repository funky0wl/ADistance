package com.luzian.adistance

import org.maplibre.android.geometry.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * gCalculates the Haversine distance between two points.
 *
 * @return Distance between the two points in meters.
 */
fun calcDistance(a: LatLng, b: LatLng): Double {
    val earthRadius = 6371000.0

    val lat1 = Math.toRadians(a.latitude)
    val lat2 = Math.toRadians(b.latitude)
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)

    val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(h), sqrt(1 - h))
    return earthRadius * c
}

/**
 * Calculates the total distance of a path defined by a list of points.
 *
 * @return Total distance of the path in meters.
 */
fun calcDistance(points: List<LatLng>): Double {
    if (points.size < 2) return 0.0
    return points.zipWithNext().sumOf { (a, b) -> calcDistance(a, b) }
}