/**********************************************************************************
 * Copyright (c) 2021 Loren M. Lang                                               *
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

package org.northwinds.amsatstatus

import android.nfc.FormatException
import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Calendar
import java.util.TimeZone
import java.util.GregorianCalendar

enum class Report(val value: String) {
    HEARD("Heard"),
    TELEMETRY_ONLY("Telemetry Only"),
    NOT_HEARD("Not Heard"),
    CREW_ACTIVE("Crew Active"),
    CONFLICTED("Conflicted");
}

fun reportFromString(str: String): Report {
    for (item in Report.values()) {
        if (item.value.equals(str, true)) {
            return item
        }
    }
    return Report.NOT_HEARD
}

class ReportTime(timestamp: Long) {
    private val time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = timestamp
    }

    constructor(time: Calendar) : this(time.timeInMillis)

    val year get() = this.time.get(Calendar.YEAR)
    val month get() = this.time.get(Calendar.MONTH)
    val day get() = this.time.get(Calendar.DAY_OF_MONTH)
    val hour get() = this.time.get(Calendar.HOUR_OF_DAY)
    val minute get() = this.time.get(Calendar.MINUTE)
    val quarter get() = this.time.get(Calendar.MINUTE) / 15

    override fun equals(other: Any?): Boolean = (other is ReportTime) && time == other.time

    override fun toString(): String {
        //val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        //println(this.time.get(Calendar.HOUR_OF_DAY))
        //println(this.time.time.hours)
        //return format.format(this.time.time).toString()
        return String.format("%04d-%02d-%02dT%02d:%02d:00Z",
            this.year, this.month + 1, this.day, this.hour, this.minute)
    }
}

fun makeReportTimeFromComponents(
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    quarter: Int,
): ReportTime {
    val calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, quarter * 15)
    calendar.set(Calendar.SECOND, 0)

    return ReportTime(calendar)
}

fun makeReportTimeFromString(dateStr: String): ReportTime {
    val calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))
    var s = dateStr.replace("Z", "+00:00")
    try {
        s = s.substring(0, 22) + s.substring(23)  // to get rid of the ":"
    } catch (e: IndexOutOfBoundsException) {
        throw ParseException("Invalid length", 0)
    }
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s)
    if (date == null)
        throw FormatException()
    calendar.time = date

    return ReportTime(calendar)
}

data class SatReport(
    val name: String,
    val report: Report,
    val time: ReportTime,
    val callsign: String,
    val gridSquare: String = "",
)

data class SatReportSlot(
    val name: String,
    val report: Report,
    val time: ReportTime,
    val reports: List<SatReport>,
)
