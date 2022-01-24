/**********************************************************************************
 * Copyright (c) 2022 Loren M. Lang                                               *
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

import kotlin.test.*

class SatReportTest {
    @Test
    fun `can create ReportTime object from zero timestamp`() {
        val time = ReportTime(0)
        assertEquals(1970, time.year, "Year")
        assertEquals(1, time.month + 1, "Month")
        assertEquals(1, time.day, "Day")
        assertEquals(0, time.hour, "Hour")
        assertEquals(0, time.minute, "Minute")
        assertEquals(0, time.quarter, "Quarter")
    }

    @Test
    fun `can create ReportTime object from recent timestamp`() {
        val time = ReportTime(1640648700L * 1000)  // UTC: 2021-12-27T23:45:00Z
        assertEquals(2021, time.year, "Year")
        assertEquals(12, time.month + 1, "Month")
        assertEquals(27, time.day, "Day")
        assertEquals(23, time.hour, "Hour")
        assertEquals(45, time.minute, "Minute")
        assertEquals(3, time.quarter, "Quarter")
    }

    @Test
    fun `ReportTime is equal to itself`() {
        val expected = ReportTime(1640648700L * 1000)  // UTC: 2021-12-27T23:45:00Z
        val actual = expected
        assertTrue("Timestamps should match") { expected == actual }
        assertTrue("Timestamps should match") { expected == actual }
        assertEquals(expected, actual)
    }

    @Test
    fun `ReportTime is equal an identical timestamp`() {
        val expected = ReportTime(1640648700L * 1000)  // UTC: 2021-12-27T23:45:00Z
        val actual = ReportTime(1640648700L * 1000)
        assertTrue("Timestamps should match") { expected == actual }
        assertTrue("Timestamps should match") { expected == actual }
        assertEquals(expected, actual)
    }

    @Test
    fun `ReportTime is not equal to a different timestamp`() {
        val expected = ReportTime(1640648700L * 1000)  // UTC: 2021-12-27T23:45:00Z
        val actual = ReportTime(3600L * 1000)
        assertFalse("Timestamps should not match") { expected == actual }
        assertFalse("Timestamps should not match") { expected == actual }
        assertNotEquals(expected, actual)
    }

    @Test
    fun `ReportTime is not equal to null`() {
        val expected = ReportTime(1640648700L * 1000)  // UTC: 2021-12-27T23:45:00Z
        val actual: ReportTime? = null
        assertFalse("Timestamps should not match") { expected == actual }
        assertFalse("Timestamps should not match") { expected == actual }
        assertNotEquals(expected, actual)
    }
}