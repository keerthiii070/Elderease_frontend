package com.elderease.app.ui.model

data class DietAiResponse(
    val status: Boolean,
    val reply: String? = null,
    val message: String? = null,
    val remaining_today: Int? = null
)
