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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.databinding.FragmentDashboardMultiBinding
import org.northwinds.amsatstatus.ui.dashboard.adapters.MultiDashboardViewAdapter

private const val TAG = "AmsatStatus-Dashboard"

/**
 * Dashboard showing recent status of satellites
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mFirebaseAnalytics = Firebase.analytics
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Dashboard")
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)

        val dashboardViewModel: DashboardViewModel by viewModels()

        val binding = FragmentDashboardMultiBinding.inflate(inflater, container, false)

        val nameView = binding.name
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
                val satelliteIds =
                    resources.getStringArray(R.array.satellite_ids)
                val params = Bundle().apply {
                    val item = Bundle().apply {
                        val satelliteNames =
                            resources.getStringArray(R.array.satellite_names)
                        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "satellite")
                        putString(FirebaseAnalytics.Param.ITEM_ID, satelliteIds[position])
                        putString(FirebaseAnalytics.Param.ITEM_NAME, satelliteNames[position])
                    }
                    putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
                    putString(FirebaseAnalytics.Param.ITEM_LIST_ID, "dashboard")
                    putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Dashboard")
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
                dashboardViewModel.updateSlots(satelliteIds[position])
                Log.v(TAG, "Spinner on item selected complete.")
            }
        }
        //val reportView = binding.reports
        //dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
        //    with(reportView) {
        //        adapter = MyReportRecyclerViewAdapter(it)
        //    }
        //})
        val reportView = binding.reports
        dashboardViewModel.reportSlots.observe(viewLifecycleOwner, {
            with(reportView) {
                setAdapter(MultiDashboardViewAdapter(requireActivity(), it))
            }
        })

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val satHeard =
            prefs.getString(requireContext().getString(R.string.preference_satellite), "")
        val idx = requireContext().resources.getStringArray(R.array.satellite_ids).indexOf(satHeard)
        if (idx >= 0)
            nameView.setSelection(idx)
        else
            nameView.setSelection(0)

        Log.v(TAG, "Dashboard onCreateView() complete.")

        return binding.root
    }
}
