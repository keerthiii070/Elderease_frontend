package com.elderease.app.ui.repository

import com.elderease.app.ui.model.EmergencyContactRequest
import com.elderease.app.ui.network.ApiClient

class EmergencyRepository {

    suspend fun saveEmergencyContact(request: EmergencyContactRequest) =
        ApiClient.api.saveEmergencyContact(request)

    suspend fun getEmergencyContact(email: String) =
        ApiClient.api.getEmergencyContact(email)
}
