package org.northwinds.amsatstatus.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        //val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.reports.observe(viewLifecycleOwner, Observer {
            if (root is RecyclerView) {
                with(root) {
                    adapter = MyReportRecyclerViewAdapter(it)
                }
            }
        })

        return root
    }
}
