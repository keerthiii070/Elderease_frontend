package com.elderease.app.ui.alarm

import android.content.Context

object ReminderPrefs {

    private const val PREFS = "elder_ease_prefs"

    // Water
    private const val KEY_WATER_INTERVAL_HOURS = "water_interval_hours"

    // Juice
    private const val KEY_JUICE_HOUR = "juice_hour"
    private const val KEY_JUICE_MINUTE = "juice_minute"

    fun saveWaterInterval(context: Context, hours: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_WATER_INTERVAL_HOURS, hours)
            .apply()
    }

    fun getWaterInterval(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_WATER_INTERVAL_HOURS, 2) // default 2 hours
    }

    fun saveJuiceTime(context: Context, hour: Int, minute: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_JUICE_HOUR, hour)
            .putInt(KEY_JUICE_MINUTE, minute)
            .apply()
    }

    fun getJuiceHour(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_JUICE_HOUR, 10) // default 10 AM
    }

    fun getJuiceMinute(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_JUICE_MINUTE, 30) // default 30
    }
}
