package com.elderease.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.JuiceRequest
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JuiceViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiClient.api   // ✅ FIXED

    private val _currentIntake = MutableStateFlow(0)
    val currentIntake: StateFlow<Int> = _currentIntake

    private val _dailyLimit = MutableStateFlow(400)
    val dailyLimit: StateFlow<Int> = _dailyLimit

    private fun getEmail(): String =
        getApplication<Application>()
            .getSharedPreferences("elder_ease_prefs", Application.MODE_PRIVATE)
            .getString("user_email", "") ?: ""

    /* ---------- LOAD TODAY JUICE ---------- */
    fun loadToday() {
        viewModelScope.launch {
            try {
                val res = api.getJuiceToday(
                    mapOf("email" to getEmail())
                )
                if (res.status) {
                    _currentIntake.value = res.todayTotal
                    _dailyLimit.value = res.dailyLimit
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ---------- ADD JUICE ---------- */
    fun addJuice(amount: Int = 50) {
        viewModelScope.launch {
            try {
                val res = api.addJuiceIntake(
                    JuiceRequest(getEmail(), amount)
                )
                if (res.status) {
                    _currentIntake.value = res.todayTotal
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ---------- REMOVE JUICE ---------- */
    fun removeJuice(amount: Int = 50) {
        viewModelScope.launch {
            try {
                val res = api.removeJuiceIntake(
                    mapOf(
                        "email" to getEmail(),
                        "amount_ml" to amount.toString()
                    )
                )
                if (res.status) {
                    _currentIntake.value = res.todayTotal
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
