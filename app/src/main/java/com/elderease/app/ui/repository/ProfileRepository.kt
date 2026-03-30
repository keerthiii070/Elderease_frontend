package com.elderease.app.ui.repository

import com.elderease.app.ui.model.PersonalizeRequest
import com.elderease.app.ui.network.ApiClient

class ProfileRepository {

    /* -----------------------------
       SAVE PERSONALIZATION PROFILE
    ------------------------------ */
    suspend fun saveProfile(
        request: PersonalizeRequest
    ) =
        ApiClient.api.savePersonalizeProfile(request)
}
