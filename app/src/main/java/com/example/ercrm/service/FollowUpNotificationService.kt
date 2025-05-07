package com.example.ercrm.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ercrm.MainActivity
import com.example.ercrm.R
import com.example.ercrm.data.model.FollowUp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FollowUpNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "follow_ups_channel"
    private val channelName = "Follow-up Reminders"
    private val channelDescription = "Notifications for lead follow-ups"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showFollowUpNotification(followUp: FollowUp) {
        // Create intent for lead details
        val leadDetailsIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("lead_id", followUp.id)
            putExtra("screen", "lead_details")
        }

        // Create intent for follow-ups screen
        val followUpsIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("screen", "follow_ups")
        }

        val leadDetailsPendingIntent = PendingIntent.getActivity(
            context,
            followUp.id,
            leadDetailsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val followUpsPendingIntent = PendingIntent.getActivity(
            context,
            followUp.id + 1000, // Different request code to avoid conflicts
            followUpsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (followUp.isOverdue) "⚠️ Overdue Follow-up" else "🔔 Follow-up Reminder"
        val content = buildString {
            append("Follow-up with ${followUp.name} ")
            when {
                followUp.isOverdue -> append("was due ${Math.abs(followUp.daysLeft)} day(s) ago!")
                followUp.daysLeft == 0 -> append("scheduled for today!")
                else -> append("in ${followUp.daysLeft} day(s).")
            }
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(leadDetailsPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_notification,
                "View Lead",
                leadDetailsPendingIntent
            )
            .addAction(
                R.drawable.ic_notification,
                "View All Follow-ups",
                followUpsPendingIntent
            )
            .build()

        notificationManager.notify(followUp.id, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
} 