package org.northwinds.amsatstatus.ui.home

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

import java.util.Calendar
import java.util.TimeZone
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.core.StringContains
import org.junit.Before
import org.junit.Ignore
//import org.mockito.ArgumentCaptor
//import org.mockito.Mockito.mock
//import org.mockito.ArgumentCaptor
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.verify
import org.northwinds.amsatstatus.*

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        PreferenceManager(appContext).sharedPreferences.edit { clear() }
    }

    @Test
    fun dateTimePickersLoadUtcTime() {
        val frag = launchFragmentInContainer<HomeFragment>()

        val utc_time = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        var year = 0
        var month = 0
        var day = 0
        var hour = 0
        var minute = 0
        frag.onFragment {
            val date_widget = it.view!!.findViewById(R.id.date_fixture) as DatePicker
            year = date_widget.year
            month = date_widget.month
            day = date_widget.dayOfMonth
            val time_widget = it.view!!.findViewById<TimePicker>(R.id.time_fixture)
            hour = time_widget.currentHour
            minute = time_widget.currentMinute
            assertEquals("Year", utc_time.get(Calendar.YEAR), date_widget.year)
            assertEquals("Month", utc_time.get(Calendar.MONTH), date_widget.month)
            assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
            assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
            assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, time_widget.currentMinute)
        }
        assertEquals("Year", utc_time.get(Calendar.YEAR), year)
        assertEquals("Month", utc_time.get(Calendar.MONTH), month)
        assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), day)
        assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), hour)
        assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, minute)

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("UTC"))))
    }

    //@Ignore("This test is incomplete")
    @Test
    fun dateTimePickersLoadLocalTime() {
        PreferenceManager(appContext).sharedPreferences.edit {
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        TimeZone.setDefault(TimeZone.getTimeZone("MST"))
        val local_time = Calendar.getInstance(TimeZone.getTimeZone("MST"))
        val frag = launchFragmentInContainer<HomeFragment>()

        frag.onFragment {
            val date_widget = it.view!!.findViewById(R.id.date_fixture) as DatePicker
            val time_widget = it.view!!.findViewById<TimePicker>(R.id.time_fixture)
            assertEquals("Year", local_time.get(Calendar.YEAR), date_widget.year)
            assertEquals("Month", local_time.get(Calendar.MONTH), date_widget.month)
            assertEquals("Day", local_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
            assertEquals("Hour", local_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
            assertEquals("Minute", local_time.get(Calendar.MINUTE) / 15, time_widget.currentMinute)
        }

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("Local"))))
    }

    @Test
    fun verifyDefaultReportFields() {
        val frag = launchFragmentInContainer<HomeFragment>()
        onView(withId(R.id.callsign)).check(matches(withText("")))
        onView(withId(R.id.gridsquare)).check(matches(withText("")))
    }

    @Test
    fun verifyLoadsSavedValuesForReportFields() {
        val EXPECTED_CALLSIGN = "AB1CD"
        val EXPECTED_GRIDSQUARE = "CN85nu"
        PreferenceManager(appContext).sharedPreferences.edit {
            putString(appContext.getString(R.string.preference_callsign), EXPECTED_CALLSIGN)
            putString(appContext.getString(R.string.preference_default_grid), EXPECTED_GRIDSQUARE)
        }
        val frag = launchFragmentInContainer<HomeFragment>()
        onView(withId(R.id.callsign)).check(matches(withText(EXPECTED_CALLSIGN)))
        onView(withId(R.id.gridsquare)).check(matches(withText(EXPECTED_GRIDSQUARE)))
    }

    // UTC:   2019-01-01T05:23:45Z
    // Local: 2018-12-31T21:23:45-08:00
    val ref_time = 1546320225*1000L
    val ref_timezone = "America/Los_Angeles"

    @Test
    fun homeFragmentLoad() {
        val frag = launchFragmentInContainer<HomeFragment>()

        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
        val utc_time = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        var year = 0
        var month = 0
        var day = 0
        var hour = 0
        var minute = 0
        frag.onFragment {
            val date_widget = it.view!!.findViewById(R.id.date_fixture) as DatePicker
            year = date_widget.year
            month = date_widget.month
            day = date_widget.dayOfMonth
            val time_widget = it.view!!.findViewById<TimePicker>(R.id.time_fixture)
            hour = time_widget.currentHour
            minute = time_widget.currentMinute
            assertEquals("Year", utc_time.get(Calendar.YEAR), date_widget.year)
            assertEquals("Month", utc_time.get(Calendar.MONTH), date_widget.month)
            assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
            assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
            assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, time_widget.currentMinute)
        }
        assertEquals("Year", utc_time.get(Calendar.YEAR), year)
        assertEquals("Month", utc_time.get(Calendar.MONTH), month)
        assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), day)
        assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), hour)
        assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, minute)
    }

    @Test
    fun dateTimeSubmitsCorrectFixedUTCTime() {
        PreferenceManager(appContext).sharedPreferences.edit {
            putString("callsign", "A1BC")
            putBoolean(appContext.getString(R.string.preference_local_time), false)
        }

        //val apiMock = mock(AmsatApi::class.java)
        val apiMock = mock<AmsatApi> {}
        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))
        //val utc_time = Calendar.getInstance(TimeZone.getTimeZone("MST"))
        val frag = launchFragmentInContainer<HomeFragment>(instantiate = { HomeFragment(Clock(), apiMock) })

        onView(withId(R.id.submit_button)).perform(scrollTo(), click())

        //var arg: ArgumentCaptor<SatReport> = ArgumentCaptor.forClass(SatReport::class.java)
        //var arg: ArgumentCaptor<SatReport> = argumentCaptor.forClass(SatReport::class.java)
        val arg = argumentCaptor<SatReport>()
        //verify(apiMock).sendReport(capture(arg))
        verify(apiMock).sendReport(arg.capture())
        assertEquals("A1BC", arg.firstValue.callsign)
        //SatReport("", Report.CREW_ACTIVE, ReportTime(Calendar.getInstance()), ""))
    }
}
