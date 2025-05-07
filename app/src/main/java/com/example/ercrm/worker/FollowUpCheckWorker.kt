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

class FollowUpCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val notificationService: FollowUpNotificationService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "FollowUpCheckWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting follow-up check worker")
            val response = apiService.getFollowUps()
            
            if (response.isSuccessful) {
                Log.d(TAG, "API call successful")
                val followUps = response.body()?.followUps ?: emptyList()
                Log.d(TAG, "Received ${followUps.size} follow-ups")
                
                // Filter follow-ups within ±2 days
                val filteredFollowUps = followUps.filter { followUp ->
                    val daysLeft = followUp.daysLeft
                    daysLeft in -2..2
                }
                Log.d(TAG, "Filtered to ${filteredFollowUps.size} follow-ups within ±2 days")

                // Sort by days left (0, 1, 2, -1, -2)
                val sortedFollowUps = filteredFollowUps.sortedBy { it.daysLeft }
                Log.d(TAG, "Sorted follow-ups by days left")

                // Show notifications for each follow-up
                sortedFollowUps.forEach { followUp ->
                    Log.d(TAG, "Processing follow-up: ${followUp.name} (${followUp.daysLeft} days left)")
                    notificationService.showFollowUpNotification(followUp)
                }

                Log.d(TAG, "Successfully processed all follow-ups")
                Result.success()
            } else {
                val errorMessage = response.body()?.message ?: "Unknown error"
                Log.e(TAG, "API call failed: $errorMessage")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in follow-up check worker", e)
            Result.failure()
        }
    }

    @AssistedFactory
    interface Factory : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): FollowUpCheckWorker
    }
} 