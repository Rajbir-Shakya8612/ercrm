package com.example.ercrm.di

import com.example.ercrm.worker.FollowUpCheckWorker
import com.example.ercrm.worker.NotificationWorker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.ClassKey

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerBindingModule {
    @Binds
    @IntoMap
    @ClassKey(FollowUpCheckWorker::class)
    abstract fun bindFollowUpCheckWorkerFactory(factory: FollowUpCheckWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @ClassKey(NotificationWorker::class)
    abstract fun bindNotificationWorkerFactory(factory: NotificationWorker.Factory): ChildWorkerFactory
} 