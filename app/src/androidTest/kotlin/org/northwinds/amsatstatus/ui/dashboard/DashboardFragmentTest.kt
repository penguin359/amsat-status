package org.northwinds.amsatstatus.ui.dashboard

import android.util.Log
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.util.Checks
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Test
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
