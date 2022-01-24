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

package org.northwinds.amsatstatus.util

import kotlin.test.Test
import kotlin.test.assertEquals

class LocatorTest {
    @Test
    fun `returns correct grid square for coordinates`() {
        val lat = 45.851151
        val lon = -122.839653
        print(Locator.coord_to_grid(lat, lon))
        assertEquals("CN85nu94fg", Locator.coord_to_grid(lat, lon))
    }

    @Test
    fun `returns coordinates for 10 char grid square`() {
        val pos = Locator.grid_to_coord("CN85NU94FG")
        assertEquals(45.851151, pos.latitude, absoluteTolerance = 1.0 / (1 * 24 * 10 * 24))
        assertEquals(-122.839653, pos.longitude, absoluteTolerance = 1.0 / (0.5 * 24 * 10 * 24))
    }

    @Test
    fun `test_grid_to_coord_4char`() {
        val pos = Locator.grid_to_coord("CN85", corner = "SW")
        assertEquals(45.0, pos.latitude)
        assertEquals(-124.0, pos.longitude)

        val pos2 = Locator.grid_to_coord("CN96", corner = "SW")
        assertEquals(46.0, pos2.latitude)
        assertEquals(-122.0, pos2.longitude)
    }

    @Test
    fun `test_grid_to_coord_4char_offset`() {
        val pos = Locator.grid_to_coord("CN85", corner = "NE")
        assertEquals(46.0, pos.latitude)
        assertEquals(-122.0, pos.longitude)

        val pos2 = Locator.grid_to_coord("CN85", corner = "NW")
        assertEquals(46.0, pos2.latitude)
        assertEquals(-124.0, pos2.longitude)

        val pos3 = Locator.grid_to_coord("CN85", corner = "SE")
        assertEquals(45.0, pos3.latitude)
        assertEquals(-122.0, pos3.longitude)
    }

    @Test
    fun `test_grid_to_coord_4char_center`() {
        val pos = Locator.grid_to_coord("CN85", corner = "")
        assertEquals(45.5, pos.latitude)
        assertEquals(-123.0, pos.longitude)
    }
}
