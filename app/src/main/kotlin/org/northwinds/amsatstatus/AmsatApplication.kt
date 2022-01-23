package org.northwinds.amsatstatus

import javax.net.ssl.SSLContext
import javax.net.ssl.HttpsURLConnection

//import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.multidex.MultiDexApplication

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AmsatApplication : MultiDexApplication() {
    companion object {
        private const val TAG = "AmsatStatus-AmsatApplication"
    }

    private lateinit var pref_enable_analytics: String
    private lateinit var pref_enable_crash_reports: String

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val listener = SharedPreferences.OnSharedPreferenceChangeListener(function = { prefs, key ->
        if (pref_enable_analytics.equals(key)) {
            val analytics = prefs.getBoolean(pref_enable_analytics, false)
            Log.v(TAG, "Analytics status changed: $analytics")
            firebaseAnalytics.setAnalyticsCollectionEnabled(analytics)
        } else if (pref_enable_crash_reports.equals(key)) {
            val crash_reports = prefs.getBoolean(pref_enable_crash_reports, false)
            Log.v(TAG, "Crash reports status changed: $crash_reports")
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crash_reports)
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
        val crash_reports = prefs.getBoolean(pref_enable_crash_reports, false)
        Log.v(TAG, "Analytics status initial: $analytics")
        Log.v(TAG, "Crash reports status initial: $crash_reports")
        firebaseAnalytics.setAnalyticsCollectionEnabled(analytics)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(crash_reports)

        val default = SSLContext.getDefault()
        val params = default.getDefaultSSLParameters()
        Log.d(TAG, "Default Protocol: ${default.getProtocol()}")
        Log.d(TAG, "Default Protocols: ${params.getProtocols().joinToString()}")
        Log.d(TAG, "Default Ciphers: ${params.getCipherSuites().joinToString()}")
        val context = SSLContext.getInstance("TLSv1.2")
        context.init(null, null, null)
        val params2 = context.getDefaultSSLParameters()
        Log.d(TAG, "TLSv1.2 Protocol: ${context.getProtocol()}")
        Log.d(TAG, "TLSv1.2 Protocols: ${params2.getProtocols().joinToString()}")
        Log.d(TAG, "TLSv1.2 Ciphers: ${params2.getCipherSuites().joinToString()}")
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
    }
}
