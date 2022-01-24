/**********************************************************************************
 * Copyright (c) 2022 Loren M. Lang                                               *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

/**********************************************************************************
 * Copyright (c) 2020 Loren M. Lang                                               *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

package org.northwinds.amsatstatus.util

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ClockTest {
    @Test
    fun testUtcToLocalConversion() {
        val cal = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.YEAR, 2019)
        cal.set(Calendar.MONTH, 7 - 1)
        cal.set(Calendar.DAY_OF_MONTH, 11)
        cal.set(Calendar.HOUR_OF_DAY, 4)
        cal.set(Calendar.MINUTE, 10)
        cal.set(Calendar.SECOND, 30)

        cal.timeInMillis  // Recompute the time stamp to TZ change will happen
        cal.timeZone = TimeZone.getTimeZone("MST")  // This time zone is UTC-07
        assertEquals(2019, cal.get(Calendar.YEAR))
        assertEquals(7 - 1, cal.get(Calendar.MONTH))
        assertEquals(10, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(21, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(10, cal.get(Calendar.MINUTE))
        assertEquals(30, cal.get(Calendar.SECOND))
    }

    @Test
    fun testClockGetsCorrectUtcTime() {
        val expected = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        val clock = MyClock()
        val actual = clock.utcCalendar

        assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR))
        assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH))
        assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH))
        assertEquals(expected.get(Calendar.HOUR_OF_DAY), actual.get(Calendar.HOUR_OF_DAY))
        assertEquals(expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE))
        assertEquals(expected.get(Calendar.SECOND), actual.get(Calendar.SECOND))
    }

    @Test
    fun testClockGetsCorrectLocalTime() {
        val expected = GregorianCalendar()
        val clock = MyClock()
        val actual = clock.localCalendar

        assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR))
        assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH))
        assertEquals(expected.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH))
        assertEquals(expected.get(Calendar.HOUR_OF_DAY), actual.get(Calendar.HOUR_OF_DAY))
        assertEquals(expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE))
        assertEquals(expected.get(Calendar.SECOND), actual.get(Calendar.SECOND))
    }

    @Test
    fun testClockGetsCorrectFixedUtcTime() {
        val clock = MyClock(1587610245L * 1000)
        val actual = clock.utcCalendar

        assertEquals(2020, actual.get(Calendar.YEAR))
        assertEquals(4 - 1, actual.get(Calendar.MONTH))
        assertEquals(23, actual.get(Calendar.DAY_OF_MONTH))
        assertEquals(2, actual.get(Calendar.HOUR_OF_DAY))
        assertEquals(50, actual.get(Calendar.MINUTE))
        assertEquals(45, actual.get(Calendar.SECOND))
    }

    @Test
    fun testClockGetsCorrectFixedLocalTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-09"))
        val clock = MyClock(1587610245L * 1000)
        val actual = clock.localCalendar

        assertEquals(2020, actual.get(Calendar.YEAR), "Bad year")
        assertEquals(4 - 1, actual.get(Calendar.MONTH), "Bad month")
        assertEquals(22, actual.get(Calendar.DAY_OF_MONTH), "Bad day")
        assertEquals(17, actual.get(Calendar.HOUR_OF_DAY), "Bad hour")
        assertEquals(50, actual.get(Calendar.MINUTE), "Bad minute")
        assertEquals(45, actual.get(Calendar.SECOND), "Bad second")
    }

    @Test
    fun testClockGetsCorrectTimesDuringNewYears() {
        // UTC:   2019-01-01T05:23:45Z
        // Local: 2018-12-31T21:23:45-08:00
        val refTime = 1546320225 * 1000L
        val refTimezone = "America/Los_Angeles"

        TimeZone.setDefault(TimeZone.getTimeZone(refTimezone))
        val clock = MyClock(refTime)
        val utc = clock.utcCalendar
        assertEquals(2019, utc.get(Calendar.YEAR), "Bad year")
        assertEquals(1 - 1, utc.get(Calendar.MONTH), "Bad month")
        assertEquals(1, utc.get(Calendar.DAY_OF_MONTH), "Bad day")
        assertEquals(5, utc.get(Calendar.HOUR_OF_DAY), "Bad hour")
        assertEquals(23, utc.get(Calendar.MINUTE), "Bad minute")
        assertEquals(45, utc.get(Calendar.SECOND), "Bad second")

        val local = clock.localCalendar
        assertEquals(2018, local.get(Calendar.YEAR), "Bad year")
        assertEquals(12 - 1, local.get(Calendar.MONTH), "Bad month")
        assertEquals(31, local.get(Calendar.DAY_OF_MONTH), "Bad day")
        assertEquals(21, local.get(Calendar.HOUR_OF_DAY), "Bad hour")
        assertEquals(23, local.get(Calendar.MINUTE), "Bad minute")
        assertEquals(45, local.get(Calendar.SECOND), "Bad second")
    }
}
