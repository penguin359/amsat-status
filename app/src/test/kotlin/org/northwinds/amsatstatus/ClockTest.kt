package org.northwinds.amsatstatus

import kotlin.test.*

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone

import org.northwinds.amsatstatus.ui.home.HomeFragment

class ClockTest {
	@Test
	fun testUtcToLocalConversion() {
		val cal = GregorianCalendar(TimeZone.getTimeZone("UTC"))
		cal.set(Calendar.YEAR, 2019)
		cal.set(Calendar.MONTH, 7-1)
		cal.set(Calendar.DAY_OF_MONTH, 11)
		cal.set(Calendar.HOUR_OF_DAY, 4)
		cal.set(Calendar.MINUTE, 10)
		cal.set(Calendar.SECOND, 30)

		cal.timeInMillis  // Recompute the time stamp to TZ change will happen
		cal.timeZone = TimeZone.getTimeZone("MST")  // This time zone is UTC-07
		assertEquals(2019, cal.get(Calendar.YEAR))
		assertEquals(7-1,  cal.get(Calendar.MONTH))
		assertEquals(10,   cal.get(Calendar.DAY_OF_MONTH))
		assertEquals(21,   cal.get(Calendar.HOUR_OF_DAY))
		assertEquals(10,   cal.get(Calendar.MINUTE))
		assertEquals(30,   cal.get(Calendar.SECOND))
	}

	@Test
	fun testClockGetsCorrectUtcTime() {
		val expected = GregorianCalendar(TimeZone.getTimeZone("UTC"))
		val clock = Clock()
		val actual = clock.utcCalendar

		assertEquals(expected.get(Calendar.YEAR),         actual.get(Calendar.YEAR))
		assertEquals(expected.get(Calendar.MONTH),        actual.get(Calendar.MONTH))
		assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH))
		assertEquals(expected.get(Calendar.HOUR_OF_DAY),  actual.get(Calendar.HOUR_OF_DAY))
		assertEquals(expected.get(Calendar.MINUTE),       actual.get(Calendar.MINUTE))
		assertEquals(expected.get(Calendar.SECOND),       actual.get(Calendar.SECOND))
	}

	@Test
	fun testClockGetsCorrectLocalTime() {
		val expected = GregorianCalendar()
		val clock = Clock()
		val actual = clock.localCalendar

		assertEquals(expected.get(Calendar.YEAR),         actual.get(Calendar.YEAR))
		assertEquals(expected.get(Calendar.MONTH),        actual.get(Calendar.MONTH))
		assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH))
		assertEquals(expected.get(Calendar.HOUR_OF_DAY),  actual.get(Calendar.HOUR_OF_DAY))
		assertEquals(expected.get(Calendar.MINUTE),       actual.get(Calendar.MINUTE))
		assertEquals(expected.get(Calendar.SECOND),       actual.get(Calendar.SECOND))
	}

	@Test
	fun testClockGetsCorrectFixedUtcTime() {
		val clock = Clock(1587610245L*1000)
		val actual = clock.utcCalendar

		assertEquals(2020, actual.get(Calendar.YEAR))
		assertEquals(4-1,  actual.get(Calendar.MONTH))
		assertEquals(23,   actual.get(Calendar.DAY_OF_MONTH))
		assertEquals(2,    actual.get(Calendar.HOUR_OF_DAY))
		assertEquals(50,   actual.get(Calendar.MINUTE))
		assertEquals(45,   actual.get(Calendar.SECOND))
	}

	@Test
	fun testClockGetsCorrectFixedLocalTime() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-09"))
		val clock = Clock(1587610245L*1000)
		val actual = clock.localCalendar

		assertEquals(2020, actual.get(Calendar.YEAR), "Bad year")
		assertEquals(4-1,  actual.get(Calendar.MONTH), "Bad month")
		assertEquals(22,   actual.get(Calendar.DAY_OF_MONTH), "Bad day")
		assertEquals(17,   actual.get(Calendar.HOUR_OF_DAY), "Bad hour")
		assertEquals(50,   actual.get(Calendar.MINUTE), "Bad minute")
		assertEquals(45,   actual.get(Calendar.SECOND), "Bad second")
	}
}
