package com.elderease.app.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.DietAiRequest
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DietPlannerViewModel : ViewModel() {

    private val _reply = MutableStateFlow<String?>(null)
    val reply: StateFlow<String?> = _reply

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun sendPrompt(email: String, prompt: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = ApiClient.api.aiDietChat(
                    DietAiRequest(email, prompt)
                )

                if (response.status) {
                    _reply.value = response.reply
                } else {
                    _error.value = response.message ?: "Something went wrong"
                }
            } catch (e: Exception) {
                _error.value = "Network error"
            } finally {
                _loading.value = false
            }
        }
    }
}
