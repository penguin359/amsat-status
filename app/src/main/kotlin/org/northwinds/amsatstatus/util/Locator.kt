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

import java.lang.RuntimeException

data class Position(val latitude: Double, val longitude: Double)

class Locator {
    companion object {
        fun coord_to_grid(lat: Double, lon: Double): String {
            val real_lon = lon + 180
            val real_lat = lat + 90

            if (real_lon < 0 || real_lon > 180)
                throw RuntimeException("Longitude out of range")
            if (real_lat < 0 || real_lat > 180)
                throw RuntimeException("Latitude out of range")

            var int_lon = (real_lon * 12 * 10 * 24).toInt()
            var int_lat = (real_lat * 24 * 10 * 24).toInt()
            if (int_lon >= 18 * 10 * 24 * 10 * 24)
                int_lon = 18 * 10 * 24 * 10 * 24 - 1
            if (int_lat >= 18 * 10 * 24 * 10 * 24)
                int_lat = 18 * 10 * 24 * 10 * 24 - 1
            val field_lon = int_lon / (10 * 24 * 10 * 24)
            var remain_lon = int_lon % (10 * 24 * 10 * 24)
            val square_lon = remain_lon / (24 * 10 * 24)
            remain_lon = remain_lon % (24 * 10 * 24)
            val subsquare_lon = remain_lon / (10 * 24)
            remain_lon = remain_lon % (10 * 24)
            val extsquare_lon = remain_lon / (24)
            val a_lon = remain_lon % (24)
            val field_lat = int_lat / (10 * 24 * 10 * 24)
            var remain_lat = int_lat % (10 * 24 * 10 * 24)
            val square_lat = remain_lat / (24 * 10 * 24)
            remain_lat = remain_lat % (24 * 10 * 24)
            val subsquare_lat = remain_lat / (10 * 24)
            remain_lat = remain_lat % (10 * 24)
            val extsquare_lat = remain_lat / (24)
            val a_lat = remain_lat % (24)
            return "${('A' + field_lon).toChar()}${('A' + field_lat).toChar()}${square_lon}${square_lat}${('a' + subsquare_lon).toChar()}${('a' + subsquare_lat).toChar()}$extsquare_lon$extsquare_lat${('a' + a_lon).toChar()}${('a' + a_lat).toChar()}"
        }

        fun grid_to_coord(grid2: String, corner: String = ""): Position {
            val grid = grid2.trim().toLowerCase()
            if (!setOf(2, 4, 6, 8, 10).contains(grid.length))
                throw RuntimeException("Not a grid square")
            val field_lon = (grid[0] - 'a').toInt()
            val field_lat = (grid[1] - 'a').toInt()
            var center_offset = 5 * 24 * 10 * 24
            var square_lon = 0
            var square_lat = 0
            if (grid.length > 2) {
                square_lon = (grid[2] - '0').toInt()
                square_lat = (grid[3] - '0').toInt()
                center_offset = 12 * 10 * 24
            }
            var subsquare_lon = 0
            var subsquare_lat = 0
            if (grid.length > 4) {
                subsquare_lon = (grid[4] - 'a').toInt()
                subsquare_lat = (grid[5] - 'a').toInt()
                center_offset = 5 * 24
            }
            var extsquare_lon = 0
            var extsquare_lat = 0
            if (grid.length > 6) {
                extsquare_lon = (grid[6] - '0').toInt()
                extsquare_lat = (grid[7] - '0').toInt()
                center_offset = 12
            }
            var a_lon = 0
            var a_lat = 0
            if (grid.length > 8) {
                a_lon = (grid[8] - 'a').toInt()
                a_lat = (grid[9] - 'a').toInt()
                center_offset = 0
            }
            var lon = field_lon
            var lat = field_lat
            lon *= 10
            lat *= 10
            lon += square_lon
            lat += square_lat
            lon *= 24
            lat *= 24
            lon += subsquare_lon
            lat += subsquare_lat
            lon *= 10
            lat *= 10
            lon += extsquare_lon
            lat += extsquare_lat
            lon *= 24
            lat *= 24
            lon += a_lon
            lat += a_lat
            if (corner.contains('E')) {
                lon += 2 * center_offset
            } else if (!corner.contains('W')) {
                lon += center_offset
            }
            if (corner.contains('N')) {
                lat += 2 * center_offset
            } else if (!corner.contains('S')) {
                lat += center_offset
            }
            var real_lon = lon.toDouble() / (12 * 10 * 24)
            var real_lat = lat.toDouble() / (24 * 10 * 24)
            real_lon -= 180
            real_lat -= 90
            return Position(real_lat, real_lon)
        }
    }
}

//class Position:
//    pass
//
//class LocatorPosition(Position):
//    def __init__(self, grid):
//        self.grid = grid
//
//    def grid(self):
//        return self.grid
//
//    def coord(self):
//        return grid_to_coord(self.grid)
//
//class CoordinatePosition(Position):
//    def __init__(self, lat, lon):
//        self.lat = lat
//        self.lon = long
//
//    def grid(self):
//        return coord_to_grid(self.lat, self.lon)
