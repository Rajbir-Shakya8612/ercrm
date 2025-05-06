package com.example.ercrm.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
// import com.example.ercrm.data.api.ApiService
// import com.example.ercrm.service.FollowUpNotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FollowUpCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
    // private val apiService: ApiService,
    // private val notificationService: FollowUpNotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // TODO: Add notification logic here if needed, or re-add DI with a custom WorkerFactory
        Result.success()
    }
} 