package org.northwinds.amsatstatus

import java.util.Calendar

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

import com.github.paweladamski.httpclientmock.HttpClientMock

class AmsatApiTest {
    @Test fun `instantiate the AMSAT API`() {
        val api = AmsatApi()
    }

    @Test fun `makes the correct URL request to retrieve satellite`() {
        val url = "https://amsat.org/status/api/v1/sat_info.php"
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val api = AmsatApi(httpClientMock)
        api.getReport("AO-91", 24)
        httpClientMock.verify().get(url)
        .withParameter("name", "AO-91")
        .withParameter("hours", "24")
        .called()
    }

    @Test fun `makes the correct URL request to retrieve alternate satellite`() {
        val url = "https://amsat.org/status/api/v1/sat_info.php"
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val api = AmsatApi(httpClientMock)
        api.getReport("FO-29", 6)
        httpClientMock.verify().get(url)
        .withParameter("name", "FO-29")
        .withParameter("hours", "6")
        .called()
    }

    @Test fun `can instantiate a satellite report`() {
        val timeStr = "2018-02-27T02:00:00Z"
        val time = makeReportTimeFromString(timeStr)
        val report = SatReport("NO-84", Report.HEARD, time, "AB1CD", "CN85")
        assertEquals("NO-84", report.name)
        assertEquals("Heard", report.report.value)
        assertEquals(timeStr, report.time.toString())
        assertEquals("AB1CD", report.callsign)
        assertEquals("CN85", report.gridSquare)
    }

    @Test fun `can instantiate a satellite report without a grid`() {
        val timeStr = "2022-12-01T08:45:00Z"
        val time = makeReportTimeFromString(timeStr)
        val report = SatReport("PICSat-1", Report.CREW_ACTIVE, time, "X1YZ")
        assertEquals("PICSat-1", report.name)
        assertEquals("Crew Active", report.report.value)
        assertEquals(timeStr, report.time.toString())
        assertEquals("X1YZ", report.callsign)
        assertEquals("", report.gridSquare)
    }

    @Test fun `can get report enum from string`() {
        assertEquals(Report.HEARD, reportFromString("Heard"))
        assertEquals(Report.NOT_HEARD, reportFromString("not heard"))
        assertEquals(Report.CREW_ACTIVE, reportFromString("CREW ACTIVE"))
        assertEquals(Report.TELEMETRY_ONLY, reportFromString("TeLeMeTrY OnLy"))
    }

    @Test fun `will return not heard report enum from unknown string`() {
        assertEquals(Report.NOT_HEARD, reportFromString("invalid string"))
    }

    @Test fun `can get string from report enum`() {
        /* These are the exact strings as used in the AMSAT API */
        assertEquals("Heard", Report.HEARD.value)
        assertEquals("Not Heard", Report.NOT_HEARD.value)
        assertEquals("Crew Active", Report.CREW_ACTIVE.value)
        assertEquals("Telemetry Only", Report.TELEMETRY_ONLY.value)
    }

    @Test fun `can create ReportTime from Calendar`() {
        val calendar = Calendar.getInstance()
        val time = ReportTime(calendar)

        assertEquals(calendar.get(Calendar.YEAR), time.year)
        assertEquals(calendar.get(Calendar.MONTH)+1, time.month+1)
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), time.day)
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), time.hour)
        assertEquals(calendar.get(Calendar.MINUTE), time.minute)
        assertEquals(calendar.get(Calendar.MINUTE) / 15, time.quarter)
    }

    @Test fun `can create ReportTime from components`() {
        val time = makeReportTimeFromComponents(
            year = 2020,
            month = 6,
            day = 12,
            hour = 17,
            quarter = 3)

        assertEquals(2020, time.year)
        assertEquals(6+1, time.month+1)
        assertEquals(12, time.day)
        assertEquals(17, time.hour)
        assertEquals(45, time.minute)
        assertEquals(3, time.quarter)
    }

    @Test fun `can create ReportTime from string`() {
        val dateStr = "2019-10-05T21:15:00Z"
        val time = makeReportTimeFromString(dateStr)

        assertEquals(2019, time.year)
        assertEquals(10, time.month+1)
        assertEquals(5, time.day)
        assertEquals(21, time.hour)
        assertEquals(15, time.minute)
        assertEquals(1, time.quarter)
    }

    @Test fun `can convert ReportTime to text`() {
        val time = makeReportTimeFromComponents(
            year = 2021,
            month = 3-1,
            day = 29,
            hour = 23,
            quarter = 2)

        assertEquals("2021-03-29T23:30:00Z", time.toString())
    }

    @Test fun `returns details on AO-91 satellite`() {
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val api = AmsatApi(httpClientMock)
        api.getReport("AO-91", 24)
    }
}
