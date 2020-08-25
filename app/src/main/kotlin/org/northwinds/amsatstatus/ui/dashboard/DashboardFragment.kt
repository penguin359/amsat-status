package org.northwinds.amsatstatus.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.northwinds.amsatstatus.R

import org.northwinds.amsatstatus.ui.dashboard.adapters.MyReportRecyclerViewAdapter

/**
 * Dashboard showing recent status of satellites
 */
class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        /*
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        */
        val nameView: Spinner = root.findViewById(R.id.name)
        nameView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                dashboardViewModel.clear()
            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, _id: Long) {
                //val spinner = view as Spinner
                //val id = spinner.selectedItemPosition
                val id = position
                val satellite_ids =
                    resources.getStringArray(R.array.satellite_ids)
                /*
                Toast.makeText(
                    activity!!.applicationContext,
                    "Sat Item " + position.toString() + " (" + satellite_ids[id] + ")",
                    Toast.LENGTH_LONG
                ).show()
                */
                dashboardViewModel.update(satellite_ids[id])
            }
        }
        val reportView: RecyclerView = root.findViewById(R.id.reports)
        dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
            with(reportView) {
                adapter = MyReportRecyclerViewAdapter(it)
            }
        })

        return root
    }
}
