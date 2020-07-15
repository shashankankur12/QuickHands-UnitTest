package com.quickhandslogistics.adapters.workSheet

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.item_select_status.view.*

class WorkSheetItemStatusAdapter(private val resources: Resources, private val onAdapterClick: WorkSheetItemDetailContract.View.OnAdapterItemClickListener) :
    Adapter<WorkSheetItemStatusAdapter.ViewHolder>() {

    private var statusList: LinkedHashMap<String, String> = LinkedHashMap()
    private var initialDisplayStatus: String = ""
    private var selectedDisplayStatus: String = ""

    init {
        statusList[resources.getString(R.string.scheduled)] = AppConstant.WORK_ITEM_STATUS_SCHEDULED
        statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
        statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
        statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
        statusList[resources.getString(R.string.completed)] = AppConstant.WORK_ITEM_STATUS_COMPLETED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_select_status, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    private fun getItem(position: Int): Pair<String, String> {
        val key = ArrayList(statusList.keys)[position]
        val value = statusList[key]
        return Pair(key!!, value!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewStatus: TextView = view.textViewStatus
        private val imageViewAdd: ImageView = view.imageViewAdd

        fun bind(pair: Pair<String, String>) {
            textViewStatus.text = pair.first

            when (pair.first) {
                resources.getString(R.string.in_progress) -> textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
                resources.getString(R.string.on_hold) -> textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
                resources.getString(R.string.scheduled) -> textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_light))
                resources.getString(R.string.cancelled) -> textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                resources.getString(R.string.completed) -> textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }

            if (selectedDisplayStatus == pair.first) {
                textViewStatus.typeface = Typeface.DEFAULT_BOLD
                imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick)
            } else {
                textViewStatus.typeface = Typeface.DEFAULT
                imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick_blank)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val pair = getItem(adapterPosition)
                        if (selectedDisplayStatus != pair.first) {
                            selectedDisplayStatus = pair.first
                            notifyDataSetChanged()
                            onAdapterClick.onSelectStatus(pair.second)
                        }
                    }
                }
            }
        }
    }

    fun updateInitialStatus(initialStatus: String) {
        this.initialDisplayStatus = initialStatus
        selectedDisplayStatus = initialStatus
        notifyDataSetChanged()
    }

    fun updateStatusList(statusList: LinkedHashMap<String, String>) {
        this.statusList.clear()
        this.statusList.putAll(statusList)
        notifyDataSetChanged()
    }
}