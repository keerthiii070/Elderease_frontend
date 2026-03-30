package com.elderease.app.ui.network

import android.os.Build
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private fun isEmulator(): Boolean {
        return (
                Build.FINGERPRINT.startsWith("generic") ||
                        Build.FINGERPRINT.lowercase().contains("emulator") ||
                        Build.MODEL.contains("Emulator") ||
                        Build.MODEL.contains("Android SDK built for x86") ||
                        Build.MANUFACTURER.contains("Genymotion") ||
                        Build.BRAND.startsWith("generic") ||
                        Build.DEVICE.startsWith("generic")
                )
    }

    private val BASE_URL: String by lazy {
        if (isEmulator()) {
            "http://10.0.2.2/elder_ease/"
        } else {
            "http://192.168.31.194/elder_ease/"
        }
    }

    // ✅ ADD THIS (FOR PROFILE IMAGE URL)
    val profileImageBaseUrl: String by lazy {
        BASE_URL + "uploads/profile_images/"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
