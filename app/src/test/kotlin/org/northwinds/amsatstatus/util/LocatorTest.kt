package org.northwinds.amsatstatus.util

import java.util.Calendar
import java.util.TimeZone

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import org.hamcrest.core.StringContains

import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.github.paweladamski.httpclientmock.HttpClientMock
import org.northwinds.amsatstatus.util.Locator

class LocatorTest {
    @Test fun `returns correct grid square for coordinates`() {
        val lat = 45.851151
        val lon = -122.839653
        print(Locator.coord_to_grid(lat, lon))
        assertEquals("CN85nu94fg", Locator.coord_to_grid(lat, lon))
    }

    @Test fun `returns coordinates for 10 char grid square`() {
        val pos = Locator.grid_to_coord("CN85NU94FG")
        assertEquals(45.851151, pos.latitude, absoluteTolerance=1.0/(1*24*10*24))
        assertEquals(-122.839653, pos.longitude, absoluteTolerance=1.0/(0.5*24*10*24))
    }
}
