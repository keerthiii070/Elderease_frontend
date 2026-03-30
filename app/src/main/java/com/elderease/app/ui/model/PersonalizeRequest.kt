package com.elderease.app.ui.model

data class PersonalizeRequest(
    val email: String,
    val full_name: String,
    val age: Int,
    val conditions: String,
    val blood_group: String,
    val weight_kg: Double,
    val height_cm: Double
)
