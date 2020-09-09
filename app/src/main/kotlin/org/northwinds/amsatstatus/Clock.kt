package org.northwinds.amsatstatus

import java.util.Calendar
import java.util.TimeZone

class Clock(val timestamp: Long) {
	constructor() : this(System.currentTimeMillis())
	val utcCalendar get() = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = timestamp }
	val localCalendar get() = Calendar.getInstance().apply { timeInMillis = timestamp }
}
