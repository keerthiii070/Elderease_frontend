package com.elderease.app.ui.model

data class WaterRequest(
    val email: String,
    val amount_ml: Int
)

data class SetGoalRequest(
    val email: String,
    val daily_target: Int
)

data class WaterTodayResponse(
    val status: Boolean,
    val todayTotal: Int,
    val dailyTarget: Int
)

data class WaterActionResponse(
    val status: Boolean,
    val message: String,
    val added: Int? = null,
    val todayTotal: Int,
    val dailyGoal: Int,
    val remaining: Int,
    val time: String? = null
)
