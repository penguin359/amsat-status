package org.northwinds.amsatstatus

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * [RecyclerView.Adapter] that can display a [SatReport].
 */
class MyReportRecyclerViewAdapter(
    private val values: List<SatReport>
) : RecyclerView.Adapter<MyReportRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.reportView.text = item.report.toString()
        holder.timeView.text = item.time.toString()
        holder.callsignView.text = item.callsign
        holder.gridsquareView.text = item.gridSquare
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
