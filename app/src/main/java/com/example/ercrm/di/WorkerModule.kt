package com.example.ercrm.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.ercrm.worker.FollowUpCheckWorker
import com.example.ercrm.worker.NotificationWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.ClassKey
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    @Provides
    @IntoMap
    @ClassKey(FollowUpCheckWorker::class)
    fun provideFollowUpCheckWorkerFactory(
        factory: FollowUpCheckWorker.Factory
    ): ChildWorkerFactory {
        return object : ChildWorkerFactory {
            override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
                return factory.create(appContext, params)
            }
        }
    }

    @Provides
    @IntoMap
    @ClassKey(NotificationWorker::class)
    fun provideNotificationWorkerFactory(
        factory: NotificationWorker.Factory
    ): ChildWorkerFactory {
        return object : ChildWorkerFactory {
            override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
                return factory.create(appContext, params)
            }
        }
    }
} 