package org.northwinds.amsatstatus.testing

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commitNow
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.northwinds.amsatstatus.R
import java.io.Closeable

public inline fun <reified F : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null
): HiltFragmentScenario<F> = HiltFragmentScenario.launchInHiltContainer(
    F::class.java, fragmentArgs, themeResId, initialState,
    factory
)

public inline fun <reified F : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    crossinline instantiate: () -> F
): HiltFragmentScenario<F> = HiltFragmentScenario.launchInHiltContainer(
    F::class.java, fragmentArgs, themeResId, initialState,
    object : FragmentFactory() {
        override fun instantiate(
            classLoader: ClassLoader,
            className: String
        ) = when (className) {
            F::class.java.name -> instantiate()
            else -> super.instantiate(classLoader, className)
        }
    }
)

public class HiltFragmentScenario<F : Fragment> constructor(
    @Suppress("MemberVisibilityCanBePrivate") /* synthetic access */
    internal val fragmentClass: Class<F>,
    private val activityScenario: ActivityScenario<HiltTestActivity>
) : Closeable {
    internal class FragmentFactoryHolderViewModel : ViewModel() {
        var fragmentFactory: FragmentFactory? = null
        override fun onCleared() {
            super.onCleared()
            fragmentFactory = null
        }
        companion object {
            @Suppress("MemberVisibilityCanBePrivate")
            internal val FACTORY: ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val viewModel =
                            FragmentFactoryHolderViewModel()
                        return viewModel as T
                    }
                }
            fun getInstance(activity: FragmentActivity): FragmentFactoryHolderViewModel {
                val viewModel: FragmentFactoryHolderViewModel by activity.viewModels { FACTORY }
                return viewModel
            }
        }
    }

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
        /*private*/ const val FRAGMENT_TAG = "FragmentScenario_Fragment_Tag"

        @JvmOverloads
        @JvmStatic
        public fun <F : Fragment> launchInHiltContainer(
            fragmentClass: Class<F>,
            fragmentArgs: Bundle? = null,
            @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
            initialState: Lifecycle.State = Lifecycle.State.RESUMED,
            factory: FragmentFactory? = null
        ): HiltFragmentScenario<F> = internalLaunch(
            fragmentClass,
            fragmentArgs,
            themeResId,
            initialState,
            factory,
            android.R.id.content
        )

        @SuppressLint("RestrictedApi")
        internal fun <F : Fragment> internalLaunch(
            fragmentClass: Class<F>,
            fragmentArgs: Bundle?,
            @StyleRes themeResId: Int,
            initialState: Lifecycle.State,
            factory: FragmentFactory?,
            @IdRes containerViewId: Int
        ): HiltFragmentScenario<F> {
            require(initialState != Lifecycle.State.DESTROYED) {
                "Cannot set initial Lifecycle state to $initialState for FragmentScenario"
            }
            val componentName = ComponentName(
                ApplicationProvider.getApplicationContext(),
                HiltTestActivity::class.java
            )
            val startActivityIntent = Intent.makeMainActivity(componentName)
                .putExtra( //FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY
            "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY", themeResId)
            val scenario = HiltFragmentScenario(
                fragmentClass,
                ActivityScenario.launch(
                    startActivityIntent
                )
            )
            scenario.activityScenario.onActivity { activity ->
                if (factory != null) {
                    FragmentFactoryHolderViewModel.getInstance(activity).fragmentFactory = factory
                    activity.supportFragmentManager.fragmentFactory = factory
                }
                val fragment = activity.supportFragmentManager.fragmentFactory
                    .instantiate(requireNotNull(fragmentClass.classLoader), fragmentClass.name)
                fragment.arguments = fragmentArgs
                activity.supportFragmentManager.commitNow {
                    add(containerViewId, fragment, FRAGMENT_TAG)
                    setMaxLifecycle(fragment, initialState)
                }
            }
            return scenario
        }
    }
}
