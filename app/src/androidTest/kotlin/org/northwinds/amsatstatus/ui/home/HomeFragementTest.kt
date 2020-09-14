package org.northwinds.amsatstatus.ui.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
//import androidx.fragment.app.testing.FragmentScenario.launchFragmentInContainer
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    @Test
    fun homeFragmentLoad() {
        assertTrue(true)
        //var frag = HomeFragment()
        launchFragmentInContainer<HomeFragment>()
    }
}
