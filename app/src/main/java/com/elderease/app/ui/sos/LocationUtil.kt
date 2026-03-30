package com.elderease.app.ui.sos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

object LocationUtil {

    suspend fun getGoogleMapsLink(context: Context): String {
        val fine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            return "Location permission not granted"
        }

        return try {
            val fused = LocationServices.getFusedLocationProviderClient(context)

            // ✅ Use lastLocation first (works even if GPS is off sometimes)
            val last = fused.lastLocation.await()

            if (last != null) {
                "https://maps.google.com/?q=${last.latitude},${last.longitude}"
            } else {
                "Location not available"
            }

        } catch (e: Exception) {
            "Location error"
        }
    }
}
