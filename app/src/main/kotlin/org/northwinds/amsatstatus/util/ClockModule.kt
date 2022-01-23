package org.northwinds.amsatstatus.util

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ClockModule {
    @Binds
    fun bindClock(clock: MyClock): Clock
}