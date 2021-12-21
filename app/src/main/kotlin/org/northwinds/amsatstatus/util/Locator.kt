package org.northwinds.amsatstatus.util

import java.lang.RuntimeException

class Locator {
    companion object {
        fun coord_to_grid(lat: Double, lon: Double): String
        {
            val real_lon = lon + 180
            val real_lat = lat + 90

            if (real_lon < 0 || real_lon > 180)
                throw RuntimeException ("Longitude out of range")
            if (real_lat < 0 || real_lat > 180)
                throw RuntimeException ("Latitude out of range")

            var int_lon = (real_lon * 12 * 10 * 24).toInt()
            var int_lat = (real_lat * 24 * 10 * 24).toInt()
            if(int_lon >= 18 * 10 * 24 * 10 * 24)
                int_lon = 18 * 10 * 24 * 10 * 24 - 1
            if(int_lat >= 18 * 10 * 24 * 10 * 24)
                int_lat = 18 * 10 * 24 * 10 * 24 - 1
            val field_lon = int_lon / (10*24*10*24)
            var remain_lon = int_lon % (10*24*10*24)
            val square_lon = remain_lon / (24*10*24)
            remain_lon = remain_lon % (24*10*24)
            val subsquare_lon = remain_lon / (10*24)
            remain_lon = remain_lon % (10*24)
            val extsquare_lon = remain_lon / (24)
            val a_lon = remain_lon % (24)
            val field_lat = int_lat / (10*24*10*24)
            var remain_lat = int_lat % (10*24*10*24)
            val square_lat = remain_lat / (24*10*24)
            remain_lat = remain_lat % (24*10*24)
            val subsquare_lat = remain_lat / (10*24)
            remain_lat = remain_lat % (10*24)
            val extsquare_lat = remain_lat / (24)
            val a_lat = remain_lat % (24)
            return "${('A'+field_lon).toChar()}${('A'+field_lat).toChar()}${square_lon}${square_lat}${('a'+subsquare_lon).toChar()}${('a'+subsquare_lat).toChar()}$extsquare_lon$extsquare_lat${('a'+a_lon).toChar()}${('a'+a_lat).toChar()}"
        }
    }
}

//def grid_to_coord(grid, corner=''):
//    grid = grid.strip().lower()
//    if len(grid) not in (2, 4, 6, 8, 10):
//        raise ValueError('Not a grid square')
//    field_lon = ord(grid[0]) - ord('a')
//    field_lat = ord(grid[1]) - ord('a')
//    center_offset = 5*24*10*24
//    if len(grid) > 2:
//        square_lon = int(grid[2])
//        square_lat = int(grid[3])
//        center_offset = 12*10*24
//    else:
//        square_lon = 0
//        square_lat = 0
//    if len(grid) > 4:
//        subsquare_lon = ord(grid[4]) - ord('a')
//        subsquare_lat = ord(grid[5]) - ord('a')
//        center_offset = 5*24
//    else:
//        subsquare_lon = 0
//        subsquare_lat = 0
//    if len(grid) > 6:
//        extsquare_lon = int(grid[6])
//        extsquare_lat = int(grid[7])
//        center_offset = 12
//    else:
//        extsquare_lon = 0
//        extsquare_lat = 0
//    if len(grid) > 8:
//        a_lon = ord(grid[8]) - ord('a')
//        a_lat = ord(grid[9]) - ord('a')
//        center_offset = 0
//    else:
//        a_lon = 0
//        a_lat = 0
//    lon = field_lon
//    lat = field_lat
//    lon *= 10
//    lat *= 10
//    lon += square_lon
//    lat += square_lat
//    lon *= 24
//    lat *= 24
//    lon += subsquare_lon
//    lat += subsquare_lat
//    lon *= 10
//    lat *= 10
//    lon += extsquare_lon
//    lat += extsquare_lat
//    lon *= 24
//    lat *= 24
//    lon += a_lon
//    lat += a_lat
//    if 'E' in corner:
//        lon += 2*center_offset
//    elif 'W' not in corner:
//        lon += center_offset
//    if 'N' in corner:
//        lat += 2*center_offset
//    elif 'S' not in corner:
//        lat += center_offset
//    lon /= 12*10*24
//    lat /= 24*10*24
//    lon -= 180
//    lat -= 90
//    return lat, lon
//
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
//
//
//class LocatorTest(unittest.TestCase):
//    def test_grid_to_coord_10char(self):
//        lat, lon = grid_to_coord('CN85NU94FG')
//        self.assertAlmostEqual(lat, 45.851151, delta=1/(1*24*10*24))
//        self.assertAlmostEqual(lon, -122.839653, delta=1/(0.5*24*10*24))
//
//    def test_grid_to_coord_4char(self):
//        lat, lon = grid_to_coord('CN85', corner='SW')
//        self.assertEqual(lat, 45.)
//        self.assertEqual(lon, -124.)
//
//        lat, lon = grid_to_coord('CN96', corner='SW')
//        self.assertEqual(lat, 46.)
//        self.assertEqual(lon, -122.)
//
//    def test_grid_to_coord_4char_offset(self):
//        lat, lon = grid_to_coord('CN85', corner='NE')
//        self.assertEqual(lat, 46.)
//        self.assertEqual(lon, -122.)
//
//        lat, lon = grid_to_coord('CN85', corner='NW')
//        self.assertEqual(lat, 46.)
//        self.assertEqual(lon, -124.)
//
//        lat, lon = grid_to_coord('CN85', corner='SE')
//        self.assertEqual(lat, 45.)
//        self.assertEqual(lon, -122.)
//
//    def test_grid_to_coord_4char_center(self):
//        lat, lon = grid_to_coord('CN85', corner='')
//        self.assertEqual(lat, 45.5)
//        self.assertEqual(lon, -123.)
//
//
//if __name__ == '__main__':
//    lat = 45.851151
//    lon = -122.839653
//    print(coord_to_grid(lat, lon))
//    assert coord_to_grid(lat, lon).upper() == 'CN85NU94FG'
//    unittest.main()
//    print(grid_to_coord('CN85NU94FG'))
//    print(grid_to_coord('CN85'))
