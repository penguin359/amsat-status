package org.northwinds.amsatstatus


import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
//import androidx.test.espresso.Espresso.onData
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.StringContains.containsString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

class MainActivityTestRule(private val showDialog: Boolean = false, private val callsign: String = "", private val grid: String = "") :
  ActivityTestRule<MainActivity>(MainActivity::class.java) {
    override fun beforeActivityLaunched() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        PreferenceManager.getDefaultSharedPreferences(appContext).edit {
            clear()
            if(!showDialog) {
                putBoolean(appContext.getString(R.string.preference_asked_for_consent), true)
            }
//            if(!callsign.?nil) {
                putString("callsign", callsign)
//            }
            putString("default_grid", grid)
        }
    }
}

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityCustomTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mActivityTestRule = MainActivityTestRule(false, "AB1CDE", "CN85nu")

    @Test
    fun mainActivityLoadsCustomFieldsTest() {
        val callsignEditText = onView(withId(R.id.callsign))
        callsignEditText.check { view, noViewFoundException ->
            val editView = view as AppCompatEditText
            assertEquals("AB1CDE", editView.text.toString())
        }
            //.check(containsString("AB1CD"))

        val gridsquareEditText = onView(withId(R.id.gridsquare))
        gridsquareEditText.check { view, noViewFoundException ->
            val editView = view as AppCompatEditText
            assertEquals("CN85nu", editView.text.toString())
        }
    }
}

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mActivityTestRule = MainActivityTestRule()

    @Test
    fun mainActivityLoadsDefaultFieldsTest() {
        val callsignEditText = onView(withId(R.id.callsign))
        callsignEditText.check { view, noViewFoundException ->
            val editView = view as AppCompatEditText
            assertEquals("", editView.text.toString())
        }
        //.check(containsString("AB1CD"))

        val gridsquareEditText = onView(withId(R.id.gridsquare))
        gridsquareEditText.check { view, noViewFoundException ->
            val editView = view as AppCompatEditText
            assertEquals("", editView.text.toString())
        }
    }

    @Test
    fun mainActivityLoadsUpdatedFieldsTest() {
        val overflowMenuButton = onView(
            allOf(
                withContentDescription("More options"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        overflowMenuButton.perform(click())

        val appCompatTextView = onView(
            allOf(
                withId(R.id.title), withText("Settings"),
                childAtPosition(
                    childAtPosition(withId(R.id.content),0),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatTextView.perform(click())
        onView(withText(R.string.callsign)).perform(click())
        val appCompatEditText = onView(
            allOf(
                withId(android.R.id.edit),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        appCompatEditText.perform(scrollTo(), replaceText("AA0AAA"), closeSoftKeyboard())

        val appCompatButton = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        val recyclerView3 = onView(
            allOf(
                withId(R.id.recycler_view),
                childAtPosition(
                    withId(android.R.id.list_container),
                    0
                )
            )
        )
        recyclerView3.perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )

        val appCompatEditText2 = onView(
            allOf(
                withId(android.R.id.edit),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        appCompatEditText2.perform(scrollTo(), replaceText("AB34ef"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())
//        Thread.sleep(300)
//        pressBack()
//        pressBack()
        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val callsignEditText = onView(withId(R.id.callsign))
        callsignEditText.perform(scrollTo())
        callsignEditText.check { view, noViewFoundException ->
            val editView = view as AppCompatEditText
            assertEquals("AA0AAA", editView.text.toString())
        }
        //.check(containsString("AB1CD"))

        val gridsquareEditText = onView(withId(R.id.gridsquare))
        gridsquareEditText.check { view, noViewFoundException ->
            val editView = view as AppCompatEditText
            assertEquals("AB34ef", editView.text.toString())
        }
    }

    @Test
    fun mainActivityTest() {
        val appCompatRadioButton = onView(
            allOf(
                withId(R.id.telemetryOnlyRadio), withText("Downlink Only"),
                childAtPosition(
                    allOf(
                        withId(R.id.report_status),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    1
                )
            )
        )
        appCompatRadioButton.perform(scrollTo(), click())

        val appCompatRadioButton2 = onView(
            allOf(
                withId(R.id.notHeardRadio), withText("Not Heard"),
                childAtPosition(
                    allOf(
                        withId(R.id.report_status),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    2
                )
            )
        )
        appCompatRadioButton2.perform(scrollTo(), click())

        val appCompatRadioButton3 = onView(
            allOf(
                withId(R.id.crewActiveRadio), withText("ISS Crew (Voice) Active"),
                childAtPosition(
                    allOf(
                        withId(R.id.report_status),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    3
                )
            )
        )
        appCompatRadioButton3.perform(scrollTo(), click())

        /*
        val appCompatSpinner = onView(
            allOf(
                withId(R.id.satHeard),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        appCompatSpinner.perform(scrollTo(), click())
        val appCompatTextView = onData(anything())
            .inAdapterView(
                childAtPosition(
                    withClassName(`is`("android.widget.PopupWindow")),
                    0
                )
            )
            .atPosition(4)
        appCompatTextView.perform(click())
        */

        val appCompatEditText = onView(
            withId(R.id.callsign)
        )
        appCompatEditText.perform(scrollTo(), replaceText("A1BC"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            withId(R.id.gridsquare)
        )
        appCompatEditText5.perform(scrollTo(), replaceText("CN85"), closeSoftKeyboard())

        val appCompatButton = onView(
            withId(R.id.submit_button)
        )
        appCompatButton.perform(scrollTo(), click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    @Test
    @UiThreadTest
    fun homeStartsOnCurrentUTCTime() {
        val utc_time = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        //onView(withId(R.id.date_fixture))
        val date_widget = mActivityTestRule.activity.findViewById<DatePicker>(R.id.date_fixture)
        assertEquals("Year", utc_time.get(Calendar.YEAR), date_widget.year)
        assertEquals("Month", utc_time.get(Calendar.MONTH), date_widget.month)
        assertEquals("Day", utc_time.get(Calendar.DAY_OF_MONTH), date_widget.dayOfMonth)
        val time_widget = mActivityTestRule.activity.findViewById<TimePicker>(R.id.time_fixture)
        assertEquals("Hour", utc_time.get(Calendar.HOUR_OF_DAY), time_widget.currentHour)
    }
}
