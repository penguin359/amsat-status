package org.northwinds.amsatstatus.util

import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.testing.TestInstallIn

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.RuntimeException
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = arrayOf(ActivityRetainedComponent::class),
    replaces = arrayOf(ThreadModule::class)
)
abstract class EspressoThreadModule {
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
        val id = counter.getAndIncrement()
//        return IdlingThreadPoolExecutor("EspressoTestPool-$id", 5, 5, 50, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory())
        return IdlingThreadPoolExecutor("EspressoTestPool", 5, 5, 50, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory())
    }

    companion object {
        val counter = AtomicInteger(1)
    }
}
