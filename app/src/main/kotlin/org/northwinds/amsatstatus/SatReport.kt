package org.northwinds.amsatstatus

import java.text.SimpleDateFormat
import java.text.ParseException
import java.util.Calendar
import java.util.TimeZone
import java.util.GregorianCalendar

import org.northwinds.amsatstatus.Report
import org.northwinds.amsatstatus.SatReport

enum class Report(val value: String) {
    HEARD("Heard"),
    TELEMETRY_ONLY("Telemetry Only"),
    NOT_HEARD("Not Heard"),
    CREW_ACTIVE("Crew Active");
}

fun reportFromString(str: String) : Report {
    for(item in Report.values()) {
        if(item.value.equals(str, true)) {
            return item
        }
    }
    return Report.NOT_HEARD
}

class ReportTime(time: Calendar) {
    val time = time
    val year get() = this.time.get(Calendar.YEAR)
    val month get() = this.time.get(Calendar.MONTH)
    val day get() = this.time.get(Calendar.DAY_OF_MONTH)
    val hour get() = this.time.get(Calendar.HOUR_OF_DAY)
    val minute get() = this.time.get(Calendar.MINUTE)
    val quarter get() = this.time.get(Calendar.MINUTE) / 15

    override fun toString() : String {
        //val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        //println(this.time.get(Calendar.HOUR_OF_DAY))
        //println(this.time.time.hours)
        //return format.format(this.time.time).toString()
        return String.format("%04d-%02d-%02dT%02d:%02d:00Z",
            this.year, this.month+1, this.day, this.hour, this.minute)
    }
}

fun makeReportTimeFromComponents(
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    quarter: Int
) : ReportTime {
    val calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, quarter * 15)
    calendar.set(Calendar.SECOND, 0)

    return ReportTime(calendar)
}

fun makeReportTimeFromString(dateStr: String) : ReportTime {
    val calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))
    var s = dateStr.replace("Z", "+00:00")
    try {
        s = s.substring(0, 22) + s.substring(23)  // to get rid of the ":"
    } catch (e: IndexOutOfBoundsException) {
        throw ParseException("Invalid length", 0)
    }
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s)
    calendar.time = date

    return ReportTime(calendar)
}

data class SatReport(
    val name: String,
    val report: Report,
    val time: ReportTime,
    val callsign: String,
    val gridSquare: String = "")
