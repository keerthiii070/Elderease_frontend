package com.elderease.app.ui.sos

import android.content.Context
import android.widget.Toast

class SosManager(private val context: Context) {

    fun triggerSOSCallOnly() {
        val emergencyNumber = EmergencyNumberUtil.getEmergencyNumber(context)
        Toast.makeText(context, "Opening Emergency Dialer: $emergencyNumber", Toast.LENGTH_LONG).show()
        CallUtil.openDialer(context, emergencyNumber)
    }
}
