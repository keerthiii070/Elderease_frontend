package com.elderease.app.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.elderease.app.ui.network.ApiClient
import kotlinx.coroutines.launch

private const val ROUTE_LOGIN = "login"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    /* -------- GET EMAIL FROM SESSION -------- */
    val email = remember {
        context.getSharedPreferences(
            "elder_ease_prefs",
            Context.MODE_PRIVATE
        ).getString("user_email", null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* -------- PRIVACY POLICY -------- */
            SettingsItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "Read how we protect your data",
                onClick = {
                    navController.navigate("privacy_policy")
                }
            )

            Divider()

            /* -------- DELETE ACCOUNT -------- */
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Delete Account",
                subtitle = "Deactivate your account safely",
                titleColor = Color.Red,
                onClick = {
                    showDeleteDialog = true
                }
            )
        }
    }

    /* -------- DELETE CONFIRMATION -------- */
    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {

                if (email == null) {
                    Toast.makeText(context, "Session expired", Toast.LENGTH_SHORT).show()
                    return@DeleteAccountDialog
                }

                loading = true
                showDeleteDialog = false

                scope.launch {
                    try {
                        val response = ApiClient.api.softDeleteAccount(
                            mapOf("email" to email)
                        )

                        val status = response["status"] as? Boolean ?: false
                        val message = response["message"] as? String ?: "Unknown error"

                        if (status) {
                            // 🔴 Clear session
                            context.getSharedPreferences(
                                "elder_ease_prefs",
                                Context.MODE_PRIVATE
                            ).edit().clear().apply()

                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            "Server error. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        loading = false
                    }
                }
            }
        )
    }

    /* -------- LOADING OVERLAY -------- */
    if (loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/* ================= COMPONENTS ================= */

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    fontSize = 16.sp
                )
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Account", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "Your account will be deactivated.\n\n" +
                        "You can restore your account within 30 days by signing up again with the same email.\n\n" +
                        "⚠ After 30 days, your account and all data will be permanently deleted.\n\n" +
                        "Do you want to continue?"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
