package com.elderease.app.ui.model

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val name: String?
)
