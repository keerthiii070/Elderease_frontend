package com.elderease.app.ui.sos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class PowerButtonReceiver(
    private val onTriplePress: () -> Unit
) : BroadcastReceiver() {

    private var pressCount = 0
    private var lastPressTime = 0L

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_SCREEN_ON &&
            intent?.action != Intent.ACTION_SCREEN_OFF
        ) return

        val now = SystemClock.elapsedRealtime()

        if (now - lastPressTime > 2000) pressCount = 0

        pressCount++
        lastPressTime = now

        if (pressCount >= 2) {
            pressCount = 0
            onTriplePress()
        }
    }
}
