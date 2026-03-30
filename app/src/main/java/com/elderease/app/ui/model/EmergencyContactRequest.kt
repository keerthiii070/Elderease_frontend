package com.elderease.app.ui.model

data class EmergencyContactRequest(
    val user_email: String,
    val contact_name: String,
    val relationship: String,
    val contact_phone: String,
    val contact_email: String,
    val contact_age: Int
)
