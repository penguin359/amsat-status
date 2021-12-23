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
    private val _demo_reports = listOf(
        SatReport(
            "DEMO-2",
            Report.HEARD,
            makeReportTimeFromString("2018-02-27T02:00:00Z"),
            "AB1C"
        ),
        SatReport(
            "DEMO-1",
            Report.NOT_HEARD,
            makeReportTimeFromString("2018-02-27T03:00:00Z"),
            "K7IW",
            "CN85nu"
        ),
        SatReport(
            "DEMO-1",
            Report.TELEMETRY_ONLY,
            makeReportTimeFromString("2018-02-27T03:15:00Z"),
            "ZL1D",
            "CN96az"
        ),
        SatReport(
            "DEMO-1",
            Report.CREW_ACTIVE,
            makeReportTimeFromString("2018-02-27T04:30:00Z"),
            "KG7GAN",
            "DM43",
        ),
        SatReport(
            "DEMO-1",
            Report.HEARD,
            makeReportTimeFromString("2018-02-27T05:45:00Z"),
            "AG7NC",
            "CM59ax"
        ),
        SatReport(
            "DEMO-1",
            Report.HEARD,
            makeReportTimeFromString("2018-02-27T06:30:00Z"),
            "OM/DL1IBM"
        )
    )

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val _reports = MutableLiveData<List<SatReport>>().apply {
        value = _demo_reports
    }

    val reports: LiveData<List<SatReport>> = _reports

    fun update(name: String) {
        Log.v(TAG, "Clearing results")
        empty()
        Log.v(TAG, "Starting thread for satellite $name")

        executor.execute(object : Runnable {
            override fun run(): Unit {
                val api = AmsatApi()
                Log.v(TAG, "Posting request")
                if(name == "DEMO-1") {
                    _reports.postValue(_demo_reports)
                } else {
                    _reports.postValue(api.getReport(name, 24))
                }
            }
        })
    }

    fun empty() {
        _reports.value = ArrayList<SatReport>()
    }
}
