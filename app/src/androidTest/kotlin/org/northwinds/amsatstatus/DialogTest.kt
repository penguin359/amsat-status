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

package org.northwinds.amsatstatus


import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DialogTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var mActivityTestRule = MainActivityTestRule(true)
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val pref_enable_analytics = appContext.getString(R.string.preference_enable_analytics)
    val pref_enable_crash_reports = appContext.getString(R.string.preference_enable_crash_reports)
    val prefs = PreferenceManager(appContext).sharedPreferences

    @Test
    fun analyticsAreDisabledByDefault() {
        assertFalse("Analytics enabled", prefs.getBoolean(pref_enable_analytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(pref_enable_crash_reports, false))
    }

    @Test
    fun dialogIsShownOnLoad() {
        onView(withText(appContext.getString(R.string.title_analytics)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun boxesAreUncheckedByDefault() {
        onView(withText(appContext.getString(R.string.title_anonymous_usage)))
            .inRoot(isDialog())
            .check(matches(not(isChecked())))
        onView(withText(appContext.getString(R.string.title_crash_reports)))
            .inRoot(isDialog())
            .check(matches(not(isChecked())))
    }

    @Test
    fun analyticsStayDisabledByDefault() {
        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
        assertFalse("Analytics enabled", prefs.getBoolean(pref_enable_analytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(pref_enable_crash_reports, false))
    }

    @Test
    fun analyticsWillEnableAnalyticsWhenChecked() {
        onView(withText(appContext.getString(R.string.title_anonymous_usage)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
        assertTrue("Analytics enabled", prefs.getBoolean(pref_enable_analytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(pref_enable_crash_reports, false))
    }

    @Test
    fun analyticsWillEnableCrashReportsWhenChecked() {
        onView(withText(appContext.getString(R.string.title_crash_reports)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
        assertFalse("Analytics enabled", prefs.getBoolean(pref_enable_analytics, false))
        assertTrue("Crash reports enabled", prefs.getBoolean(pref_enable_crash_reports, false))
    }

    @Test
    fun analyticsWillNotEnableAnyOnBackButton() {
        onView(withText(appContext.getString(R.string.title_anonymous_usage)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText(appContext.getString(R.string.title_crash_reports)))
            .inRoot(isDialog())
            .perform(click())
        onView(withText(appContext.getString(R.string.title_analytics)))
            .inRoot(isDialog())
            .perform(pressBack())
        assertFalse("Analytics enabled", prefs.getBoolean(pref_enable_analytics, false))
        assertFalse("Crash reports enabled", prefs.getBoolean(pref_enable_crash_reports, false))
    }
}
