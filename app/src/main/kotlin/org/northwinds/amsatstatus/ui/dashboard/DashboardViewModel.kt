/**********************************************************************************
 * Copyright (c) 2020 Loren M. Lang                                               *
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

package org.northwinds.amsatstatus.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.northwinds.amsatstatus.AmsatApi
import org.northwinds.amsatstatus.SatReport
import org.northwinds.amsatstatus.SatReportSlot
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
                //Thread.sleep(15000)
                _reportSlots.postValue(api.getReportsBySlot(name, 24))
            }
        })
    }

    fun emptySlots() {
        _reportSlots.value = ArrayList<SatReportSlot>()
    }
}
