package org.northwinds.amsatstatus.ui.dashboard

import java.util.concurrent.Executors

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.northwinds.amsatstatus.*
import java.util.concurrent.ExecutorService
import javax.inject.Inject

private const val TAG = "AmsatStatus-DashboardVM"

@HiltViewModel
class DashboardViewModel @Inject constructor(private val executor: ExecutorService) : ViewModel() {
    //private val executor = Executors.newSingleThreadScheduledExecutor()
    private val _reports = MutableLiveData<List<SatReport>>()
    private val _reportSlots = MutableLiveData<List<SatReportSlot>>().apply {
        value = AmsatApi().getReportsBySlot("DEMO-1", 24)
    }

    val reports: LiveData<List<SatReport>> = _reports
    val reportSlots: LiveData<List<SatReportSlot>> = _reportSlots

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

    fun updateSlots(name: String) {
        Log.v(TAG, "Clearing results")
        emptySlots()
        Log.v(TAG, "Starting thread for satellite $name")

        executor.execute(object : Runnable {
            override fun run(): Unit {
                val api = AmsatApi()
                Log.v(TAG, "Posting request")
                _reportSlots.postValue(api.getReportsBySlot(name, 24))
            }
        })
    }

    fun emptySlots() {
        _reportSlots.value = ArrayList<SatReportSlot>()
    }
}
