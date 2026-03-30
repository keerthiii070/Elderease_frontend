package com.elderease.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elderease.app.ui.network.ApiClient
import com.elderease.app.util.FileUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class EditProfileViewModel : ViewModel() {

    fun saveProfileAndEmergency(
        context: Context,
        email: String,

        // profile
        fullName: String,
        userPhone: String,
        age: Int,
        weight: Int,
        bloodGroup: String,
        medicalConditions: String,
        imageUri: Uri?,
        existingImage: String?,

        // emergency
        contactName: String,
        relationship: String,
        contactPhone: String,
        contactEmail: String,
        contactAge: Int,

        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {

                /* --------------------------------
                   1) UPDATE ELDER PROFILE (MULTIPART)
                -------------------------------- */
                val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email)
                    .addFormDataPart("full_name", fullName)
                    .addFormDataPart("age", age.toString())
                    .addFormDataPart("weight_kg", weight.toString())
                    .addFormDataPart("blood_group", bloodGroup)
                    .addFormDataPart("health_conditions", medicalConditions)
                    .addFormDataPart("phone", userPhone)

                // IMAGE HANDLING
                when {
                    imageUri != null -> {
                        val file = FileUtil.from(context, imageUri)
                        builder.addFormDataPart(
                            "profile_image",
                            file.name,
                            file.asRequestBody("image/*".toMediaType())
                        )
                    }

                    !existingImage.isNullOrEmpty() -> {
                        builder.addFormDataPart("profile_image", existingImage)
                    }
                }

                val profileRequestBody = builder.build()

                val profileResponse = ApiClient.api.updateElderProfile(profileRequestBody)

                Log.d("PROFILE_UPDATE", "response=$profileResponse")

                if (profileResponse["status"] != true) {
                    Toast.makeText(
                        context,
                        profileResponse["message"]?.toString() ?: "Profile update failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                /* --------------------------------
                   2) UPDATE EMERGENCY CONTACT (FORM)
                -------------------------------- */
                val emergencyResponse = ApiClient.api.updateEmergencyContact(
                    email = email,
                    contactName = contactName,
                    relationship = relationship,
                    contactPhone = contactPhone,
                    contactEmail = contactEmail,
                    contactAge = contactAge
                )

                Log.d("EMERGENCY_UPDATE", "response=$emergencyResponse")

                if (emergencyResponse["status"] == true) {
                    Toast.makeText(
                        context,
                        "Profile + Emergency updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    onSuccess()
                } else {
                    Toast.makeText(
                        context,
                        emergencyResponse["message"]?.toString() ?: "Emergency update failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
