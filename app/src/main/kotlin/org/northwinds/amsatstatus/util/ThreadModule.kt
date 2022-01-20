package org.northwinds.amsatstatus.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
@InstallIn(ActivityRetainedComponent::class)
class ThreadModule {
    @Provides
    @ActivityRetainedScoped
    fun provideThreadExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }
}