package com.elderease.app.ui.model

import com.google.gson.annotations.SerializedName

data class ElderProfile(

    @SerializedName("full_name")
    val fullName: String?,

    val email: String?,

    val phone: String?,

    val age: Int?,

    @SerializedName("weight_kg")
    val weightKg: Int?,

    @SerializedName("height_cm")
    val heightCm: Int?,

    val bmi: Double?,

    @SerializedName("blood_group")
    val bloodGroup: String?,

    @SerializedName("health_conditions")
    val healthConditions: String?,

    @SerializedName("profile_image")
    val profileImage: String?
)
