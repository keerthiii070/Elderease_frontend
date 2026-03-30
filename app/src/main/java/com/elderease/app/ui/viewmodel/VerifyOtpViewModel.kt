package com.elderease.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.VerifyOtpRequest
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.launch

class VerifyOtpViewModel : ViewModel() {

    fun verifyOtp(
        email: String,
        otp: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.verifyOtp(
                    VerifyOtpRequest(email = email, otp = otp)
                )

                if (response.status) {
                    onSuccess()
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                onError("Network error. Please try again.")
            }
        }
    }
}
