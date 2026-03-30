package com.elderease.app.ui.sos

import android.content.Context
import android.telephony.TelephonyManager

object EmergencyNumberUtil {

    fun getEmergencyNumber(context: Context): String {
        return "112" // or 911 depending on country
    }
}
