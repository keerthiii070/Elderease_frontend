package com.elderease.app.ui.model

import com.google.gson.annotations.SerializedName

data class ElderProfileResponse(
    val status: Boolean,

    @SerializedName("profile")
    val profile: ElderProfile?
)
