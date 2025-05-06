package com.example.ercrm.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ercrm.utils.NotificationHelper

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification(
            "Lead Reminder",
            "Tap to view lead details.",
            leadId = 1 // Static for now
        )
        return Result.success()
    }
} 