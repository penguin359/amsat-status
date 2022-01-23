package org.northwinds.amsatstatus

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ExampleInstrumentedTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertThat(
            "Package name",
            appContext.packageName,
            `is`(
                anyOf(
                    equalTo("org.northwinds.amsatstatus"),
                    equalTo("org.northwinds.amsatstatus.dev")
                )
            )
        )
    }
}