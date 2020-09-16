package org.northwinds.amsatstatus.ui.home

import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

import java.util.Calendar
import java.util.TimeZone
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.northwinds.amsatstatus.R

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        PreferenceManager(appContext).sharedPreferences.edit { clear() }
    }

    @Test
    fun homeFragmentLoad() {
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
    }

    @Test
    fun homeFragmentLocal() {
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
}
