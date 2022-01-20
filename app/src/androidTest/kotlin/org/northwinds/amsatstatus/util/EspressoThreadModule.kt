package org.northwinds.amsatstatus.util

import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.testing.TestInstallIn
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.RuntimeException
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = arrayOf(ActivityRetainedComponent::class),
    replaces = arrayOf(ThreadModule::class)
)
abstract class EspressoThreadModule {
/*
    @Provides
    @ActivityRetainedScoped
    fun provideIdlingThreadExecutor(): IdlingThreadPoolExecutor {
        return IdlingThreadPoolExecutor("EspressoTestPool", 1, 5, 50, TimeUnit.MILLISECONDS, LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory())
    }
*/

    @Binds
    abstract fun provideThreadExecutor(executor: IdlingThreadPoolExecutor): ExecutorService
}

@Module
//@InstallIn(ActivityRetainedComponent::class)
@InstallIn(SingletonComponent::class)
class IdleEspressoThreadModule {
    @Provides
//    @ActivityRetainedScoped
    @Singleton
    fun provideIdlingThreadExecutor(): IdlingThreadPoolExecutor {
        return IdlingThreadPoolExecutor("EspressoTestPool", 1, 5, 50, TimeUnit.MILLISECONDS, LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory())
    }
}
