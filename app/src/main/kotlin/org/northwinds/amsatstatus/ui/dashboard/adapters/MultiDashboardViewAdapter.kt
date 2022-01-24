/**********************************************************************************
 * Copyright (c) 2021 Loren M. Lang                                               *
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
    val reportSlots: List<SatReportSlot>,
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
        parent: ViewGroup?,
    ): View {
        val view = super.getGroupView(groupPosition, isExpanded, convertView, parent)
        val color = when (reportSlots[groupPosition].report) {
            Report.NOT_HEARD -> R.color.notHeard
            Report.TELEMETRY_ONLY -> R.color.telemetryOnly
            Report.HEARD -> R.color.heard
            Report.CREW_ACTIVE -> R.color.crewActive
            Report.CONFLICTED -> R.color.conflict
        }
        view.findViewById<View>(R.id.multi_group_cell)
            .setBackgroundColor(context.resources.getColor(color))
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
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
        view.findViewById<View>(R.id.multi_cell)
            .setBackgroundColor(context.resources.getColor(color))
        return view
    }
}
