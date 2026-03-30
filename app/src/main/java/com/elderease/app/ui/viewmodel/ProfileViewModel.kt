package com.elderease.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.ElderProfile
import com.elderease.app.ui.model.EmergencyContact
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    /* ---------- PROFILE ---------- */
    private val _profile = MutableStateFlow<ElderProfile?>(null)
    val profile: StateFlow<ElderProfile?> = _profile

    /* ---------- EMERGENCY ---------- */
    private val _emergency = MutableStateFlow<EmergencyContact?>(null)
    val emergency: StateFlow<EmergencyContact?> = _emergency

    /* ---------- LOADING ---------- */
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /* ---------- ERROR ---------- */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /* ---------- LOAD PROFILE ---------- */
    fun loadProfile(email: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val res = ApiClient.api.getElderProfile(email)
                if (res.status) {
                    _profile.value = res.profile
                } else {
                    _profile.value = null
                    _error.value = "Profile not found"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Network error"
                _profile.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    /* ---------- LOAD EMERGENCY CONTACT ---------- */
    fun loadEmergencyContact(email: String) {
        viewModelScope.launch {
            try {
                val res = ApiClient.api.getEmergencyContact(email)
                _emergency.value = if (res.status) res.contact else null
            } catch (e: Exception) {
                e.printStackTrace()
                _emergency.value = null
            }
        }
    }
}
