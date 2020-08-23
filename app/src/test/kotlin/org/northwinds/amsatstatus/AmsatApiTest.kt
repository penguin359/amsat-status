package org.northwinds.amsatstatus

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

import com.github.paweladamski.httpclientmock.HttpClientMock

//import org.northwinds.amsatstatus.SatReport

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
        val report = SatReport("NO-84", Report.HEARD, "", "AB1CD", "CN85")
        assertEquals("NO-84", report.name)
        assertEquals("Heard", report.report.value)
        assertEquals("", report.time)
        assertEquals("AB1CD", report.callsign)
        assertEquals("CN85", report.gridSquare)
    }

    @Test fun `can instantiate a satellite report without a grid`() {
        val report = SatReport("PICSat-1", Report.CREW_ACTIVE, "", "X1YZ")
        assertEquals("PICSat-1", report.name)
        assertEquals("Crew Active", report.report.value)
        assertEquals("", report.time)
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

    @Test fun `returns details on AO-91 satellite`() {
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val api = AmsatApi(httpClientMock)
        api.getReport("AO-91", 24)
    }
}
