package com.elderease.app.data

import kotlinx.coroutines.flow.Flow

class BloodPressureRepository(private val bloodPressureDao: BloodPressureDao) {

    val allRecords: Flow<List<BloodPressureRecord>> = bloodPressureDao.getAllRecords()

    suspend fun insert(record: BloodPressureRecord) {
        bloodPressureDao.insert(record)
    }
}