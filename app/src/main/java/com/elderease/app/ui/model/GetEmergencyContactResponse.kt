package com.elderease.app.ui.model

import com.google.gson.annotations.SerializedName

data class GetEmergencyContactResponse(
    val status: Boolean,

    @SerializedName("data")
    val contact: EmergencyContact?,

    val message: String? = null
)
