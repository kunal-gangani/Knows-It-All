package com.example.know_it_all.util

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Earth's radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.asin(Math.sqrt(a))
    return R * c
}

fun formatDistance(distanceInKm: Double): String {
    return when {
        distanceInKm < 1.0 -> "${(distanceInKm * 1000).toInt()}m"
        else -> String.format("%.1f km", distanceInKm)
    }
}
