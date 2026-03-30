package com.elderease.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.model.SetGoalRequest
import com.elderease.app.ui.model.WaterRequest
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DrinkWaterViewModel(application: Application) : AndroidViewModel(application) {

    /* ---------- PREFS ---------- */
    private val prefs =
        application.getSharedPreferences("elder_ease_prefs", Application.MODE_PRIVATE)

    private val email: String =
        prefs.getString("user_email", "") ?: ""

    /* ---------- STATE ---------- */
    private val _currentIntake = MutableStateFlow(0)
    val currentIntake: StateFlow<Int> = _currentIntake

    private val _dailyTarget = MutableStateFlow(2000)
    val dailyTarget: StateFlow<Int> = _dailyTarget

    /* ---------- UI MESSAGE (FIX) ---------- */
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    fun clearMessage() {
        _uiMessage.value = null
    }

    /* --------------------------------------------------
       LOAD TODAY
    -------------------------------------------------- */
    fun loadToday() {
        if (email.isBlank()) return

        viewModelScope.launch {
            try {
                val res = ApiClient.api.getWaterToday(
                    mapOf("email" to email)
                )
                if (res.status) {
                    _currentIntake.value = res.todayTotal
                    _dailyTarget.value = res.dailyTarget
                }
            } catch (e: Exception) {
                _uiMessage.value = "Failed to load water data"
            }
        }
    }

    /* --------------------------------------------------
       ADD WATER
    -------------------------------------------------- */
    fun addIntake(amount: Int) {
        if (email.isBlank()) return

        viewModelScope.launch {
            try {
                val res = ApiClient.api.addWaterIntake(
                    WaterRequest(email, amount)
                )

                if (res.status) {
                    _currentIntake.value = res.todayTotal
                    _dailyTarget.value = res.dailyGoal
                } else {
                    _uiMessage.value = res.message
                }

            } catch (e: Exception) {
                _uiMessage.value = "Server error while adding water"
            }
        }
    }

    /* --------------------------------------------------
       REMOVE WATER
    -------------------------------------------------- */
    fun removeIntake(amount: Int) {
        if (email.isBlank()) return

        viewModelScope.launch {
            try {
                val res = ApiClient.api.removeWaterIntake(
                    WaterRequest(email, amount)
                )

                if (res.status) {
                    _currentIntake.value = res.todayTotal
                } else {
                    _uiMessage.value = res.message
                }

            } catch (e: Exception) {
                _uiMessage.value = "Server error while removing water"
            }
        }
    }

    /* --------------------------------------------------
       SET DAILY GOAL (MAX 5000)
    -------------------------------------------------- */
    fun setDailyTarget(target: Int) {
        if (email.isBlank()) return

        if (target > 5000) {
            _uiMessage.value = "Maximum daily goal is 5000 ml"
            return
        }

        viewModelScope.launch {
            try {
                ApiClient.api.setDailyWaterGoal(
                    SetGoalRequest(email, target)
                )
                _dailyTarget.value = target
            } catch (e: Exception) {
                _uiMessage.value = "Failed to update daily goal"
            }
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory {
            return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
    }
}
