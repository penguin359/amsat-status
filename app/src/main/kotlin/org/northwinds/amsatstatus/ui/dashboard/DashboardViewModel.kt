package org.northwinds.amsatstatus.ui.dashboard

import java.util.concurrent.Executors

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import org.northwinds.amsatstatus.AmsatApi
import org.northwinds.amsatstatus.SatReport
import org.northwinds.amsatstatus.Report
import org.northwinds.amsatstatus.makeReportTimeFromString

private val TAG = "DashboardViewModel"

class DashboardViewModel : ViewModel() {
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val _reports = MutableLiveData<List<SatReport>>()

    val reports: LiveData<List<SatReport>> = _reports

    fun update(name: String) {
        Log.v(TAG, "Clearing results")
        empty()
        Log.v(TAG, "Starting thread for satellite $name")

        executor.execute(object : Runnable {
            override fun run(): Unit {
                val api = AmsatApi()
                Log.v(TAG, "Posting request")
                _reports.postValue(api.getReport(name, 24))
            }
        })
    }

    fun empty() {
        _reports.value = ArrayList<SatReport>()
    }
}
