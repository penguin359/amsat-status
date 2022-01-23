/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.northwinds.amsatstatus.testing
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
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
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.amsatstatus.R
import java.io.Closeable
/**
 * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
 * given arguments hosted by an empty [FragmentActivity] and waits for it to reach [initialState].
 *
 * This method cannot be called from the main thread.
 *
 * @param fragmentArgs a bundle to passed into fragment
 * @param themeResId a style resource id to be set to the host activity's theme
 * @param initialState the initial [Lifecycle.State]. This must be one of
 * [Lifecycle.State.CREATED], [Lifecycle.State.STARTED], or [Lifecycle.State.RESUMED].
 * @param factory a fragment factory to use or null to use default factory
 */
public inline fun <reified F : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    initialState: Lifecycle.State = Lifecycle.State.RESUMED,
    factory: FragmentFactory? = null
): HiltFragmentScenario<F> = HiltFragmentScenario.launchInHiltContainer(
    F::class.java, fragmentArgs, themeResId, initialState,
    factory
)
/**
 * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
 * given arguments hosted by an empty [FragmentActivity] using
 * [instantiate] to create the Fragment and waits for it to reach [initialState].
 *
 * This method cannot be called from the main thread.
 *
 * @param fragmentArgs a bundle to passed into fragment
 * @param themeResId a style resource id to be set to the host activity's theme
 * @param initialState the initial [Lifecycle.State]. This must be one of
 * [Lifecycle.State.CREATED], [Lifecycle.State.STARTED], or [Lifecycle.State.RESUMED].
 * @param instantiate method which will be used to instantiate the Fragment. This is a
 * simplification of the [FragmentFactory] interface for cases where only a single class
 * needs a custom constructor called.
 */
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
/**
 * Run [block] using [FragmentScenario.onFragment], returning the result of the [block].
 *
 * If any exceptions are raised while running [block], they are rethrown.
 */
@SuppressWarnings("DocumentExceptions")
public inline fun <reified F : Fragment, T : Any> FragmentScenario<F>.withFragment(
    crossinline block: F.() -> T
): T {
    lateinit var value: T
    var err: Throwable? = null
    onFragment { fragment ->
        try {
            value = block(fragment)
        } catch (t: Throwable) {
            err = t
        }
    }
    err?.let { throw it }
    return value
}
/**
 * FragmentScenario provides API to start and drive a Fragment's lifecycle state for testing. It
 * works with arbitrary fragments and works consistently across different versions of the Android
 * framework.
 *
 * FragmentScenario only supports [androidx.fragment.app.Fragment][Fragment]. If you are using
 * a deprecated fragment class such as `android.support.v4.app.Fragment` or
 * [android.app.Fragment], please update your code to
 * [androidx.fragment.app.Fragment][Fragment].
 *
 * If your testing Fragment has a dependency to specific theme such as `Theme.AppCompat`,
 * use the theme ID parameter in [launch] method.
 *
 * @param F The Fragment class being tested
 *
 * @see ActivityScenario a scenario API for Activity
 */
public class HiltFragmentScenario<F : Fragment> private constructor(
    @Suppress("MemberVisibilityCanBePrivate") /* synthetic access */
    internal val fragmentClass: Class<F>,
    private val activityScenario: ActivityScenario<HiltEmptyFragmentActivity>
) : Closeable {
    /**
     * An empty activity inheriting FragmentActivity. This Activity is used to host Fragment in
     * FragmentScenario.
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @AndroidEntryPoint
    internal class HiltEmptyFragmentActivity : FragmentActivity() {
        @SuppressLint("RestrictedApi")
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(
                intent.getIntExtra(
                    THEME_EXTRAS_BUNDLE_KEY,
                    R.style.FragmentScenarioEmptyFragmentActivityTheme
                )
            )
            // Checks if we have a custom FragmentFactory and set it.
            val factory = FragmentFactoryHolderViewModel.getInstance(this).fragmentFactory
            if (factory != null) {
                supportFragmentManager.fragmentFactory = factory
            }
            // FragmentFactory needs to be set before calling the super.onCreate, otherwise the
            // Activity crashes when it is recreating and there is a fragment which has no
            // default constructor.
            super.onCreate(savedInstanceState)
        }
        companion object {
            const val THEME_EXTRAS_BUNDLE_KEY = "androidx.fragment.app.testing.FragmentScenario" +
                ".EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY"
        }
    }
    /**
     * A view-model to hold a fragment factory.
     *
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
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
    /**
     * Runs a given [action] on the current Activity's main thread.
     *
     * Note that you should never keep Fragment reference passed into your [action]
     * because it can be recreated at anytime during state transitions.
     *
     * Throwing an exception from [action] makes the host Activity crash. You can
     * inspect the exception in logcat outputs.
     *
     * This method cannot be called from the main thread.
     */
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
    /**
     * Finishes the managed fragments and cleans up device's state. This method blocks execution
     * until the host activity becomes [Lifecycle.State.DESTROYED].
     */
    public override fun close() {
        activityScenario.close()
    }
    public companion object {
        private const val FRAGMENT_TAG = "FragmentScenario_Fragment_Tag"
        /**
         * Launches a Fragment in the Activity's root view container `android.R.id.content`, with
         * given arguments hosted by an empty [FragmentActivity] themed by [themeResId],
         * using the given [FragmentFactory] and waits for it to reach [initialState].
         *
         * This method cannot be called from the main thread.
         *
         * @param fragmentClass a fragment class to instantiate
         * @param fragmentArgs a bundle to passed into fragment
         * @param themeResId a style resource id to be set to the host activity's theme
         * @param initialState The initial [Lifecycle.State]. This must be one of
         * [CREATED][Lifecycle.State.CREATED], [STARTED][Lifecycle.State.STARTED], and
         * [RESUMED][Lifecycle.State.RESUMED].
         * @param factory a fragment factory to use or null to use default factory
         */
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
                HiltEmptyFragmentActivity::class.java
            )
            val startActivityIntent = Intent.makeMainActivity(componentName)
                .putExtra(HiltEmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId)
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
