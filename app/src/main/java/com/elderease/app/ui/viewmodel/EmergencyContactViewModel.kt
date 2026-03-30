package com.elderease.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.EmergencyContactRequest
import com.elderease.app.ui.repository.EmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmergencyContactViewModel : ViewModel() {

    private val repository = EmergencyRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun setMessage(msg: String) {
        _message.value = msg
    }

    fun saveEmergencyContact(
        request: EmergencyContactRequest,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.saveEmergencyContact(request)
                _loading.value = false

                if (response.status) {
                    onSuccess()
                } else {
                    _message.value = response.message
                }

            } catch (e: Exception) {
                _loading.value = false
                _message.value = "Network error. Please try again."
            }
        }
    }
}
