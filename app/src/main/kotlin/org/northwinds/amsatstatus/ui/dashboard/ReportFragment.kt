package org.northwinds.amsatstatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment representing a list of Items.
 */
class ReportFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    val reports: ArrayList<SatReport> = ArrayList<SatReport>()

    init {
        reports.add(SatReport("DEMO-1", Report.HEARD, makeReportTimeFromString("2018-02-27T02:00:00Z"), "AB1C"))
        reports.add(SatReport("DEMO-1", Report.NOT_HEARD, makeReportTimeFromString("2018-02-27T03:00:00Z"), "K7IW"))
        reports.add(SatReport("DEMO-1", Report.TELEMETRY_ONLY, makeReportTimeFromString("2018-02-27T03:15:00Z"), "ZL1D"))
        reports.add(SatReport("DEMO-1", Report.CREW_ACTIVE, makeReportTimeFromString("2018-02-27T04:30:00Z"), "KG7GAN"))
        reports.add(SatReport("DEMO-1", Report.HEARD, makeReportTimeFromString("2018-02-27T05:45:00Z"), "AG7NC"))
        reports.add(SatReport("DEMO-1", Report.HEARD, makeReportTimeFromString("2018-02-27T06:30:00Z"), "OM/DL1IBM"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyReportRecyclerViewAdapter(reports)
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ReportFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}