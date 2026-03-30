package com.elderease.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.network.ApiClient
import com.elderease.app.ui.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 🔵 NORMAL LOGIN
                val response = repository.login(email, password)

                if (response.status) {
                    onSuccess(response.name ?: "User")
                } else {
                    // 🔴 CHECK FOR SOFT-DELETED ACCOUNT
                    if (response.message.contains("deactivated", ignoreCase = true)) {
                        restoreAccount(email, password, onSuccess, onError)
                    } else {
                        onError(response.message)
                    }
                }

            } catch (e: HttpException) {
                Log.e("LOGIN_API", "HTTP error", e)
                onError("Server error (${e.code()})")

            } catch (e: IOException) {
                Log.e("LOGIN_API", "Network error", e)
                onError("Unable to connect to server")

            } catch (e: Exception) {
                Log.e("LOGIN_API", "Unknown error", e)
                onError(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    /* --------------------------------------------------
       RESTORE SOFT-DELETED ACCOUNT
    -------------------------------------------------- */
    private fun restoreAccount(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val restoreResponse = ApiClient.api.restoreAccount(
                    mapOf(
                        "email" to email,
                        "password" to password
                    )
                )

                val restored = restoreResponse["status"] as? Boolean ?: false
                val message = restoreResponse["message"] as? String ?: "Restore failed"

                if (restored) {
                    // 🔁 LOGIN AGAIN AFTER RESTORE
                    val loginResponse = repository.login(email, password)

                    if (loginResponse.status) {
                        onSuccess(loginResponse.name ?: "User")
                    } else {
                        onError(loginResponse.message)
                    }
                } else {
                    onError(message)
                }

            } catch (e: Exception) {
                Log.e("RESTORE_API", "Restore error", e)
                onError("Restore failed. Try again.")
            }
        }
    }
}
