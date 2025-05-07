package com.example.ercrm.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.di.ChildWorkerFactory
import com.example.ercrm.service.FollowUpNotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val notificationService: FollowUpNotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("NotificationWorker", "Notification Worker started.")
        try {
            val response = apiService.getFollowUps()
            if (response.isSuccessful && response.body()?.success == true) {
                val followUps = response.body()?.followUps ?: emptyList()
                Log.d("NotificationWorker", "Follow-ups retrieved: ${followUps.size}")
                // Filter follow-ups within ±2 days
                val filteredFollowUps = followUps.filter { followUp ->
                    val daysLeft = followUp.daysLeft
                    daysLeft in -2..2
                }
                Log.d("NotificationWorker", "Filtered follow-ups: ${filteredFollowUps.size}")
                // Sort by days left (0, 1, 2, -1, -2)
                val sortedFollowUps = filteredFollowUps.sortedBy { it.daysLeft }
                Log.d("NotificationWorker", "Sorted follow-ups: ${sortedFollowUps.size}")
                // Show notifications for each follow-up
                sortedFollowUps.forEach { followUp ->
                    Log.d("NotificationWorker", "Showing notification for follow-up: ${followUp.id}")
                    notificationService.showFollowUpNotification(followUp)
                }

                Result.success()
            } else {
                Log.d("NotificationWorker", "API request failed: ${response.message()}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error in NotificationWorker: ${e.message}", e)
            Result.failure()
        }
    }

    @AssistedFactory
    interface Factory : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): NotificationWorker
    }
} 