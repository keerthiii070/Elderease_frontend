package com.elderease.app.ui.sos

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

object GpsEnableUtil {

    fun requestEnableGps(activity: Activity, onResult: (Boolean) -> Unit) {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(activity)

        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                onResult(true) // ✅ GPS already ON
            }
            .addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        e.startResolutionForResult(activity, 999)
                    } catch (_: IntentSender.SendIntentException) {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            }
    }
}
