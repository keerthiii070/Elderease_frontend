package com.elderease.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.ResetPasswordRequest
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    fun resetPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.resetPassword(
                    ResetPasswordRequest(
                        email = email,
                        password = password
                    )
                )

                if (response.status) {
                    onSuccess()
                } else {
                    onError(response.message)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.localizedMessage ?: "Network error")
            }
        }
    }
}
