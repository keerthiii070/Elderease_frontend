package com.elderease.app.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        ReminderNotificationUtil.showReminderNotification(
            context = context,
            title = "💧 Water Reminder",
            message = "Time to drink water!"
        )
    }
}
