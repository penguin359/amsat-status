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

/**********************************************************************************
 * Copyright (c) 2021 Loren M. Lang                                               *
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

import android.content.SharedPreferences
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

@HiltAndroidApp
class AmsatApplication : MultiDexApplication() {
    companion object {
        private const val TAG = "AmsatStatus-AmsatApp"
    }

    private lateinit var pref_enable_analytics: String
    private lateinit var pref_enable_crash_reports: String

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener(function = { prefs, key ->
            if (pref_enable_analytics == key) {
                val analytics = prefs.getBoolean(pref_enable_analytics, false)
                Log.v(TAG, "Analytics status changed: $analytics")
                firebaseAnalytics.setAnalyticsCollectionEnabled(analytics)
            } else if (pref_enable_crash_reports == key) {
                val crashReports = prefs.getBoolean(pref_enable_crash_reports, false)
                Log.v(TAG, "Crash reports status changed: $crashReports")
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crashReports)
            }
        })

    override fun onCreate() {
        super.onCreate()
        pref_enable_analytics = getString(R.string.preference_enable_analytics)
        pref_enable_crash_reports = getString(R.string.preference_enable_crash_reports)

        firebaseAnalytics = Firebase.analytics

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(listener)
        val analytics = prefs.getBoolean(pref_enable_analytics, false)
        val crashReports = prefs.getBoolean(pref_enable_crash_reports, false)
        Log.v(TAG, "Analytics status initial: $analytics")
        Log.v(TAG, "Crash reports status initial: $crashReports")
        firebaseAnalytics.setAnalyticsCollectionEnabled(analytics)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crashReports)

        val default = SSLContext.getDefault()
        val params = default.defaultSSLParameters
        Log.d(TAG, "Default Protocol: ${default.protocol}")
        Log.d(TAG, "Default Protocols: ${params.protocols.joinToString()}")
        Log.d(TAG, "Default Ciphers: ${params.cipherSuites.joinToString()}")
        val context = SSLContext.getInstance("TLSv1.2")
        context.init(null, null, null)
        val params2 = context.defaultSSLParameters
        Log.d(TAG, "TLSv1.2 Protocol: ${context.protocol}")
        Log.d(TAG, "TLSv1.2 Protocols: ${params2.protocols.joinToString()}")
        Log.d(TAG, "TLSv1.2 Ciphers: ${params2.cipherSuites.joinToString()}")
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
    }
}
