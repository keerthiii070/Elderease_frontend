package com.elderease.app.ui.sos

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val onTripleShake: () -> Unit
) : SensorEventListener {

    private var shakeCount = 0
    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH

        if (gForce > 2.7) {
            val now = System.currentTimeMillis()

            if (now - lastShakeTime > 1500) shakeCount = 0

            shakeCount++
            lastShakeTime = now

            if (shakeCount >= 3) {
                shakeCount = 0
                onTripleShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
