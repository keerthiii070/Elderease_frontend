package com.elderease.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.PersonalizeRequest
import com.elderease.app.ui.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonalizeViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun saveProfile(request: PersonalizeRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.saveProfile(request)
                _loading.value = false

                if (response.status) {
                    onSuccess()
                } else {
                    _message.value = response.message
                }
            } catch (e: Exception) {
                _loading.value = false
                _message.value = "Server error. Try again."
            }
        }
    }
}
