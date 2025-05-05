package com.example.ercrm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.service.FollowUpNotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class FollowUpCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val notificationService: FollowUpNotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFollowUps()
            if (response.isSuccessful && response.body()?.success == true) {
                val followUps = response.body()?.followUps ?: emptyList()
                
                // Filter follow-ups within ±2 days
                val relevantFollowUps = followUps.filter { followUp ->
                    followUp.daysLeft in -2..2
                }

                // Sort by days left (0, 1, 2, -1, -2)
                val sortedFollowUps = relevantFollowUps.sortedBy { it.daysLeft }

                // Show notification for each follow-up
                sortedFollowUps.forEach { followUp ->
                    notificationService.showFollowUpNotification(followUp)
                }

                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 