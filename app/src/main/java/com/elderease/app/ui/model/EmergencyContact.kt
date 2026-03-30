package com.elderease.app.ui.model

import com.google.gson.annotations.SerializedName

data class EmergencyContact(
    @SerializedName("contact_name")
    val contactName: String?,

    val relationship: String?,

    @SerializedName("contact_phone")
    val contactPhone: String?,

    @SerializedName("contact_email")
    val contactEmail: String?,

    @SerializedName("contact_age")
    val contactAge: Int?
)
