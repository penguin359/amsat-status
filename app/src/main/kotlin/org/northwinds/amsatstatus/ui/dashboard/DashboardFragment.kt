package org.northwinds.amsatstatus.ui.dashboard

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import org.northwinds.amsatstatus.R

import org.northwinds.amsatstatus.ui.dashboard.adapters.MyReportRecyclerViewAdapter

/**
 * Dashboard showing recent status of satellites
 */
class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mFirebaseAnalytics = Firebase.analytics
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Dashboard")
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val root = inflater.inflate(R.layout.fragment_dashboard_multi, container, false)
        val listView = root.findViewById<ExpandableListView>(R.id.reports)

//        dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
//            with(reportView) {
//                adapter = MyReportRecyclerViewAdapter(it)
//            }
//        })

        /*
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        */
        val nameView: Spinner = root.findViewById(R.id.name)
        nameView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                dashboardViewModel.emptySlots()
            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, _id: Long) {
                //val spinner = view as Spinner
                //val id = spinner.selectedItemPosition
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
                /*
                Toast.makeText(
                    activity!!.applicationContext,
                    "Sat Item " + position.toString() + " (" + satellite_ids[id] + ")",
                    Toast.LENGTH_LONG
                ).show()
                */
                dashboardViewModel.updateSlots(satellite_ids[id])
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
                val reportSlots = dashboardViewModel.reportSlots.value!!
                val reportGroups = ArrayList<Map<String, String>>()
                val reportItems = ArrayList<ArrayList<Map<String, String>>>()
                for(slot in reportSlots) {
                    reportGroups.add(mapOf("status" to slot.report.value, "time" to slot.time.toString()))
                    val list = ArrayList<Map<String, String>>()
                    for(report in slot.reports) {
                        list.add(mapOf("report" to report.report.value, "callsign" to report.callsign, "grid" to report.gridSquare))
                    }
                    reportItems.add(list)
                }
                setAdapter(SimpleExpandableListAdapter(requireActivity(),
                        reportGroups,
                    R.layout.fragment_dashboard_multi_group,
                    arrayOf("status", "time"),
                    intArrayOf(R.id.multi_status, R.id.multi_time),
                    reportItems,
                    R.layout.fragment_dashboard_multi_item,
                    arrayOf("report", "callsign", "grid"),
                    intArrayOf(R.id.multi_report, R.id.multi_callsign, R.id.multi_grid)))
            }
        })

        val prefs = PreferenceManager(context).sharedPreferences
        val satHeard = prefs.getString(requireContext().getString(R.string.preference_satellite), "")
        val idx = requireContext().resources.getStringArray(R.array.satellite_ids).indexOf(satHeard)
        if(idx >= 0)
            nameView.setSelection(idx)

        return root
    }
}
