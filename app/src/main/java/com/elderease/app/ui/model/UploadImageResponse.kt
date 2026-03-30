package com.elderease.app.ui.model

import com.google.gson.annotations.SerializedName

data class UploadImageResponse(
    val status: Boolean,
    val message: String?,

    @SerializedName("profile_image")
    val profileImage: String?
)
