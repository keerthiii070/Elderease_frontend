package com.elderease.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.elderease.app.data.BloodPressureRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Insert
    suspend fun insert(record: BloodPressureRecord)

    @Query("SELECT * FROM blood_pressure_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<BloodPressureRecord>>
}