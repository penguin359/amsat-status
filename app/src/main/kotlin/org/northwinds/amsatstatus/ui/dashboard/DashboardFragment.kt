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

import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.ui.dashboard.adapters.MultiDashboardViewAdapter

import org.northwinds.amsatstatus.ui.dashboard.adapters.MyReportRecyclerViewAdapter

private const val TAG = "AmsatStatus-Dashboard"

/**
 * Dashboard showing recent status of satellites
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mFirebaseAnalytics = Firebase.analytics
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Dashboard")
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)

        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val root = inflater.inflate(R.layout.fragment_dashboard_multi, container, false)
        val listView = root.findViewById<ExpandableListView>(R.id.reports)

        val nameView: Spinner = root.findViewById(R.id.name)
        nameView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                dashboardViewModel.emptySlots()
            }

            override fun onItemSelected(
                p0: AdapterView<*>?,
                view: View?,
                position: Int,
                _id: Long,
            ) {
                val id = position
                val satellite_ids =
                    resources.getStringArray(R.array.satellite_ids)
                val params = Bundle().apply {
                    val item = Bundle().apply {
                        val satelliteNames =
                            resources.getStringArray(R.array.satellite_names)
                        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "satellite")
                        putString(FirebaseAnalytics.Param.ITEM_ID, satellite_ids[id])
                        putString(FirebaseAnalytics.Param.ITEM_NAME, satelliteNames[id])
                    }
                    putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
                    putString(FirebaseAnalytics.Param.ITEM_LIST_ID, "dashboard")
                    putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Dashboard")
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
                dashboardViewModel.updateSlots(satellite_ids[id])
                Log.v(TAG, "Spinner on item selected complete.")
            }
        }
//        val reportView: RecyclerView = root.findViewById(R.id.reports)
//        dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
//            with(reportView) {
//                adapter = MyReportRecyclerViewAdapter(it)
//            }
//        })
        val reportView: ExpandableListView = root.findViewById(R.id.reports)
        dashboardViewModel.reportSlots.observe(viewLifecycleOwner, Observer {
            with(reportView) {
                setAdapter(MultiDashboardViewAdapter(requireActivity(), it))
            }
        })

        val prefs = PreferenceManager(context).sharedPreferences
        val satHeard =
            prefs.getString(requireContext().getString(R.string.preference_satellite), "")
        val idx = requireContext().resources.getStringArray(R.array.satellite_ids).indexOf(satHeard)
        if (idx >= 0)
            nameView.setSelection(idx)
        else
            nameView.setSelection(0)

        Log.v(TAG, "Dashboard onCreateView() complete.")

        return root
    }
}
