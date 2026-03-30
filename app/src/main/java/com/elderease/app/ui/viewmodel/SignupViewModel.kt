package com.elderease.app.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _signupSuccess = MutableStateFlow(false)
    val signupSuccess: StateFlow<Boolean> = _signupSuccess

    fun signup(
        context: Context,
        fullName: String,
        email: String,
        phone: String,
        age: String,
        password: String,
        confirmPassword: String
    ) {
        if (password != confirmPassword) {
            _message.value = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true

                val cleanName = fullName.trim()

                val response = repository.signup(
                    cleanName,
                    email.trim(),
                    phone.trim(),
                    age.trim(),
                    password,
                    confirmPassword
                )
                Log.d("SIGNUP_DEBUG", "Response = $response")


                _loading.value = false

                if (response.status) {

                    // ✅ SAVE USER NAME (CORRECT WAY)
                    val prefs = context.applicationContext
                        .getSharedPreferences(
                            "elder_ease_prefs",
                            Context.MODE_PRIVATE
                        )

                    prefs.edit()
                        .putString("user_name", cleanName)
                        .commit() // 🔥 IMPORTANT (sync save)

                    // ✅ DEBUG LOG (VERIFY ONCE)
                    Log.d(
                        "SIGNUP",
                        "Saved user_name = ${
                            prefs.getString("user_name", "NOT_SAVED")
                            
                            
                        }"
                    )

                    _signupSuccess.value = true
                } else {
                    _message.value = response.message
                }

            } catch (e: Exception) {
                _loading.value = false
                _message.value = "Server error. Please try again."
            }
        }
    }
}
