/**********************************************************************************
 * Copyright (c) 2020 Loren M. Lang                                               *
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

package org.northwinds.amsatstatus.ui.dashboard

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ExpandableListView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.util.Checks
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.EntryPoints
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.runner.RunWith
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.testing.launchFragmentInHiltContainer
import org.northwinds.amsatstatus.ui.dashboard.adapters.MyReportRecyclerViewAdapter
import org.northwinds.amsatstatus.util.EspressoThreadModule
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DashboardFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /*
    val a = object : BoundedMatcher<RecyclerView.ViewHolder, MyReportRecyclerViewAdapter.ViewHolder>(RecyclerView.ViewHolder::class.java, MyReportRecyclerViewAdapter.ViewHolder::class.java) {
        override fun matchesSafely(item: MyReportRecyclerViewAdapter.ViewHolder?): Boolean {
            TODO("Not yet implemented")
        }

        override fun describeTo(description: Description?) {
            TODO("Not yet implemented")
        }
    }
    */
    fun withItemSubjectInViewHolder(itemSubject: String): Matcher<RecyclerView.ViewHolder> {
        return object :
            BoundedMatcher<RecyclerView.ViewHolder, MyReportRecyclerViewAdapter.ViewHolder>(
                MyReportRecyclerViewAdapter.ViewHolder::class.java
            ) {
            override fun matchesSafely(holder: MyReportRecyclerViewAdapter.ViewHolder): Boolean {
                var isMatches = false
                if (holder.nameView != null) {
                    Log.d("Debug", "Matching text ${holder.nameView.text}")
                    Log.d("Debug", "Matching visibility ${holder.nameView.visibility}")
                    isMatches = (itemSubject == holder.nameView.text.toString()
                            && holder.nameView.visibility === View.VISIBLE)
                }
                return isMatches
            }

            override fun describeTo(description: Description) {
                description.appendText("with item subject: $itemSubject")
            }
        }
    }

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Ignore("Currently, this is replaced by multi-level dashboard view")
    @Test
    fun dashboardShouldShowDemoSatellite() {
        val frag = launchFragmentInHiltContainer<DashboardFragment>()
        val aa = object : BaseMatcher<View>() {
            override fun describeTo(description: Description?) {
            }

            override fun matches(item: Any?): Boolean {
                Log.d("BigDebug", "Item: $item")
                return false
            }
        }
        //onView(withId(R.id.reports)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(allOf(withId(R.id.name), hasDescendant(withText("DEMO-1")))))
        //onView(withId(R.id.reports)).perform(RecyclerViewActions.scrollToHolder<RecyclerView.ViewHolder>(withItemSubjectInViewHolder("DEMO-2")))
        //onView(withId(R.id.reports)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(allOf(withId(R.id.name), hasDescendant(withText("DEMO-2")))))
        //onView(withId(R.id.reports)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(withText("DEMO-2")))
        onView(withId(R.id.reports)).perform(RecyclerViewActions.scrollTo<MyReportRecyclerViewAdapter.ViewHolder>(
            allOf(hasDescendant(withId(R.id.name)), hasDescendant(withText("DEMO-2")))))
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DashboardMultiFragmentTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var idlingThreadPoolExecutor: IdlingThreadPoolExecutor

    @Before
    fun setUp() {
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS)
    }

    @After
    fun tearDown() {
        idlingThreadPoolExecutor.shutdownNow()
        idlingThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)
    }

    @Test
    fun dashboardShouldShowDemoSatelliteOnLaunch() {
        hiltRule.inject()
        val frag = launchFragmentInHiltContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))

        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertEquals(6, listView.adapter.count)
            }

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("2018-02-27"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("02:00"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_status))
            .check(matches(withText("Heard")))

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("2018-02-27"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("03:00"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_status))
            .check(matches(withText(containsString("Not Heard"))))

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(3)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("2018-02-27"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(3)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("04:30"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(3)
            .onChildView(withId(R.id.multi_status))
            .check(matches(withText(containsString("Crew Active"))))

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .perform(click())
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_callsign))
            .check(matches(withText(containsString("AB1C"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_grid))
            .check(matches(withText(containsString("AB34"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_report))
            .check(matches(withText("Heard")))

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(6)
            .perform(click())
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(7)
            .onChildView(withId(R.id.multi_callsign))
            .check(matches(withText(containsString("OM/DL1IBM"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(7)
            .onChildView(withId(R.id.multi_grid))
            .check(matches(withText(containsString("DM57"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(7)
            .onChildView(withId(R.id.multi_report))
            .check(matches(withText(containsString("Heard"))))
    }

    @Test
    fun dashboardShouldShowNoReportsOnDeadSatellite() {
        hiltRule.inject()
        val frag = launchFragmentInHiltContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))
        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString("MAYA-1"))
            .perform(click())
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("MAYA-1"))))

        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertEquals(0, listView.adapter.count)
            }
    }

    @Test
    fun dashboardShouldShowManyReportsOnLiveSatellite() {
        hiltRule.inject()
        val live_satellite = "AO-91"
        val frag = launchFragmentInHiltContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))
        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString(live_satellite))
            .perform(click())
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString(live_satellite))))

        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertNotEquals(0, listView.adapter.count)
            }

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_time))
            .check(matches(not(withText(containsString("2018-02-27")))))
    }

    @Test
    fun dashboardShouldRestoreDemoSatelliteWhenReselected() {
        hiltRule.inject()
        val frag = launchFragmentInHiltContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertEquals(6, listView.adapter.count)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("2018-02-27"))))

        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString("MAYA-1"))
            .perform(click())
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("MAYA-1"))))
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertEquals(0, listView.adapter.count)
            }

        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString("DEMO 1"))
            .perform(click())
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO 1"))))
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertEquals(6, listView.adapter.count)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("2018-02-27"))))
    }

    @Test
    fun dashboardShouldShowCorrectColorsDemoSatellite() {
        hiltRule.inject()
        val frag = launchFragmentInHiltContainer<DashboardFragment>()
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertThat(listView.adapter.count, `is`(equalTo(6)))
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_group_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.heard),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_group_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.notHeard),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(2)
            .onChildView(withId(R.id.multi_group_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.telemetryOnly),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(3)
            .onChildView(withId(R.id.multi_group_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.crewActive),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(5)
            .onChildView(withId(R.id.multi_group_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.conflict),
                    (view.background as ColorDrawable).color)
            }

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(5)
            .perform(click())
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(6)
            .onChildView(withId(R.id.multi_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.heard),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(7)
            .onChildView(withId(R.id.multi_cell))
            .check { view, noViewFoundException ->
                if (view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.resources.getColor(R.color.notHeard),
                    (view.background as ColorDrawable).color)
            }
    }
}
