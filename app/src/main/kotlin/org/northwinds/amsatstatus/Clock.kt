package org.northwinds.amsatstatus

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

interface Clock {
	val utcCalendar: Calendar get
	val localCalendar: Calendar get
}

class MyClock(val timestamp: Long) : Clock {
	@Inject
	constructor() : this(System.currentTimeMillis())

	override val utcCalendar get() = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = timestamp }

	override val localCalendar get() = Calendar.getInstance().apply { timeInMillis = timestamp }
}

@Module
@InstallIn(SingletonComponent::class)
interface ClockModule {
	@Binds fun bindClock(clock: MyClock): Clock
}