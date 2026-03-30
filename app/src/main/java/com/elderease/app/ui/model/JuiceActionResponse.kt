package com.elderease.app.ui.model

data class JuiceActionResponse(
    val status: Boolean,
    val message: String,
    val added: Int? = null,
    val removed: Int? = null,
    val todayTotal: Int,
    val dailyLimit: Int? = null,
    val remaining: Int? = null,
    val time: String? = null
)
