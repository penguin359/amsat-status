package org.northwinds.amsatstatus.ui.dashboard

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.northwinds.amsatstatus.AmsatApi

import org.northwinds.amsatstatus.SatReport
import org.northwinds.amsatstatus.Report
import org.northwinds.amsatstatus.makeReportTimeFromString

private class Updater(val node: MutableLiveData<List<SatReport>>) : AsyncTask<String, Void, Unit>() {
    override protected fun doInBackground(vararg name: String?) {
        val api = AmsatApi()
        node.postValue(api.getReport(name[0]!!, 24))
    }
}

class DashboardViewModel : ViewModel() {
    private val _reports = MutableLiveData<List<SatReport>>().apply {
        val entries = ArrayList<SatReport>()
        entries.add(SatReport("DEMO-2", Report.HEARD, makeReportTimeFromString("2018-02-27T02:00:00Z"), "AB1C"))
        entries.add(SatReport("DEMO-1", Report.NOT_HEARD, makeReportTimeFromString("2018-02-27T03:00:00Z"), "K7IW"))
        entries.add(SatReport("DEMO-1", Report.TELEMETRY_ONLY, makeReportTimeFromString("2018-02-27T03:15:00Z"), "ZL1D"))
        entries.add(SatReport("DEMO-1", Report.CREW_ACTIVE, makeReportTimeFromString("2018-02-27T04:30:00Z"), "KG7GAN"))
        entries.add(SatReport("DEMO-1", Report.HEARD, makeReportTimeFromString("2018-02-27T05:45:00Z"), "AG7NC"))
        entries.add(SatReport("DEMO-1", Report.HEARD, makeReportTimeFromString("2018-02-27T06:30:00Z"), "OM/DL1IBM"))
        value = entries
    }

    val reports: LiveData<List<SatReport>> = _reports

    fun update(name: String) {
        clear()
        Updater(_reports).execute(name)
    }

    fun clear() {
        _reports.value = ArrayList<SatReport>()
    }
    /*
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    */
}
