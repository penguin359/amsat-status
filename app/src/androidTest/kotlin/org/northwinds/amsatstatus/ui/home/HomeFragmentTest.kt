package org.northwinds.amsatstatus.ui.home

import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.*
import org.hamcrest.core.StringContains
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.northwinds.amsatstatus.*
import org.northwinds.amsatstatus.testing.launchFragmentInHiltContainer
import org.northwinds.amsatstatus.util.Clock
import org.northwinds.amsatstatus.util.ClockModule
import org.northwinds.amsatstatus.util.MyClock
import java.util.*


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        PreferenceManager(appContext).sharedPreferences.edit { clear() }
    }

    @Test
    fun dateTimePickersLoadCurrentUtcTime() {
        val frag = launchFragmentInHiltContainer<HomeFragment>()

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
            assertEquals("Month", utc_time.get(Calendar.MONTH)+1, date_widget.month+1)
            assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
            assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
            assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, time_widget.currentMinute)
        }
        assertEquals("Year", utc_time.get(Calendar.YEAR), year)
        assertEquals("Month", utc_time.get(Calendar.MONTH)+1, month+1)
        assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), day)
        assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), hour)
        assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, minute)

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("UTC"))))
    }

    @Test
    fun dateTimePickersLoadCurrentLocalTime() {
        PreferenceManager(appContext).sharedPreferences.edit {
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        TimeZone.setDefault(TimeZone.getTimeZone("MST"))
        val local_time = Calendar.getInstance(TimeZone.getTimeZone("MST"))
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        frag.onFragment {
            val date_widget = it.view!!.findViewById(R.id.date_fixture) as DatePicker
            val time_widget = it.view!!.findViewById<TimePicker>(R.id.time_fixture)
            assertEquals("Year", local_time.get(Calendar.YEAR), date_widget.year)
            assertEquals("Month", local_time.get(Calendar.MONTH)+1, date_widget.month+1)
            assertEquals("Day", local_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
            assertEquals("Hour", local_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
            assertEquals("Minute", local_time.get(Calendar.MINUTE) / 15, time_widget.currentMinute)
        }

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("Local"))))
    }

    @Test
    fun verifyDefaultReportFields() {
        val frag = launchFragmentInHiltContainer<HomeFragment>()
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
        val frag = launchFragmentInHiltContainer<HomeFragment>()
        onView(withId(R.id.callsign)).check(matches(withText(EXPECTED_CALLSIGN)))
        onView(withId(R.id.gridsquare)).check(matches(withText(EXPECTED_GRIDSQUARE)))
    }

    @Test
    fun homeFragmentLoad() {
        val frag = launchFragmentInHiltContainer<HomeFragment>()

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
            assertEquals("Month", utc_time.get(Calendar.MONTH)+1, date_widget.month+1)
            assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
            assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
            assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, time_widget.currentMinute)
        }
        assertEquals("Year", utc_time.get(Calendar.YEAR), year)
        assertEquals("Month", utc_time.get(Calendar.MONTH)+1, month+1)
        assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), day)
        assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), hour)
        assertEquals("Minute", utc_time.get(Calendar.MINUTE) / 15, minute)
    }

    @Test
    fun timePickerHasCorrectValueRange() {
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(allOf(
            isDescendantOfA(withId(R.id.time_fixture))/*, instanceOf<NumberPicker>()*/, `is`(
                instanceOf(NumberPicker::class.java)), hasDescendant(withContentDescription(containsString("minute"))))).check { view, exception ->
            if(view == null)
                throw exception
            val picker = try { view as NumberPicker } catch(ex: ClassCastException) { return@check }
            assertEquals(0, picker.minValue)
            assertEquals(3, picker.maxValue)
            val choices = picker.displayedValues
            assertEquals(4, choices.size)
            assertEquals("00", choices[0])
            assertEquals("15", choices[1])
            assertEquals("30", choices[2])
            assertEquals("45", choices[3])
        }
    }

    @Test
    fun timeLabelUpdatesWithPreferences() {
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("UTC"))))

        PreferenceManager(appContext).sharedPreferences.edit {
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("Local"))))

        PreferenceManager(appContext).sharedPreferences.edit {
            putBoolean(appContext.getString(R.string.preference_local_time), false)
        }

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("UTC"))))
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Test
    fun find_my_location() {
        val frag = launchFragmentInHiltContainer<HomeFragment>()
/*
        val client: LocationClient
        val dummyLocation: Location  = Location("dummy")
*/
        onView(withId(R.id.location_button)).perform(scrollTo(), click())
        Thread.sleep(5000)
        onView(withId(R.id.gridsquare)).check { view, _ ->
            val editView = view as EditText
            assertEquals("CM87wk", editView.text.toString())
        }
    }
}

@RunWith(AndroidJUnit4::class)
@UninstallModules(ClockModule::class, AmsatApiModule::class)
@HiltAndroidTest
class HomeFragmentMocksTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        PreferenceManager(appContext).sharedPreferences.edit { clear() }
    }

    // UTC:   2019-01-01T05:23:45Z
    // Local: 2018-12-31T21:23:45-08:00
    val ref_time = 1546320225*1000L
    val ref_timezone = "America/Los_Angeles"

    @BindValue
    val clock: Clock = MyClock(ref_time)

    @BindValue
    val apiMock = mock<AmsatApi> {}

    @Test
    fun dateTimePickersLoadFixedUtcTime() {
        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))

        val frag = launchFragmentInHiltContainer<HomeFragment>()

        frag.onFragment {
            val date_widget = it.view!!.findViewById(R.id.date_fixture) as DatePicker
            val time_widget = it.view!!.findViewById<TimePicker>(R.id.time_fixture)
            // UTC:   2019-01-01T05:23:45Z
            assertEquals("Bad Year",   2019,    date_widget.year)
            assertEquals("Bad Month",  1,       date_widget.month+1)
            assertEquals("Bad Day",    1,       date_widget.dayOfMonth)
            assertEquals("Bad Hour",   5,       time_widget.currentHour)
            assertEquals("Bad Minute", 23 / 15, time_widget.currentMinute)
        }

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("UTC"))))
    }

    @Test
    fun dateTimePickersLoadFixedLocalTime() {
        PreferenceManager(appContext).sharedPreferences.edit {
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))

        val frag = launchFragmentInHiltContainer<HomeFragment>()

        frag.onFragment {
            val date_widget = it.view!!.findViewById(R.id.date_fixture) as DatePicker
            val time_widget = it.view!!.findViewById<TimePicker>(R.id.time_fixture)
            // Local: 2018-12-31T21:23:45-08:00
            assertEquals("Bad Year",   2018,    date_widget.year)
            assertEquals("Bad Month",  12,      date_widget.month+1)
            assertEquals("Bad Day",    31,      date_widget.dayOfMonth)
            assertEquals("Bad Hour",   21,      time_widget.currentHour)
            assertEquals("Bad Minute", 23 / 15, time_widget.currentMinute)
        }

        onView(withId(R.id.time_mode)).check(matches(withText(StringContains("Local"))))
    }

    @Test
    fun dateTimeSubmitsCorrectFixedUTCTime() {
        val expectedCallsign = "A1BC"
        val expectedGridsquare = "BP51"
        PreferenceManager(appContext).sharedPreferences.edit {
            putString(appContext.getString(R.string.preference_callsign), expectedCallsign)
            putString(appContext.getString(R.string.preference_default_grid), expectedGridsquare)
            putBoolean(appContext.getString(R.string.preference_local_time), false)
        }

        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))
        //val utc_time = Calendar.getInstance(TimeZone.getTimeZone("MST"))
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.crewActiveRadio)).perform(scrollTo(), click())
        onView(withId(R.id.satHeard)).perform(click())
        //onData(withSpinnerText(""))
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("AO-7 Mode B"))).perform(click())
        onView(withId(R.id.satHeard)).check(matches(withSpinnerText(containsString("AO-7 Mode B"))))
        onView(withId(R.id.submit_button)).perform(scrollTo(), click())

        //var arg: ArgumentCaptor<SatReport> = ArgumentCaptor.forClass(SatReport::class.java)
        //var arg: ArgumentCaptor<SatReport> = argumentCaptor.forClass(SatReport::class.java)
        val arg = argumentCaptor<SatReport>()
        verify(apiMock).sendReport(arg.capture())
        assertEquals(expectedCallsign, arg.firstValue.callsign)
        assertEquals(expectedGridsquare, arg.firstValue.gridSquare)
        assertEquals(Report.CREW_ACTIVE, arg.firstValue.report)
        assertEquals("[B]_AO-7", arg.firstValue.name)
        val time =  arg.firstValue.time
        // UTC:   2019-01-01T05:23:45Z
        assertEquals("Bad year", 2019, time.year)
        assertEquals("Bad month", 1, time.month+1)
        assertEquals("Bad day",1, time.day)
        assertEquals("Bad hour", 5, time.hour)
        assertEquals("Bad minute", 15, time.minute)
        assertEquals("Bad quarter", 1, time.quarter)
    }

    //@Ignore("Requires an update to ReportTime to fix it")
    @Test
    fun dateTimeSubmitsCorrectFixedLocalTime() {
        val expectedCallsign = "A1BC"
        val expectedGridsquare = "BP51"
        PreferenceManager(appContext).sharedPreferences.edit {
            putString(appContext.getString(R.string.preference_callsign), expectedCallsign)
            putString(appContext.getString(R.string.preference_default_grid), expectedGridsquare)
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.telemetryOnlyRadio)).perform(scrollTo(), click())
        onView(withId(R.id.satHeard)).perform(scrollTo(), click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("ISS SSTV"))).perform(click())
        onView(withId(R.id.satHeard)).check(matches(withSpinnerText(containsString("ISS SSTV"))))
        onView(withId(R.id.submit_button)).perform(scrollTo(), click())

        val arg = argumentCaptor<SatReport>()
        verify(apiMock).sendReport(arg.capture())
        assertEquals(expectedCallsign, arg.firstValue.callsign)
        assertEquals(expectedGridsquare, arg.firstValue.gridSquare)
        assertEquals(Report.TELEMETRY_ONLY, arg.firstValue.report)
        assertEquals("ISS-SSTV", arg.firstValue.name)
        val time =  arg.firstValue.time
        // UTC:   2019-01-01T05:23:45Z
        assertEquals("Bad year", 2019, time.year)
        assertEquals("Bad month", 1, time.month+1)
        assertEquals("Bad day",1, time.day)
        assertEquals("Bad hour", 5, time.hour)
        assertEquals("Bad minute", 15, time.minute)
        assertEquals("Bad quarter", 1, time.quarter)
    }

    @Test
    fun dateTimeSubmitsCorrectFixedManualUtcTime() {
        val expectedCallsign = "A1BC"
        val expectedGridsquare = "BP51"
        PreferenceManager(appContext).sharedPreferences.edit {
            putString(appContext.getString(R.string.preference_callsign), expectedCallsign)
            putString(appContext.getString(R.string.preference_default_grid), expectedGridsquare)
            putBoolean(appContext.getString(R.string.preference_local_time), false)
        }

        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.telemetryOnlyRadio)).perform(scrollTo(), click())
        onView(withId(R.id.satHeard)).perform(scrollTo(), click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("ISS SSTV"))).perform(click())
        onView(withId(R.id.satHeard)).check(matches(withSpinnerText(containsString("ISS SSTV"))))
        onView(withId(R.id.date_fixture)).perform(PickerActions.setDate(2021, 6, 17))
        onView(withId(R.id.time_fixture)).perform(PickerActions.setTime(18, 3))
        onView(withId(R.id.submit_button)).perform(scrollTo(), click())

        val arg = argumentCaptor<SatReport>()
        verify(apiMock).sendReport(arg.capture())
        assertEquals(expectedCallsign, arg.firstValue.callsign)
        assertEquals(expectedGridsquare, arg.firstValue.gridSquare)
        assertEquals(Report.TELEMETRY_ONLY, arg.firstValue.report)
        assertEquals("ISS-SSTV", arg.firstValue.name)
        val time =  arg.firstValue.time
        assertEquals("Bad year", 2021, time.year)
        assertEquals("Bad month", 6, time.month+1)
        assertEquals("Bad day",17, time.day)
        assertEquals("Bad hour", 18, time.hour)
        assertEquals("Bad minute", 45, time.minute)
        assertEquals("Bad quarter", 3, time.quarter)
    }

    @Test
    fun dateTimeSubmitsCorrectFixedManualLocalTime() {
        val expectedCallsign = "Z3ZZ"
        val expectedGridsquare = ""
        PreferenceManager(appContext).sharedPreferences.edit {
            putString(appContext.getString(R.string.preference_callsign), expectedCallsign)
            putString(appContext.getString(R.string.preference_default_grid), expectedGridsquare)
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.crewActiveRadio)).perform(scrollTo(), click())
        onView(withId(R.id.satHeard)).perform(scrollTo(), click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Delfi-C3 (DO-64)"))).perform(click())
        onView(withId(R.id.satHeard)).check(matches(withSpinnerText(containsString("Delfi-C3 (DO-64)"))))
        onView(withId(R.id.date_fixture)).perform(PickerActions.setDate(2022, 4, 28))
        onView(withId(R.id.time_fixture)).perform(PickerActions.setTime(23, 2))
        onView(withId(R.id.submit_button)).perform(scrollTo(), click())

        val arg = argumentCaptor<SatReport>()
        verify(apiMock).sendReport(arg.capture())
        assertEquals(expectedCallsign, arg.firstValue.callsign)
        assertEquals(expectedGridsquare, arg.firstValue.gridSquare)
        assertEquals(Report.CREW_ACTIVE, arg.firstValue.report)
        assertEquals("Delfi-C3", arg.firstValue.name)
        val time =  arg.firstValue.time
        assertEquals("Bad year", 2022, time.year)
        assertEquals("Bad month", 4, time.month+1)
        assertEquals("Bad day",29, time.day)
        assertEquals("Bad hour", 6, time.hour)
        assertEquals("Bad minute", 30, time.minute)
        assertEquals("Bad quarter", 2, time.quarter)
    }

    @Test
    fun dateTimeSubmitsCorrectFixedManualLocalTimeNewYears() {
        val expectedCallsign = "Z3ZZ"
        val expectedGridsquare = ""
        PreferenceManager(appContext).sharedPreferences.edit {
            putString(appContext.getString(R.string.preference_callsign), expectedCallsign)
            putString(appContext.getString(R.string.preference_default_grid), expectedGridsquare)
            putBoolean(appContext.getString(R.string.preference_local_time), true)
        }

        TimeZone.setDefault(TimeZone.getTimeZone(ref_timezone))
        val frag = launchFragmentInHiltContainer<HomeFragment>()

        onView(withId(R.id.notHeardRadio)).perform(scrollTo(), click())
        onView(withId(R.id.satHeard)).perform(scrollTo(), click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Delfi-C3 (DO-64)"))).perform(click())
        onView(withId(R.id.satHeard)).check(matches(withSpinnerText(containsString("Delfi-C3 (DO-64)"))))
        onView(withId(R.id.date_fixture)).perform(PickerActions.setDate(2022, 12, 31))
        onView(withId(R.id.time_fixture)).perform(PickerActions.setTime(20, 1))
        onView(withId(R.id.submit_button)).perform(scrollTo(), click())

        val arg = argumentCaptor<SatReport>()
        verify(apiMock).sendReport(arg.capture())
        assertEquals(expectedCallsign, arg.firstValue.callsign)
        assertEquals(expectedGridsquare, arg.firstValue.gridSquare)
        assertEquals(Report.NOT_HEARD, arg.firstValue.report)
        assertEquals("Delfi-C3", arg.firstValue.name)
        val time =  arg.firstValue.time
        assertEquals("Bad year", 2023, time.year)
        assertEquals("Bad month", 1, time.month+1)
        assertEquals("Bad day",1, time.day)
        assertEquals("Bad hour", 4, time.hour)
        assertEquals("Bad minute", 15, time.minute)
        assertEquals("Bad quarter", 1, time.quarter)
    }
}
