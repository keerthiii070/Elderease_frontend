package com.elderease.app.ui.model

import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    val status: Boolean,

    @SerializedName("profile")
    val profile: ElderProfile?
)
