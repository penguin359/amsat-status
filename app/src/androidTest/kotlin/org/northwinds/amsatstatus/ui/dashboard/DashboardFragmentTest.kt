package org.northwinds.amsatstatus.ui.dashboard

import android.util.Log
import android.view.View
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
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.ui.dashboard.adapters.MyReportRecyclerViewAdapter


//@RunWith(AndroidJUnit4::class)
@Ignore("Currently, this is replaced by multi-level dashboard view")
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
    @Test
    fun dashboardShouldShowDemoSatellite() {
        val frag = launchFragmentInContainer<DashboardFragment>()
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("DEMO"))))
        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString("AO-27"))
            .perform(click())
//        onData(anything())
//            .inAdapterView(withId(R.id.name))
//            .atPosition(3)
//            .perform(click())
        onView(withId(R.id.name))
            .check(matches(withSpinnerText(containsString("AO-27"))))
        onView(withId(R.id.name))
            .perform(click())
        onData(hasToString(containsString("DEMO")))
            .perform(click())
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("19:30"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .onChildView(withId(R.id.multi_status))
            .check(matches(withText("heard")))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .check(matches(hasDescendant(withText(containsString("heard")))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_time))
            .check(matches(withText(containsString("19:45"))))
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(0)
            .perform(click())
        onData(anything())
            .inAdapterView(withId(R.id.reports))
            .atPosition(1)
            .onChildView(withId(R.id.multi_callsign))
            .check(matches(withText(containsString("AB1C"))))
    }
}
