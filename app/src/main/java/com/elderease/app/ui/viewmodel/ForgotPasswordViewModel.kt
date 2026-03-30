package com.elderease.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.ForgotPasswordRequest
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    fun sendOtp(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.forgotPassword(
                    ForgotPasswordRequest(email = email)
                )

                if (response.status) {
                    onSuccess()
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                e.printStackTrace() // 👈 VERY IMPORTANT FOR LOGCAT DEBUGGING
                onError(e.localizedMessage ?: "Unexpected error")
            }
        }
    }
}
