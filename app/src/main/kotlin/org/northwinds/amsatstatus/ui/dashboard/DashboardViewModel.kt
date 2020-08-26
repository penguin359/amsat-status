package org.northwinds.amsatstatus.ui.dashboard

import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

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
    private val _reports = MutableLiveData<List<SatReport>>().apply {
        value = listOf(
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
                "K7IW"
            ),
            SatReport(
                "DEMO-1",
                Report.TELEMETRY_ONLY,
                makeReportTimeFromString("2018-02-27T03:15:00Z"),
                "ZL1D"
            ),
            SatReport(
                "DEMO-1",
                Report.CREW_ACTIVE,
                makeReportTimeFromString("2018-02-27T04:30:00Z"),
                "KG7GAN"
            ),
            SatReport(
                "DEMO-1",
                Report.HEARD,
                makeReportTimeFromString("2018-02-27T05:45:00Z"),
                "AG7NC"
            ),
            SatReport(
                "DEMO-1",
                Report.HEARD,
                makeReportTimeFromString("2018-02-27T06:30:00Z"),
                "OM/DL1IBM"
            )
        )
    }

    val reports: LiveData<List<SatReport>> = _reports

    fun update(name: String) {
        Log.v(TAG, "Clearing results")
        clear()
        Log.v(TAG, "Starting thread for satellite $name")

        executor.execute(object : Runnable {
            override fun run(): Unit {
                val api = AmsatApi()
                Log.v(TAG, "Posting request")
                _reports.postValue(api.getReport(name, 24))
            }
        })
    }

    fun clear() {
        _reports.value = ArrayList<SatReport>()
    }
}
