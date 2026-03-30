package com.elderease.app.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class JuiceReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        ReminderNotificationUtil.showReminderNotification(
            context = context,
            title = "🍹 Juice Reminder",
            message = "Time for your healthy fresh juice snack!"
        )
    }
}
