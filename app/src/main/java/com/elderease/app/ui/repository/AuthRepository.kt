package com.elderease.app.ui.repository

import com.elderease.app.ui.model.LoginRequest
import com.elderease.app.ui.model.SignupRequest
import com.elderease.app.ui.network.ApiClient

class AuthRepository {

    /* -----------------------------
       LOGIN
    ------------------------------ */
    suspend fun login(
        email: String,
        password: String
    ) =
        ApiClient.api.login(
            LoginRequest(
                email = email,
                password = password
            )
        )

    /* -----------------------------
       SIGNUP
    ------------------------------ */
    suspend fun signup(
        fullName: String,
        email: String,
        phoneNumber: String,
        age: String,
        password: String,
        confirmPassword: String
    ) =
        ApiClient.api.signup(
            SignupRequest(
                fullName = fullName,
                email = email,
                phoneNumber = phoneNumber,
                age = age,
                password = password,
                confirmPassword = confirmPassword
            )
        )
}
