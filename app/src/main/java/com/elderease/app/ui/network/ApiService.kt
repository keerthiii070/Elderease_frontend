package com.elderease.app.ui.network

import com.elderease.app.ui.model.*
import okhttp3.RequestBody
import retrofit2.http.*

/* =========================================================
   MAIN API SERVICE
   ========================================================= */
interface ApiService {

    /* ---------------- LOGIN ---------------- */
    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    /* ---------------- SIGNUP ---------------- */
    @POST("signup.php")
    suspend fun signup(
        @Body request: SignupRequest
    ): SignupResponse

    /* ---------------- PERSONALIZE PROFILE ---------------- */
    @POST("save_personalize_profile.php")
    suspend fun savePersonalizeProfile(
        @Body request: PersonalizeRequest
    ): PersonalizeResponse

    /* ---------------- EMERGENCY CONTACT (JSON) ---------------- */
    @POST("save_emergency_contact.php")
    suspend fun saveEmergencyContact(
        @Body request: EmergencyContactRequest
    ): EmergencyContactResponse

    /* ---------------- GET ELDER PROFILE ---------------- */
    @GET("get_elder_profile.php")
    suspend fun getElderProfile(
        @Query("email") email: String
    ): ElderProfileResponse

    /* ---------------- GET EMERGENCY CONTACT ---------------- */
    @GET("get_emergency_contact.php")
    suspend fun getEmergencyContact(
        @Query("email") email: String
    ): GetEmergencyContactResponse

    /* =========================================================
       ✅ UPDATE ELDER PROFILE (MULTIPART – FIXED)
       ========================================================= */
    @POST("update_elder_profile.php")
    suspend fun updateElderProfile(
        @Body body: RequestBody
    ): Map<String, Any?>

    /* ---------------- EMERGENCY CONTACT (FORM) ---------------- */
    /* =========================================================
   ✅ UPDATE EMERGENCY CONTACT (FORM)
   ========================================================= */
    @FormUrlEncoded
    @POST("update_emergency_contact.php")
    suspend fun updateEmergencyContact(
        @Field("email") email: String,
        @Field("contact_name") contactName: String,
        @Field("relationship") relationship: String,
        @Field("contact_phone") contactPhone: String,
        @Field("contact_email") contactEmail: String,
        @Field("contact_age") contactAge: Int
    ): Map<String, Any?>


    /* =========================================================
       🤖 AI DIET CHAT
       ========================================================= */
    @POST("ai_diet_chat.php")
    suspend fun aiDietChat(
        @Body request: DietAiRequest
    ): DietAiResponse

    /* =========================================================
       💧 WATER INTAKE APIs
       ========================================================= */
    @POST("get_water_today.php")
    suspend fun getWaterToday(
        @Body body: Map<String, String>
    ): WaterTodayResponse

    @POST("add_water_intake.php")
    suspend fun addWaterIntake(
        @Body request: WaterRequest
    ): WaterActionResponse

    @POST("remove_water_intake.php")
    suspend fun removeWaterIntake(
        @Body request: WaterRequest
    ): WaterActionResponse

    @POST("set_daily_water_goal.php")
    suspend fun setDailyWaterGoal(
        @Body request: SetGoalRequest
    ): Map<String, Any>

    /* =========================================================
       🧃 JUICE INTAKE APIs
       ========================================================= */
    @POST("get_juice_today.php")
    suspend fun getJuiceToday(
        @Body body: Map<String, String>
    ): JuiceTodayResponse

    @POST("add_juice_intake.php")
    suspend fun addJuiceIntake(
        @Body request: JuiceRequest
    ): JuiceActionResponse

    @POST("remove_juice_intake.php")
    suspend fun removeJuiceIntake(
        @Body body: Map<String, String>
    ): JuiceActionResponse

    /* =========================================================
       🔐 PASSWORD FLOW
       ========================================================= */
    @Headers("Content-Type: application/json")
    @POST("forgot_password.php")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): BasicResponse

    @Headers("Content-Type: application/json")
    @POST("verify_otp.php")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): VerifyOtpResponse

    @POST("reset_password.php")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): BasicResponse

    /* =========================================================
       👤 ACCOUNT
       ========================================================= */
    @POST("soft_delete_account.php")
    suspend fun softDeleteAccount(
        @Body body: Map<String, String>
    ): Map<String, Any>

    @POST("restore_account.php")
    suspend fun restoreAccount(
        @Body body: Map<String, String>
    ): Map<String, Any>

    /* =========================================================
       📧 EMAIL OTP
       ========================================================= */
    @POST("send_email_otp.php")
    suspend fun send_Email_Otp(
        @Body body: Map<String, String>
    ): Map<String, Any>

    @POST("verify_email_otp.php")
    suspend fun verifyEmailOtp(
        @Body body: Map<String, String>
    ): Map<String, Any>

    /* ---------------- PRE-SIGNUP ---------------- */
    @POST("preSignup.php")
    suspend fun preSignup(
        @Body body: Map<String, String>
    ): Map<String, Any>
}
