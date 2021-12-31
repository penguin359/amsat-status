package org.northwinds.amsatstatus.ui.dashboard.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.northwinds.amsatstatus.SatReport
import org.northwinds.amsatstatus.R
import org.northwinds.amsatstatus.Report

/**
 * [RecyclerView.Adapter] that can display a [SatReport].
 */
class MyReportRecyclerViewAdapter(
    private val values: List<SatReport>
) : RecyclerView.Adapter<MyReportRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_dashboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val reportColor = when (item.report) {
            Report.HEARD -> R.color.heard
            Report.TELEMETRY_ONLY -> R.color.telemetryOnly
            Report.NOT_HEARD -> R.color.notHeard
            Report.CREW_ACTIVE -> R.color.crewActive
            Report.CONFLICTED -> R.color.conflict
        }

        holder.nameView.text = item.name
        holder.reportView.text = item.report.toString()
        holder.timeView.text = item.time.toString()
        holder.callsignView.text = item.callsign
        holder.gridsquareView.text = item.gridSquare
        holder.mainView.setBackgroundResource(reportColor)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView: ViewGroup = view.findViewById(R.id.main)
        val nameView: TextView = view.findViewById(R.id.name)
        val reportView: TextView = view.findViewById(R.id.report)
        val timeView: TextView = view.findViewById(R.id.time)
        val callsignView: TextView = view.findViewById(R.id.callsign)
        val gridsquareView: TextView = view.findViewById(R.id.gridsquare)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }
}
