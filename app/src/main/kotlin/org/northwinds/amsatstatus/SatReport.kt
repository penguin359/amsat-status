package org.northwinds.amsatstatus

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

data class SatReport(
    val name: String,
    val report: Report,
    val time: String,
    val callsign: String,
    val gridSquare: String = "")
