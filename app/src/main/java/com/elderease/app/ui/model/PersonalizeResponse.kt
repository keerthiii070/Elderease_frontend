package com.elderease.app.ui.model

data class PersonalizeResponse(
    val status: Boolean,
    val message: String,
    val bmi: Double? = null
)
