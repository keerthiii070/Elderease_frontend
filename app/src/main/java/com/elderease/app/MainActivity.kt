package com.elderease.app

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elderease.app.ui.sos.PowerButtonReceiver
import com.elderease.app.ui.sos.SosManager
import com.elderease.app.ui.auth.*
import com.elderease.app.ui.login.*
import com.elderease.app.ui.main.*
import com.elderease.app.ui.profile.*
import com.elderease.app.ui.bloodpressure.*
import com.elderease.app.ui.yoga.GentleYogaScreen
import com.elderease.app.ui.medicine.MedicineReminderScreen
import com.elderease.app.ui.diet.DietPlannerScreen
import com.elderease.app.ui.healthkit.BMICalculatorScreen
import com.elderease.app.ui.healthkit.BMIResultScreen
import com.elderease.app.ui.healthkit.PainLogScreen
import com.elderease.app.ui.main.DrinkWaterScreen
import com.elderease.app.ui.main.JuiceReminderScreen
import com.elderease.app.ui.medicalandcautions.NavigationHubScreen
import com.elderease.app.ui.medicalandcautions.BPGuideScreen
import com.elderease.app.ui.medicalandcautions.CholesterolGuideScreen
import com.elderease.app.ui.medicalandcautions.CommonMistakesScreen
import com.elderease.app.ui.medicalandcautions.GlucoseGuideScreen
import com.elderease.app.ui.main.HandWristReliefScreen
import com.elderease.app.ui.main.HipBackReliefScreen
import com.elderease.app.ui.medicalandcautions.InteractionsScreen
import com.elderease.app.ui.main.KneeReliefScreen
import com.elderease.app.ui.medicalandcautions.LabTermsGuideScreen
import com.elderease.app.ui.medicalandcautions.MedicalAwarenessScreen
import com.elderease.app.ui.medicalandcautions.MedicineSafetyGuideScreen
import com.elderease.app.ui.medicalandcautions.SafeUsageGuideScreen
import com.elderease.app.ui.main.ShoulderReliefScreen
import com.elderease.app.ui.medicalandcautions.SideEffectsScreen
import com.elderease.app.ui.medicalandcautions.AlzheimerDetailScreen
import com.elderease.app.ui.medicalandcautions.ArthritisDetailScreen
import com.elderease.app.ui.medicalandcautions.DiabetesDetailScreen
import com.elderease.app.ui.medicalandcautions.HeartDiseaseDetailScreen
import com.elderease.app.ui.medicalandcautions.HypertensionDetailScreen
import com.elderease.app.ui.medicalandcautions.OsteoporosisDetailScreen
import com.elderease.app.ui.settings.SettingsScreen
import com.elderease.app.ui.medscan.MedScanScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // ✅ POWER BUTTON RECEIVER VARIABLE
    private lateinit var powerReceiver: PowerButtonReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted -> }

            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        /* =========================================================
           ✅ POWER BUTTON 3 TIMES → SOS TRIGGER
           ========================================================= */

        powerReceiver = PowerButtonReceiver {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val email = getSharedPreferences("elder_ease_prefs", MODE_PRIVATE)
                        .getString("user_email", "") ?: ""

                    if (email.isNotEmpty()) {
                        SosManager(this@MainActivity).triggerSOSCallOnly()
                    }
                } catch (_: Exception) {}
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        registerReceiver(powerReceiver, filter)

        /* =========================================================
           UI + NAVIGATION
           ========================================================= */

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = ROUTE_SPLASH
            ) {

                /* ---------- SPLASH ---------- */
                composable(ROUTE_SPLASH) { SplashScreen(navController) }

                /* ---------- AUTH ---------- */
                composable(ROUTE_LOGIN) { LoginScreen(navController) }
                composable(ROUTE_SIGNUP) { SignupScreen(navController) }
                composable(ROUTE_FORGOT) { ForgotPasswordScreen(navController) }

                composable(
                    "reset/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) {
                    ResetPasswordScreen(
                        navController = navController,
                        email = it.arguments?.getString("email") ?: ""
                    )
                }

                /* ---------- PERSONALIZATION ---------- */
                composable(
                    "$ROUTE_PERSONALIZE/{email}/{fullName}/{age}",
                    arguments = listOf(
                        navArgument("email") { type = NavType.StringType },
                        navArgument("fullName") { type = NavType.StringType },
                        navArgument("age") { type = NavType.StringType }
                    )
                ) {
                    PersonalizeScreen(
                        navController,
                        it.arguments?.getString("email") ?: "",
                        it.arguments?.getString("fullName") ?: "",
                        it.arguments?.getString("age") ?: ""
                    )
                }

                /* ---------- HOME ---------- */
                composable(ROUTE_HOME) { HomeScreen(navController) }

                /* ---------- DRINK WATER ---------- */
                composable(ROUTE_DRINK_WATER) { DrinkWaterScreen(navController) }

                /* ---------- JUICE REMINDER ---------- */
                composable(ROUTE_JUICE_REMINDER) { JuiceReminderScreen(navController) }

                /* ---------- BLOOD PRESSURE ---------- */
                composable(ROUTE_BLOOD_PRESSURE) { BloodPressureScreen(navController) }
                composable(ROUTE_BLOOD_PRESSURE_TRENDS) { BloodPressureTrendsScreen(navController) }
                composable(ROUTE_BLOOD_PRESSURE_HISTORY) { BloodPressureHistoryScreen(navController) }

                /* ---------- HEART RATE ---------- */
                composable(ROUTE_HEART_RATE) { HeartRateScreen(navController) }

                /* ---------- BMI ---------- */
                composable(ROUTE_BMI_CALCULATOR) { BMICalculatorScreen(navController) }
                composable(
                    "$ROUTE_BMI_RESULT/{bmi}/{category}/{age}",
                    arguments = listOf(
                        navArgument("bmi") { type = NavType.FloatType },
                        navArgument("category") { type = NavType.StringType },
                        navArgument("age") { type = NavType.IntType }
                    )
                ) {
                    BMIResultScreen(
                        navController,
                        it.arguments?.getFloat("bmi") ?: 0f,
                        it.arguments?.getString("category") ?: "",
                        it.arguments?.getInt("age") ?: 0
                    )
                }

                /* ---------- PAIN & RELIEF ---------- */
                composable(ROUTE_PAIN_LOG) { PainLogScreen(navController) }
                composable(ROUTE_HAND_WRIST_RELIEF) { HandWristReliefScreen(navController) }
                composable(ROUTE_KNEE_RELIEF) { KneeReliefScreen(navController) }
                composable(ROUTE_HIP_BACK_RELIEF) { HipBackReliefScreen(navController) }
                composable(ROUTE_SHOULDER_RELIEF) { ShoulderReliefScreen(navController) }

                /* ---------- MEDICAL INFO ---------- */
                composable(ROUTE_MEDICAL_AWARENESS) { MedicalAwarenessScreen(navController) }
                composable(ROUTE_GLUCOSE_GUIDE) { GlucoseGuideScreen(navController) }
                composable(ROUTE_LAB_TERMS_GUIDE) { LabTermsGuideScreen(navController) }
                composable(ROUTE_CHOLESTEROL_GUIDE) { CholesterolGuideScreen(navController) }
                composable(ROUTE_BP_GUIDE) { BPGuideScreen(navController) }

                composable(ROUTE_MEDICINE_SAFETY) { MedicineSafetyGuideScreen(navController) }
                composable(ROUTE_SAFE_USAGE_GUIDE) { SafeUsageGuideScreen(navController) }
                composable(ROUTE_COMMON_MISTAKES) { CommonMistakesScreen(navController) }
                composable(ROUTE_INTERACTIONS) { InteractionsScreen(navController) }
                composable(ROUTE_SIDE_EFFECTS) { SideEffectsScreen(navController) }
                composable(ROUTE_MEDICAL_CONDITIONS) { MedicalConditionsScreen(navController) }

                /* ---------- CONDITION DETAILS ---------- */
                composable(ROUTE_ARTHRITIS_DETAIL) { ArthritisDetailScreen(navController) }
                composable(ROUTE_HYPERTENSION_DETAIL) { HypertensionDetailScreen(navController) }
                composable(ROUTE_DIABETES_DETAIL) { DiabetesDetailScreen(navController) }
                composable(ROUTE_HEART_DISEASE_DETAIL) { HeartDiseaseDetailScreen(navController) }
                composable(ROUTE_OSTEOPOROSIS_DETAIL) { OsteoporosisDetailScreen(navController) }
                composable(ROUTE_ALZHEIMER_DETAIL) { AlzheimerDetailScreen(navController) }

                /* ---------- NAVIGATION HUB ---------- */
                composable(ROUTE_NAVIGATION_HUB) { NavigationHubScreen(navController) }

                /* ---------- LIFESTYLE ---------- */
                composable(ROUTE_YOGA) { GentleYogaScreen(navController) }
                composable(ROUTE_MEDICINE_REMINDER) { MedicineReminderScreen(navController) }
                composable(ROUTE_DIET_PLANNER) { DietPlannerScreen(navController) }

                /* ---------- TOOLS ---------- */
                composable(ROUTE_MEDSCAN) { MedScanScreen() }

                /* ---------- EMERGENCY ---------- */
                composable(ROUTE_EMERGENCY_ASSIST) {
                    val email = getLoggedInEmail(this@MainActivity)
                    EmergencyAssistScreen(navController = navController, emailArg = email)
                }

                composable(
                    "$ROUTE_EMERGENCY_CONTACT/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) {
                    EmergencyContactScreen(
                        navController = navController,
                        emailArg = it.arguments?.getString("email") ?: ""
                    )
                }
                composable(
                    "$ROUTE_SOS_COUNTDOWN/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) {
                    com.elderease.app.ui.sos.SosCountdownScreen(
                        navController = navController,
                        emailArg = it.arguments?.getString("email") ?: ""
                    )
                }


                /* ---------- PROFILE ---------- */
                composable(ROUTE_ELDER_PROFILE) { ElderProfileScreen(navController) }
                composable(ROUTE_EDIT_ELDER_PROFILE) { EditElderProfileScreen(navController) }

                /* ---------- SETTINGS ---------- */
                composable(ROUTE_SETTINGS) { SettingsScreen(navController) }
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(powerReceiver)
        super.onDestroy()
    }
}

/* ---------- LOGIN STATE ---------- */
fun isUserLoggedIn(context: Context): Boolean =
    context.getSharedPreferences("elder_ease_prefs", Context.MODE_PRIVATE)
        .getBoolean("is_logged_in", false)

/* ---------- GET USER EMAIL ---------- */
fun getLoggedInEmail(context: Context): String =
    context.getSharedPreferences("elder_ease_prefs", Context.MODE_PRIVATE)
        .getString("user_email", "") ?: ""

/* ---------- ROUTES ---------- */
const val ROUTE_SPLASH = "splash"
const val ROUTE_LOGIN = "login"
const val ROUTE_SIGNUP = "signup"
const val ROUTE_PERSONALIZE = "personalize"
const val ROUTE_FORGOT = "forgot"

const val ROUTE_RESET = "reset"
const val ROUTE_HOME = "home"

private const val ROUTE_SETTINGS = "settings"

/* --- BLOOD PRESSURE --- */
const val ROUTE_BLOOD_PRESSURE = "bloodPressure"
const val ROUTE_BLOOD_PRESSURE_HISTORY = "bloodPressureHistory"
const val ROUTE_BLOOD_PRESSURE_TRENDS = "bloodPressureTrends"

/* --- BMI --- */
const val ROUTE_BMI_CALCULATOR = "bmiCalculator"
const val ROUTE_BMI_RESULT = "bmiResult"

/* --- PAIN & RELIEF --- */
const val ROUTE_PAIN_LOG = "painLog"
const val ROUTE_HAND_WRIST_RELIEF = "handWristRelief"
const val ROUTE_KNEE_RELIEF = "kneeRelief"
const val ROUTE_HIP_BACK_RELIEF = "hipBackRelief"
const val ROUTE_SHOULDER_RELIEF = "shoulderRelief"

/* --- MEDICAL INFO --- */
const val ROUTE_MEDICAL_AWARENESS = "medicalAwareness"
const val ROUTE_GLUCOSE_GUIDE = "glucoseGuide"
const val ROUTE_LAB_TERMS_GUIDE = "labTermsGuide"
const val ROUTE_CHOLESTEROL_GUIDE = "cholesterolGuide"
const val ROUTE_BP_GUIDE = "bpGuide"
const val ROUTE_MEDICINE_SAFETY = "medicineSafety"
const val ROUTE_SAFE_USAGE_GUIDE = "safeUsageGuide"
const val ROUTE_COMMON_MISTAKES = "commonMistakes"
const val ROUTE_INTERACTIONS = "interactions"
const val ROUTE_SIDE_EFFECTS = "sideEffects"
const val ROUTE_MEDICAL_CONDITIONS = "medicalConditions"

/* --- LIFESTYLE --- */
const val ROUTE_YOGA = "gentleYoga"
const val ROUTE_MEDICINE_REMINDER = "medicineReminder"
const val ROUTE_DIET_PLANNER = "dietPlanner"

/* --- HEALTH / DAILY --- */
const val ROUTE_DRINK_WATER = "drinkWater"
const val ROUTE_JUICE_REMINDER = "juiceReminder"
const val ROUTE_HEART_RATE = "heartRate"

/* --- NAV HUB --- */
const val ROUTE_NAVIGATION_HUB = "navigationHub"

/* --- TOOLS --- */
const val ROUTE_MEDSCAN = "medScan"

/* --- EMERGENCY --- */
const val ROUTE_EMERGENCY_ASSIST = "emergencyAssist"
const val ROUTE_EMERGENCY_CONTACT = "emergency_contact"

const val ROUTE_SOS_COUNTDOWN = "sosCountdown"


/* --- PROFILE --- */
const val ROUTE_ELDER_PROFILE = "elder_profile"
const val ROUTE_EDIT_ELDER_PROFILE = "edit_elder_profile"

const val ROUTE_ARTHRITIS_DETAIL = "arthritisDetail"
const val ROUTE_DIABETES_DETAIL = "diabetesDetail"
const val ROUTE_HYPERTENSION_DETAIL = "hypertensionDetail"
const val ROUTE_HEART_DISEASE_DETAIL = "heartDiseaseDetail"
const val ROUTE_OSTEOPOROSIS_DETAIL = "osteoporosisDetail"
const val ROUTE_ALZHEIMER_DETAIL = "alzheimerDetail"
