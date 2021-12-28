package org.northwinds.amsatstatus

import android.util.Log

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.UrlEncodedContent
import com.google.api.client.http.javanet.NetHttpTransport
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

const val TAG = "AmsatApi"

const val AMSAT_API_URL = "https://amsat.org/status/api/v1/sat_info.php"
const val AMSAT_API_POST_URL = "https://amsat.org/status/submit.php"

open class AmsatApi(private val client: HttpTransport) {
    constructor() : this(NetHttpTransport())

    fun getReport(name: String, hours: Int) : List<SatReport> {
        Log.v(TAG, "Requesting report for " + name)
        val uri = GenericUrl(AMSAT_API_URL)
        uri.`set`("name", name)
        uri.`set`("hours", hours)
        Log.d(TAG, "To build")
        val httpGet = client.createRequestFactory().buildGetRequest(uri)
        httpGet.headers.userAgent = "AMSATStatus/1.0"
        Log.d(TAG, "Done")
        var list = ArrayList<SatReport>()
        Log.d(TAG, "To execute")
        var rawResponse = "[]"
        try {
                rawResponse = httpGet.execute().parseAsString()
        } catch(ex: Exception) {
                Log.d(TAG, "Died on ex" + ex)
        }
        Log.d(TAG, "Done")
        Log.d(TAG, rawResponse)

            val jsonList = JSONTokener(rawResponse).nextValue() as JSONArray

            for(idx in 0..jsonList.length()-1) {
                val entry = jsonList.get(idx) as JSONObject
                val satName = entry.getString("name")
                val callsign = entry.getString("callsign")
                val grid = entry.getString("grid_square")
                val timeStr = entry.getString("reported_time")
                val time = makeReportTimeFromString(timeStr)
                val reportStr = entry.getString("report")
                var report = reportFromString(reportStr)
                val reportObj = SatReport(satName, report, time, callsign, grid)
                list.add(reportObj)
            }

        return list
    }

    fun getReportsBySlot(name: String, hours: Int) : List<SatReportSlot> {
        val groups = ArrayList<SatReportSlot>()
        val all_reports = getReport(name, hours)
        if(all_reports.size == 0)
            return groups
        var reports = ArrayList<SatReport>()
        var group_report = all_reports[0].report
        var group_time = all_reports[0].time
        for(report in all_reports) {
                if(report.time == group_time) {
                    if(report.report != group_report) {
                        group_report = Report.CONFLICTED
                    }
                } else {
                    val group = SatReportSlot(name, group_report, group_time, reports)
                    groups.add(group)
                    reports = ArrayList<SatReport>()
                    group_report = report.report
                    group_time = report.time
                }
                reports.add(report)
        }
        if(reports.size > 0) {
            val group = SatReportSlot(name, group_report, group_time, reports)
            groups.add(group)
        }
        return groups
//        return listOf(SatReportSlot(name, Report.NOT_HEARD, ReportTime(0), listOf()))
    }

    open fun sendReport(report: SatReport) {
        val uri = GenericUrl(AMSAT_API_POST_URL)
        val params: MutableMap<String, String> = LinkedHashMap()
        params["SatName"] = report.name
        params["SatReport"] = report.report.value
        params["SatYear"] = report.time.year.toString()
        params["SatMonth"] = (report.time.month+1).toString()
        params["SatDay"] = report.time.day.toString()
        params["SatHour"] = report.time.hour.toString()
        params["SatPeriod"] = report.time.quarter.toString()
        params["SatCall"] = report.callsign
        params["SatGridSquare"] = report.gridSquare
        params["SatSubmit"] = "yes"
        params["Confirm"] = "yes"

        val content = UrlEncodedContent(params)
        val httpPost = client.createRequestFactory().buildPostRequest(uri, content)
        httpPost.headers.userAgent = "AMSATStatus/1.0"
        val rawResponse = httpPost.execute().parseAsString()
        Log.v(TAG, "Posting report")
        Log.v(TAG, report.toString())
    }
}
