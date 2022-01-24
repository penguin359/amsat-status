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

package org.northwinds.amsatstatus

import android.util.Log

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.UrlEncodedContent
import com.google.api.client.http.javanet.NetHttpTransport
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import javax.inject.Inject

const val TAG = "AmsatStatus-AmsatApi"

const val AMSAT_API_URL = "https://amsat.org/status/api/v1/sat_info.php"
const val AMSAT_API_POST_URL = "https://amsat.org/status/submit.php"

private const val DEMO_SAT_REPORT = """[
    {
        "name": "DEMO-2",
        "reported_time": "2018-02-27T02:00:00Z",
        "callsign": "AB1C",
        "report": "Heard",
        "grid_square": "AB34"
    },
    {
        "name": "DEMO-1",
        "reported_time": "2018-02-27T03:00:00Z",
        "callsign": "K7IW",
        "report": "Not Heard",
        "grid_square": "CN85nu"
    },
    {
        "name": "DEMO-1",
        "reported_time": "2018-02-27T03:15:00Z",
        "callsign": "ZL1D",
        "report": "Telemetry Only",
        "grid_square": "CN96az"
    },
    {
        "name": "DEMO-1",
        "reported_time": "2018-02-27T04:30:00Z",
        "callsign": "KG7GAN",
        "report": "Crew Active",
        "grid_square": "DM43"
    },
    {
        "name": "DEMO-1",
        "reported_time": "2018-02-27T05:45:00Z",
        "callsign": "AG7NC",
        "report": "Heard",
        "grid_square": "CM59ax"
    },
    {
        "name": "DEMO-1",
        "reported_time": "2018-02-27T06:30:00Z",
        "callsign": "OM/DL1IBM",
        "report": "Heard",
        "grid_square": "DM57"
    },
    {
        "name": "DEMO-1",
        "reported_time": "2018-02-27T06:30:00Z",
        "callsign": "ZA1FG",
        "report": "Not Heard",
        "grid_square": "EA85as"
    }
]"""

open class AmsatApi(private val client: HttpTransport) {
    constructor() : this(NetHttpTransport())

    fun getReport(name: String, hours: Int) : List<SatReport> {
        Log.v(TAG, "Requesting report for " + name)
        val uri = GenericUrl(AMSAT_API_URL)
        uri.`set`("name", name)
        uri.`set`("hours", hours)
        Log.d(TAG, "To build")
        val rawResponse = if(name == "DEMO-1") {
            DEMO_SAT_REPORT
        } else {
            val httpGet = client.createRequestFactory().buildGetRequest(uri)
            httpGet.headers.userAgent = "AMSATStatus/1.0"
            Log.d(TAG, "Done")
            Log.d(TAG, "To execute")
            try {
                val response = httpGet.execute()
                Log.d(TAG, "Have a response")
                response.parseAsString()
            } catch (ex: Exception) {
                Log.d(TAG, "Died on ex" + ex)
                "[]"
            }
        }
        Log.d(TAG, "Done")
        Log.d(TAG, rawResponse)

        val list = ArrayList<SatReport>()
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

@Module
@InstallIn(SingletonComponent::class)
class AmsatApiModule {
    @Provides
    fun provideAmsatApi(): AmsatApi {
        return AmsatApi()
    }
}