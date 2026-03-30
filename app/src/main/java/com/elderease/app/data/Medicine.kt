package com.elderease.app.data

data class Medicine(
    val medicineName: String,
    val composition: String,
    val uses: String,
    val sideEffects: String,
    val imageUrl: String,
    val manufacturer: String,
    val excellentReview: Int,
    val averageReview: Int,
    val poorReview: Int,
    val prescriptionRequired: String,
    val precautions: String,
    val storage: String,
    val warnings: String,
    val category: String
)
