package org.northwinds.amsatstatus

import org.hamcrest.MatcherAssert.assertThat
import kotlin.test.*

class SatReportTest {
    @Test fun `can create ReportTime object from zero timestamp`() {
        val time = ReportTime(0)
        assertEquals(1970, time.year, "Year")
        assertEquals(1, time.month+1, "Month")
        assertEquals(1, time.day, "Day")
        assertEquals(0, time.hour, "Hour")
        assertEquals(0, time.minute, "Minute")
        assertEquals(0, time.quarter, "Quarter")
    }

    @Test fun `can create ReportTime object from recent timestamp`() {
        val time = ReportTime(1640648700L*1000)  // UTC: 2021-12-27T23:45:00Z
        assertEquals(2021, time.year, "Year")
        assertEquals(12, time.month+1, "Month")
        assertEquals(27, time.day, "Day")
        assertEquals(23, time.hour, "Hour")
        assertEquals(45, time.minute, "Minute")
        assertEquals(3, time.quarter, "Quarter")
    }

    @Test fun `ReportTime is equal to itself`() {
        val expected = ReportTime(1640648700L*1000)  // UTC: 2021-12-27T23:45:00Z
        val actual = expected
        assertTrue("Timestamps should match") { expected.equals(actual) }
        assertTrue("Timestamps should match") { expected == actual }
        assertEquals(expected, actual)
    }

    @Test fun `ReportTime is equal an identical timestamp`() {
        val expected = ReportTime(1640648700L*1000)  // UTC: 2021-12-27T23:45:00Z
        val actual = ReportTime(1640648700L*1000)
        assertTrue("Timestamps should match") { expected.equals(actual) }
        assertTrue("Timestamps should match") { expected == actual }
        assertEquals(expected, actual)
    }

    @Test fun `ReportTime is not equal to a different timestamp`() {
        val expected = ReportTime(1640648700L*1000)  // UTC: 2021-12-27T23:45:00Z
        val actual = ReportTime(3600L*1000)
        assertFalse("Timestamps should not match") { expected.equals(actual) }
        assertFalse("Timestamps should not match") { expected == actual }
        assertNotEquals(expected, actual)
    }

    @Test fun `ReportTime is not equal to null`() {
        val expected = ReportTime(1640648700L*1000)  // UTC: 2021-12-27T23:45:00Z
        val actual: ReportTime? = null
        assertFalse("Timestamps should not match") { expected.equals(actual) }
        assertFalse("Timestamps should not match") { expected == actual }
        assertNotEquals(expected, actual)
    }
}