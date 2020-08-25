package org.northwinds.amsatstatus

import java.io.BufferedReader
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import org.json.JSONTokener
import org.json.JSONObject
import org.json.JSONArray

const val AMSAT_API_URL = "https://amsat.org/status/api/v1/sat_info.php"
const val AMSAT_API_POST_URL = "https://amsat.org/status/submit.php"

class AmsatApi(client: CloseableHttpClient) {

    val client = client

    constructor() : this(HttpClients.createDefault())

    fun getReport(name: String, hours: Int) : List<SatReport> {
        val uri = URIBuilder(AMSAT_API_URL)
        uri.addParameter("name", name)
        uri.addParameter("hours", hours.toString())
        val httpGet = HttpGet(uri.build())
        var list = ArrayList<SatReport>()
        this.client.execute(httpGet).use { response1 ->
            val entity1 = response1.getEntity()
            val body = BufferedInputStream(entity1.getContent())
            val textBuilder = StringBuilder()
            BufferedReader(InputStreamReader
                (body, Charset.forName("UTF-8"))).use { reader ->
                var c = reader.read()
                while (c != -1) {
                    textBuilder.append(c.toChar())
                    c = reader.read()
                }
            }

            val jsonList = JSONTokener(textBuilder.toString()).nextValue() as JSONArray

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
            EntityUtils.consume(entity1)
        }

        return list
    }

    fun sendReport(report: SatReport) {
        val httpPost = HttpPost(AMSAT_API_POST_URL)

        val postParameters = ArrayList<NameValuePair>()
        postParameters.add(BasicNameValuePair("SatName", report.name))
        postParameters.add(BasicNameValuePair("SatReport", report.report.value))
        postParameters.add(BasicNameValuePair("SatYear", report.time.year.toString()))
        postParameters.add(BasicNameValuePair("SatMonth", (report.time.month+1).toString().padStart(2, '0')))
        postParameters.add(BasicNameValuePair("SatDay", report.time.day.toString()))
        postParameters.add(BasicNameValuePair("SatHour", report.time.hour.toString()))
        postParameters.add(BasicNameValuePair("SatPeriod", report.time.quarter.toString()))
        postParameters.add(BasicNameValuePair("SatCall", report.callsign))
        postParameters.add(BasicNameValuePair("SatGridSquare", report.gridSquare))
        postParameters.add(BasicNameValuePair("SatSubmit", "yes"))
        postParameters.add(BasicNameValuePair("Confirm", "yes"))

        httpPost.setEntity(UrlEncodedFormEntity(postParameters, "UTF-8"))
        this.client.execute(httpPost).use { response1 ->
            println("Posting report")
            println(report.toString())
            val entity1 = response1.getEntity()
            EntityUtils.consume(entity1)
        }
    }
}
