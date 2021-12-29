package org.northwinds.amsatstatus.ui.dashboard

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ExpandableListView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.util.Checks
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Ignore
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.ui.dashboard.adapters.MyReportRecyclerViewAdapter


//@RunWith(AndroidJUnit4::class)
class DashboardFragmentTest {
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
        return object : BoundedMatcher<RecyclerView.ViewHolder, MyReportRecyclerViewAdapter.ViewHolder>(
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
        val frag = launchFragmentInContainer<DashboardFragment>()
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

class DashboardMultiFragmentTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun dashboardShouldShowDemoSatelliteOnLaunch() {
        val frag = launchFragmentInContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))

        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if(view == null)
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
        val frag = launchFragmentInContainer<DashboardFragment>()
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
                if(view == null)
                    throw noViewFoundException
                val listView = view as ExpandableListView
                assertEquals(0, listView.adapter.count)
            }
    }

    @Test
    fun dashboardShouldShowManyReportsOnLiveSatellite() {
        val frag = launchFragmentInContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))
        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString("AO-91"))
            .perform(click())
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("AO-91"))))

        Thread.sleep(300)
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if(view == null)
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
        val frag = launchFragmentInContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if(view == null)
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
        Thread.sleep(300)
        onView(withId(R.id.reports))
            .check { view, noViewFoundException ->
                if(view == null)
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
                if(view == null)
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
        val frag = launchFragmentInContainer<DashboardFragment>()
        Thread.sleep(100)
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                "color drawable"))
                assertEquals(appContext.getColor(R.color.heard),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.getColor(R.color.notHeard),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(2)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.getColor(R.color.telemetryOnly),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(3)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.getColor(R.color.crewActive),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(5)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.getColor(R.color.conflict),
                    (view.background as ColorDrawable).color)
            }

        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(5)
            .perform(click())
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(6)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.getColor(R.color.heard),
                    (view.background as ColorDrawable).color)
            }
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(7)
            .check { view, noViewFoundException ->
                if(view == null)
                    throw noViewFoundException
                assertThat(view.background, describedAs("has a solid background",
                    instanceOf(ColorDrawable::class.java),
                    "color drawable"))
                assertEquals(appContext.getColor(R.color.notHeard),
                    (view.background as ColorDrawable).color)
            }
    }
}
