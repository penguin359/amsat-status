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

import com.github.paweladamski.httpclientmock.HttpClientMock
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import org.hamcrest.core.StringContains
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class AmsatApiTest {
    @Test
    fun `instantiate the AMSAT API`() {
        val api = AmsatApi()
    }

    @Test
    fun `makes the correct URL request to retrieve satellite`() {
        val url = "https://amsat.org/status/api/v1/sat_info.php"
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)
        api.getReport("AO-91", 24)
        httpClientMock.verify().get(url)
            .withParameter("name", "AO-91")
            .withParameter("hours", "24")
            .called()
    }

    @Test
    fun `makes the correct URL request to retrieve alternate satellite`() {
        val url = "https://amsat.org/status/api/v1/sat_info.php"
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)
        api.getReport("FO-29", 6)
        httpClientMock.verify().get(url)
            .withParameter("name", "FO-29")
            .withParameter("hours", "6")
            .called()
    }

    @Test
    fun `can instantiate a satellite report`() {
        val timeStr = "2018-02-27T02:00:00Z"
        val time = makeReportTimeFromString(timeStr)
        val report = SatReport("NO-84", Report.HEARD, time, "AB1CD", "CN85")
        assertEquals("NO-84", report.name)
        assertEquals("Heard", report.report.value)
        assertEquals(timeStr, report.time.toString())
        assertEquals("AB1CD", report.callsign)
        assertEquals("CN85", report.gridSquare)
    }

    @Test
    fun `can instantiate a satellite report without a grid`() {
        val timeStr = "2022-12-01T08:45:00Z"
        val time = makeReportTimeFromString(timeStr)
        val report = SatReport("PICSat-1", Report.CREW_ACTIVE, time, "X1YZ")
        assertEquals("PICSat-1", report.name)
        assertEquals("Crew Active", report.report.value)
        assertEquals(timeStr, report.time.toString())
        assertEquals("X1YZ", report.callsign)
        assertEquals("", report.gridSquare)
    }

    @Test
    fun `can get report enum from string`() {
        assertEquals(Report.HEARD, reportFromString("Heard"))
        assertEquals(Report.NOT_HEARD, reportFromString("not heard"))
        assertEquals(Report.CREW_ACTIVE, reportFromString("CREW ACTIVE"))
        assertEquals(Report.TELEMETRY_ONLY, reportFromString("TeLeMeTrY OnLy"))
    }

    @Test
    fun `will return not heard report enum from unknown string`() {
        assertEquals(Report.NOT_HEARD, reportFromString("invalid string"))
    }

    @Test
    fun `can get string from report enum`() {
        /* These are the exact strings as used in the AMSAT API */
        assertEquals("Heard", Report.HEARD.value)
        assertEquals("Not Heard", Report.NOT_HEARD.value)
        assertEquals("Crew Active", Report.CREW_ACTIVE.value)
        assertEquals("Telemetry Only", Report.TELEMETRY_ONLY.value)
    }

    @Test
    fun `can create ReportTime from Calendar`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val time = ReportTime(calendar)

        assertEquals(calendar.get(Calendar.YEAR), time.year, "Bad Year")
        assertEquals(calendar.get(Calendar.MONTH) + 1, time.month + 1, "Bad Month")
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), time.day, "Bad Day")
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), time.hour, "Bad Hour")
        assertEquals(calendar.get(Calendar.MINUTE), time.minute, "Bad Minute")
        assertEquals(calendar.get(Calendar.MINUTE) / 15, time.quarter, "Bad Quarter")
    }

    @Test
    fun `can create ReportTime from UNIX timestamp`() {
        // Make sure test is not affected by local time zone
        TimeZone.setDefault(TimeZone.getTimeZone("MST"))

        // September 18, 2020 5:24:10 AM (UTC)
        // 2020-09-18T05:24:10Z
        val test_time = 1600406650 * 1000L
        val time = ReportTime(test_time)

        assertEquals(2020, time.year, "Bad Year")
        assertEquals(9, time.month + 1, "Bad Month")
        assertEquals(18, time.day, "Bad Day")
        assertEquals(5, time.hour, "Bad Hour")
        assertEquals(24, time.minute, "Bad Minute")
        assertEquals(24 / 15, time.quarter, "Bad Quarter")
    }

    @Test
    fun `can create UTC-based ReportTime from local calendar`() {
        // UTC:   2019-01-01T05:23:45Z
        // Local: 2018-12-31T21:23:45-08:00
        val ref_time = 1546320225 * 1000L
        val ref_timezone = "America/Los_Angeles"

        // Make sure test is not affected by local time zone
        val timezone = TimeZone.getTimeZone(ref_timezone)
        TimeZone.setDefault(timezone)
        val cal = Calendar.getInstance(timezone)
        // Local: 2018-12-31T21:23:45-08:00
        cal.set(Calendar.YEAR, 2018)
        cal.set(Calendar.MONTH, 12 - 1)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        cal.set(Calendar.HOUR_OF_DAY, 21)
        cal.set(Calendar.MINUTE, 23)

        val time = ReportTime(cal)

        assertEquals(2019, time.year, "Bad Year")
        assertEquals(1, time.month + 1, "Bad Month")
        assertEquals(1, time.day, "Bad Day")
        assertEquals(5, time.hour, "Bad Hour")
        assertEquals(23, time.minute, "Bad Minute")
        assertEquals(23 / 15, time.quarter, "Bad Quarter")
    }

    @Test
    fun `can create ReportTime from components`() {
        val time = makeReportTimeFromComponents(
            year = 2020,
            month = 6,
            day = 12,
            hour = 17,
            quarter = 3)

        assertEquals(2020, time.year)
        assertEquals(6 + 1, time.month + 1)
        assertEquals(12, time.day)
        assertEquals(17, time.hour)
        assertEquals(45, time.minute)
        assertEquals(3, time.quarter)
    }

    @Test
    fun `can create ReportTime from string`() {
        val dateStr = "2019-10-05T21:15:00Z"
        val time = makeReportTimeFromString(dateStr)

        assertEquals(2019, time.year)
        assertEquals(10, time.month + 1)
        assertEquals(5, time.day)
        assertEquals(21, time.hour)
        assertEquals(15, time.minute)
        assertEquals(1, time.quarter)
    }

    @Test
    fun `can convert ReportTime to text`() {
        val time = makeReportTimeFromComponents(
            year = 2021,
            month = 3 - 1,
            day = 29,
            hour = 23,
            quarter = 2)

        assertEquals("2021-03-29T23:30:00Z", time.toString())
    }

    @Test
    fun `returns details on AO-91 satellite`() {
        val json = "[{" +
                "\"name\":\"AO-91\"," +
                "\"reported_time\":\"2020-08-20T06:30:00Z\"," +
                "\"callsign\":\"W6WW\"," +
                "\"report\":\"Heard\"," +
                "\"grid_square\":\"DM14\"" +
                "}]"

        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn(json)

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)
        val reports = api.getReport("AO-91", 1)

        assertEquals(1, reports.size)

        val entry = reports.get(0)
        assertEquals("AO-91", entry.name)
        assertEquals("W6WW", entry.callsign)
        assertEquals("DM14", entry.gridSquare)
        assertEquals("2020-08-20T06:30:00Z", entry.time.toString())
        assertEquals(Report.HEARD, entry.report)
    }

    @Test
    fun `returns details on multiple satellites`() {
        val json = """[
            {
                "name": "AO-91",
                "reported_time": "2020-08-20T06:30:00Z",
                "callsign": "W6WW",
                "report": "Heard",
                "grid_square": "DM14"
            },
            {
                "name": "CAS-4A",
                "reported_time": "2020-08-20T04:30:00Z",
                "callsign": "YD0NXX",
                "report": "Heard",
                "grid_square": "OI33js"
            },
            {
                "name": "AO-91",
                "reported_time": "2020-08-19T18:30:00Z",
                "callsign": "KC7MG",
                "report": "Heard",
                "grid_square": "DM42"
            },
            {
                "name": "AO-91",
                "reported_time": "2020-08-19T18:30:00Z",
                "callsign": "AA5PK",
                "report": "Not Heard",
                "grid_square": "DM91"
            },
            {
                "name": "AO-91",
                "reported_time": "2020-08-19T17:30:00Z",
                "callsign": "WA5KBH",
                "report": "Crew Active",
                "grid_square": "EM30"
            },
            {
                "name": "AO-91",
                "reported_time": "2020-08-19T15:30:00Z",
                "callsign": "KB9STR",
                "report": "Telemetry Only",
                "grid_square": "EM69"
            },
            {
                "name": "AO-91",
                "reported_time": "2020-08-19T10:30:00Z",
                "callsign": "OE\/PE4KH",
                "report": "Heard",
                "grid_square": "JN47"
            }
        ]"""

        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn(json)

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)
        val reports = api.getReport("", 24)

        assertEquals(7, reports.size)

        val entry = reports.get(0)
        assertEquals("AO-91", reports.get(0).name)
        assertEquals("2020-08-20T06:30:00Z", reports.get(0).time.toString())
        assertEquals("W6WW", reports.get(0).callsign)
        assertEquals(Report.HEARD, reports.get(0).report)
        assertEquals("DM14", reports.get(0).gridSquare)

        assertEquals("CAS-4A", reports.get(1).name)
        assertEquals("2020-08-20T04:30:00Z", reports.get(1).time.toString())
        assertEquals("OI33js", reports.get(1).gridSquare)

        assertEquals(Report.NOT_HEARD, reports.get(3).report)

        assertEquals("WA5KBH", reports.get(4).callsign)
        assertEquals(Report.CREW_ACTIVE, reports.get(4).report)
        assertEquals("EM30", reports.get(4).gridSquare)

        assertEquals(Report.TELEMETRY_ONLY, reports.get(5).report)

        assertEquals("2020-08-19T10:30:00Z", reports.get(6).time.toString())
        assertEquals("OE/PE4KH", reports.get(6).callsign)
        assertEquals("JN47", reports.get(6).gridSquare)
    }

    @Test
    fun `returns details across multiple requests`() {
        val json = "[{" +
                "\"name\":\"AO-91\"," +
                "\"reported_time\":\"2020-08-20T06:30:00Z\"," +
                "\"callsign\":\"W6WW\"," +
                "\"report\":\"Heard\"," +
                "\"grid_square\":\"DM14\"" +
                "}]"

        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn(json)

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)

        val reports = api.getReport("AO-91", 1)
        assertEquals(1, reports.size)

        val reports2 = api.getReport("AO-91", 1)
        assertEquals(1, reports2.size)
    }

    @Ignore("Google HTTP client does not implement getContent for Apache")
    @Test
    fun `can send report on a satellite`() {
        val httpClientMock = HttpClientMock()
        httpClientMock.onPost().doReturn("Success")

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)
        val timeStr = "2018-02-27T02:15:00Z"
        val time = makeReportTimeFromString(timeStr)
        val report = SatReport("NO-84", Report.TELEMETRY_ONLY, time, "AB1CD", "CN85")
        api.sendReport(report)

        var url = "https://amsat.org/status/submit.php"
        httpClientMock.verify().post(url)
            .withFormParameter("SatName", "NO-84")
            .withFormParameter("SatReport", "Telemetry Only")
            .withFormParameter("SatYear", "2018")
            .withFormParameter("SatMonth", "02")
            .withFormParameter("SatDay", "27")
            .withFormParameter("SatHour", "2")
            .withFormParameter("SatPeriod", "1")
            .withFormParameter("SatCall", "AB1CD")
            .withFormParameter("SatGridSquare", "CN85")
            .withFormParameter("SatSubmit", "yes")
            .withFormParameter("Confirm", "yes")
            .called()
        // /status/submit.php?SatSubmit=yes&Confirm=yes&SatName=CubeBel-1&SatYear=2020&SatMonth=08&SatDay=23&SatHour=11&SatPeriod=0&SatCall=K7IW&SatReport=Not+Heard&SatGridSquare=CN85nu
    }

    @Test
    fun `sends correct user agent when requesting details`() {
        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn("[]")

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)

        api.getReport("AO-91", 24)
        httpClientMock.verify().get().withHeader("User-Agent", StringContains("AMSATStatus/1.0"))
            .called()
    }

    @Ignore("Google HTTP client does not implement getContent for Apache")
    @Test
    fun `sends correct user agent on report submission`() {
        val httpClientMock = HttpClientMock()
        httpClientMock.onPost().doReturn("Success")

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)

        val timeStr = "2018-02-27T02:15:00Z"
        val time = makeReportTimeFromString(timeStr)
        val report = SatReport("NO-84", Report.TELEMETRY_ONLY, time, "AB1CD", "CN85")
        api.sendReport(report)
        httpClientMock.verify().post().withHeader("User-Agent", StringContains("AMSATStatus/1.0"))
            .called()
    }

    @Test
    fun `get reports group by each time slot`() {
        val json = """[
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T09:30:00Z",
                "callsign": "0P1SD",
                "report": "Not Heard",
                "grid_square": "JN47"
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T09:30:00Z",
                "callsign": "AK7NO",
                "report": "Not Heard",
                "grid_square": "BP43"
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T09:45:00Z",
                "callsign": "CN2A",
                "report": "Heard",
                "grid_square": "NA12nl"
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T09:45:00Z",
                "callsign": "AK7NO",
                "report": "Not Heard",
                "grid_square": "BP43"
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T10:30:00Z",
                "callsign": "A1AA",
                "report": "Heard",
                "grid_square": ""
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T10:30:00Z",
                "callsign": "B2BB",
                "report": "Heard",
                "grid_square": "DF87"
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T10:30:00Z",
                "callsign": "C3CC",
                "report": "Heard",
                "grid_square": ""
            },
            {
                "name": "NO-84_PSK",
                "reported_time": "2020-10-02T10:45:00Z",
                "callsign": "D4DD",
                "report": "Not Heard",
                "grid_square": "GH43"
            }
        ]"""

        val httpClientMock = HttpClientMock()
        httpClientMock.onGet().doReturn(json)

        val transport = ApacheHttpTransport(httpClientMock)
        val api = AmsatApi(transport)
        val reportSlots = api.getReportsBySlot("NO-84_PSK", 24)

        assertEquals(4, reportSlots.size)

        reportSlots[0].also {
            assertEquals("NO-84_PSK", it.name)
            assertEquals("2020-10-02T09:30:00Z", it.time.toString())
            assertEquals(Report.NOT_HEARD, it.report)
            assertEquals(2, it.reports.size)
            it.reports[0].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T09:30:00Z", it.time.toString())
                assertEquals(Report.NOT_HEARD, it.report)
                assertEquals("0P1SD", it.callsign)
                assertEquals("JN47", it.gridSquare)
            }
            it.reports[1].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T09:30:00Z", it.time.toString())
                assertEquals(Report.NOT_HEARD, it.report)
                assertEquals("AK7NO", it.callsign)
                assertEquals("BP43", it.gridSquare)
            }
        }

        reportSlots[1].also {
            assertEquals("NO-84_PSK", it.name)
            assertEquals("2020-10-02T09:45:00Z", it.time.toString())
            assertEquals(Report.CONFLICTED, it.report)
            assertEquals(2, it.reports.size)
            it.reports[0].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T09:45:00Z", it.time.toString())
                assertEquals(Report.HEARD, it.report)
                assertEquals("CN2A", it.callsign)
                assertEquals("NA12nl", it.gridSquare)
            }
            it.reports[1].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T09:45:00Z", it.time.toString())
                assertEquals(Report.NOT_HEARD, it.report)
                assertEquals("AK7NO", it.callsign)
                assertEquals("BP43", it.gridSquare)
            }
        }

        reportSlots[2].also {
            assertEquals("NO-84_PSK", it.name)
            assertEquals("2020-10-02T10:30:00Z", it.time.toString())
            assertEquals(Report.HEARD, it.report)
            assertEquals(3, it.reports.size)
            it.reports[0].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T10:30:00Z", it.time.toString())
                assertEquals(Report.HEARD, it.report)
                assertEquals("A1AA", it.callsign)
                assertEquals("", it.gridSquare)
            }
            it.reports[1].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T10:30:00Z", it.time.toString())
                assertEquals(Report.HEARD, it.report)
                assertEquals("B2BB", it.callsign)
                assertEquals("DF87", it.gridSquare)
            }
            it.reports[2].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T10:30:00Z", it.time.toString())
                assertEquals(Report.HEARD, it.report)
                assertEquals("C3CC", it.callsign)
                assertEquals("", it.gridSquare)
            }
        }

        reportSlots[3].also {
            assertEquals("NO-84_PSK", it.name)
            assertEquals("2020-10-02T10:45:00Z", it.time.toString())
            assertEquals(Report.NOT_HEARD, it.report)
            assertEquals(1, it.reports.size)
            it.reports[0].also {
                assertEquals("NO-84_PSK", it.name)
                assertEquals("2020-10-02T10:45:00Z", it.time.toString())
                assertEquals(Report.NOT_HEARD, it.report)
                assertEquals("D4DD", it.callsign)
                assertEquals("GH43", it.gridSquare)
            }
        }
    }

    @Test
    fun `can get demo satellite with default implementation`() {
        val api = AmsatApi()
        val reports = api.getReport("DEMO-1", 24)
        assertEquals(7, reports.size)

        reports[0].also {
            assertEquals("DEMO-2", it.name)
            assertEquals(Report.HEARD, it.report)
            assertEquals("2018-02-27T02:00:00Z", it.time.toString())
            assertEquals("AB1C", it.callsign)
        }

        reports[1].also {
            assertEquals("DEMO-1", it.name)
            assertEquals(Report.NOT_HEARD, it.report)
            assertEquals("2018-02-27T03:00:00Z", it.time.toString())
            assertEquals("K7IW", it.callsign)
        }

        reports[2].also {
            assertEquals("DEMO-1", it.name)
            assertEquals(Report.TELEMETRY_ONLY, it.report)
            assertEquals("2018-02-27T03:15:00Z", it.time.toString())
            assertEquals("ZL1D", it.callsign)
        }

        reports[3].also {
            assertEquals("DEMO-1", it.name)
            assertEquals(Report.CREW_ACTIVE, it.report)
            assertEquals("2018-02-27T04:30:00Z", it.time.toString())
            assertEquals("KG7GAN", it.callsign)
        }

        reports[4].also {
            assertEquals("DEMO-1", it.name)
            assertEquals(Report.HEARD, it.report)
            assertEquals("2018-02-27T05:45:00Z", it.time.toString())
            assertEquals("AG7NC", it.callsign)
        }

        reports[5].also {
            assertEquals("DEMO-1", it.name)
            assertEquals(Report.HEARD, it.report)
            assertEquals("2018-02-27T06:30:00Z", it.time.toString())
            assertEquals("OM/DL1IBM", it.callsign)
        }
    }
}
