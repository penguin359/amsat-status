package org.northwinds.amsatstatus.ui.dashboard.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleExpandableListAdapter
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.Report
import org.northwinds.amsatstatus.SatReportSlot

class MultiDashboardViewAdapter(
    private val context: Context,
    val reportSlots: List<SatReportSlot>
) : SimpleExpandableListAdapter(
    context,
    reportSlots.map { slot ->
        mapOf("status" to slot.report.value, "time" to slot.time.toString().replace('T', '\n'))
    },
    R.layout.fragment_dashboard_multi_group,
    arrayOf("status", "time"),
    intArrayOf(R.id.multi_status, R.id.multi_time),
    reportSlots.map { slot ->
        slot.reports.map { report ->
            mapOf(
                "report" to report.report.value,
                "callsign" to report.callsign,
                "grid" to report.gridSquare
            )
        }
    },
    R.layout.fragment_dashboard_multi_item,
    arrayOf("report", "callsign", "grid"),
    intArrayOf(R.id.multi_report, R.id.multi_callsign, R.id.multi_grid)
) {
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = super.getGroupView(groupPosition, isExpanded, convertView, parent)
        val color = when (reportSlots[groupPosition].report) {
            Report.NOT_HEARD -> R.color.notHeard
            Report.TELEMETRY_ONLY -> R.color.telemetryOnly
            Report.HEARD -> R.color.heard
            Report.CREW_ACTIVE -> R.color.crewActive
            Report.CONFLICTED -> R.color.conflict
        }
        view.findViewById<View>(R.id.multi_group_cell).setBackgroundColor(context.resources.getColor(color))
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view =
            super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent)
        val color = when (reportSlots[groupPosition].reports[childPosition].report) {
            Report.NOT_HEARD -> R.color.notHeard
            Report.TELEMETRY_ONLY -> R.color.telemetryOnly
            Report.HEARD -> R.color.heard
            Report.CREW_ACTIVE -> R.color.crewActive
            else -> 0
        }
        view.findViewById<View>(R.id.multi_cell).setBackgroundColor(context.resources.getColor(color))
        return view
    }
}
