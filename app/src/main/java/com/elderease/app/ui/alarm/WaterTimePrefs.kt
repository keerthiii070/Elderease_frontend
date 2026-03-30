package com.elderease.app.ui.alarm

import android.content.Context

object WaterTimePrefs {

    private const val PREF_NAME = "elder_ease_prefs"
    private const val KEY_WATER_TIMES = "water_times" // store like "14:00,16:00"

    fun saveWaterTimes(context: Context, times: List<String>) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_WATER_TIMES, times.joinToString(","))
            .apply()
    }

    fun getWaterTimes(context: Context): List<String> {
        val saved = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_WATER_TIMES, "") ?: ""

        if (saved.isBlank()) return emptyList()

        return saved.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
