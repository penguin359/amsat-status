package org.northwinds.amsatstatus.testing

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.northwinds.amsatstatus.R
import java.io.Closeable

@ExperimentalCoroutinesApi
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    fragmentFactory: FragmentFactory? = null,
    action: FragmentScenario.FragmentAction<T>
//    crossinline action: T.() -> Unit = {}
): HiltFragmentScenario<T> {
    val mainActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(  //FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY", themeResId)

    val activityScenario = ActivityScenario.launch<HiltTestActivity>(mainActivityIntent)
    val scenario = HiltFragmentScenario(T::class.java, activityScenario)
    activityScenario.onActivity { activity ->
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory = it
        }
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs

        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment, HiltFragmentScenario.FRAGMENT_TAG)
            .commitNow()

//            (fragment as T).action()
        action.perform(requireNotNull(T::class.java.cast(fragment)))
    }
    return scenario
}

public class HiltFragmentScenario<F : Fragment> constructor(
    @Suppress("MemberVisibilityCanBePrivate") /* synthetic access */
    internal val fragmentClass: Class<F>,
    private val activityScenario: ActivityScenario<HiltTestActivity>
) : Closeable {
    public fun onFragment(action: FragmentScenario.FragmentAction<F>): HiltFragmentScenario<F> {
        activityScenario.onActivity { activity ->
            val fragment = requireNotNull(
                activity.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            ) {
                "The fragment has been removed from the FragmentManager already."
            }
            check(fragmentClass.isInstance(fragment))
            action.perform(requireNotNull(fragmentClass.cast(fragment)))
        }
        return this
    }

    public override fun close() {
        activityScenario.close()
    }

    public companion object {
        const val FRAGMENT_TAG = "FragmentScenario_Fragment_Tag"
    }
}
