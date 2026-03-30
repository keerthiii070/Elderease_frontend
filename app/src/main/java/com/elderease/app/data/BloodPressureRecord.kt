package com.elderease.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_records")
data class BloodPressureRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int = 72,
    val timestamp: Long = System.currentTimeMillis()
)