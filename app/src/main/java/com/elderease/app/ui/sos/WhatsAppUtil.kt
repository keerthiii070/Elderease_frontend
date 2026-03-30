package com.elderease.app.ui.sos

import android.content.Context
import android.content.Intent
import android.net.Uri

object WhatsAppUtil {

    fun openWhatsApp(context: Context, phone: String, message: String) {

        val cleanPhone = phone.replace("+", "").replace(" ", "")

        val url = "https://wa.me/$cleanPhone?text=${Uri.encode(message)}"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            setPackage("com.whatsapp") // force WhatsApp
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}
