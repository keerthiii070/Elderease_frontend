package com.elderease.app.ui.model

data class SignupRequest(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val age: String,
    val password: String,
    val confirmPassword: String
)
