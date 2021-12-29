package org.northwinds.amsatstatus.ui.dashboard.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleExpandableListAdapter
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.Report
import org.northwinds.amsatstatus.SatReportSlot

//var a = SimpleExpandableListAdapter(
//SimpleExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo)

class MultiDashboardViewAdapter(private val context: Context,
                                groupData: List<Map<String, String>>,
                                groupLayout: Int,
                                groupFrom: Array<String>,
                                groupTo: IntArray,
                                childData: List<List<Map<String, String>>>,
                                childLayout: Int,
                                childFrom: Array<String>,
                                childTo: IntArray) :
    SimpleExpandableListAdapter(context,
        groupData,
        groupLayout,
        groupFrom,
        groupTo,
        childData,
        childLayout,
        childFrom,
        childTo) {

    constructor(context: Context, reportSlots: List<SatReportSlot>) : this(context,
            ArrayList<Map<String, String>>(),
            R.layout.fragment_dashboard_multi_group,
            arrayOf("status", "time"),
            intArrayOf(R.id.multi_status, R.id.multi_time),
            ArrayList<ArrayList<Map<String, String>>>(),
            R.layout.fragment_dashboard_multi_item,
            arrayOf("report", "callsign", "grid"),
            intArrayOf(R.id.multi_report, R.id.multi_callsign, R.id.multi_grid)) {
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = super.getGroupView(groupPosition, isExpanded, convertView, parent)
        val report = getGroup(groupPosition) as Map<String, String>
        val color = when(report["status"]) {
            Report.NOT_HEARD.value -> R.color.notHeard
            Report.TELEMETRY_ONLY.value -> R.color.telemetryOnly
            Report.HEARD.value -> R.color.heard
            Report.CREW_ACTIVE.value -> R.color.crewActive
            Report.CONFLICTED.value -> R.color.conflict
            else -> 0
        }
/*
        val color = when(report.report) {
            Report.NOT_HEARD -> R.color.notHeard
            Report.TELEMETRY_ONLY -> R.color.telemetryOnly
            Report.HEARD -> R.color.heard
            Report.CREW_ACTIVE -> R.color.crewActive
            Report.CONFLICTED -> R.color.conflict
        }
*/
        view.setBackgroundColor(context.resources.getColor(color))
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent)
        val report = getChild(groupPosition, childPosition) as Map<String, String>
        val color = when(report["report"]) {
            Report.NOT_HEARD.value -> R.color.notHeard
            Report.TELEMETRY_ONLY.value -> R.color.telemetryOnly
            Report.HEARD.value -> R.color.heard
            Report.CREW_ACTIVE.value -> R.color.crewActive
            else -> 0
        }
/*
        val color = when(report.report) {
            Report.NOT_HEARD -> R.color.notHeard
            Report.TELEMETRY_ONLY -> R.color.telemetryOnly
            Report.HEARD -> R.color.heard
            Report.CREW_ACTIVE -> R.color.crewActive
        }
*/
        view.setBackgroundColor(context.resources.getColor(color))
        return view
    }
}
