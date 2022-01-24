/**********************************************************************************
 * Copyright (c) 2022 Loren M. Lang                                               *
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

package org.northwinds.amsatstatus.util

import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.testing.TestInstallIn

import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.RuntimeException
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = arrayOf(ActivityRetainedComponent::class),
    replaces = arrayOf(ThreadModule::class)
)
abstract class EspressoThreadModule {
    @Binds
    abstract fun provideThreadExecutor(executor: IdlingThreadPoolExecutor): ExecutorService
}

@Module
//@InstallIn(ActivityRetainedComponent::class)
@InstallIn(SingletonComponent::class)
class IdleEspressoThreadModule {
    @Provides
//    @ActivityRetainedScoped
    @Singleton
    fun provideIdlingThreadExecutor(): IdlingThreadPoolExecutor {
        val id = counter.getAndIncrement()
//        return IdlingThreadPoolExecutor("EspressoTestPool-$id", 5, 5, 50, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory())
        return IdlingThreadPoolExecutor("EspressoTestPool", 5, 5, 50, TimeUnit.MILLISECONDS, LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory())
    }

    companion object {
        val counter = AtomicInteger(1)
    }
}
