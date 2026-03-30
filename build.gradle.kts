// Root build.gradle.kts
// ❌ DO NOT APPLY ANDROID / KSP HERE

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
