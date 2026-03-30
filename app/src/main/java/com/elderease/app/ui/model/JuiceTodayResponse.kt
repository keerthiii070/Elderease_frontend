package com.elderease.app.ui.model

data class JuiceTodayResponse(
    val status: Boolean,
    val todayTotal: Int,
    val dailyLimit: Int,
    val remaining: Int,
    val date: String
)
