package com.quickhandslogistics.adapters.reports

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.ReportsContract
import kotlinx.android.synthetic.main.item_reports.view.*

class ReportsAdapter(private val resources: Resources, private val onAdapterClick: ReportsContract.View.OnAdapterItemClickListener) :
    Adapter<ReportsAdapter.ViewHolder>() {

    private var reportsList: ArrayList<String> = ArrayList()

    init {
        reportsList.add(resources.getString(R.string.time_clock_report))
        reportsList.add(resources.getString(R.string.lumper_sheet_report))
        reportsList.add(resources.getString(R.string.customer_report))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_reports, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return reportsList.size
    }

    private fun getItem(position: Int): String {
        return reportsList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewReportName: TextView = view.textViewReportName

        fun bind(name: String) {
            textViewReportName.text = name
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val reportName = getItem(adapterPosition)
                        openReportScreen(reportName)
                    }
                }
            }
        }
    }

    private fun openReportScreen(reportName: String) {
        when (reportName) {
            resources.getString(R.string.lumper_sheet_report) -> {
                onAdapterClick.showLumperJobReport()
            }
            resources.getString(R.string.time_clock_report) -> {
                onAdapterClick.showTimeClockReport()
            }
            resources.getString(R.string.customer_report) -> {
                onAdapterClick.showCustomerSheetReport()
            }
        }
    }
}