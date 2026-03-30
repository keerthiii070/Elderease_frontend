package com.elderease.app.ui.sos

import android.content.Context
import android.content.Intent
import android.net.Uri

object CallUtil {

    fun openDialer(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
