package com.quickhandslogistics.modified.adapters.workSheet

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
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import kotlinx.android.synthetic.main.item_select_status.view.*

class WorkItemStatusAdapter(
    private val resources: Resources,
    private val onAdapterClick: WorkSheetItemDetailContract.View.OnAdapterItemClickListener
) : Adapter<WorkItemStatusAdapter.ViewHolder>() {

    private var statusList: ArrayList<String> = ArrayList()
    private var initialStatus: String = ""
    private var selectedStatus: String = ""

    init {
        statusList.add(resources.getString(R.string.in_progress))
        statusList.add(resources.getString(R.string.on_hold))
        statusList.add(resources.getString(R.string.scheduled))
        statusList.add(resources.getString(R.string.cancelled))
        statusList.add(resources.getString(R.string.completed))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_status, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    private fun getItem(position: Int): String {
        return statusList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateInitialStatus(initialStatus: String) {
        this.initialStatus = initialStatus
        selectedStatus = initialStatus
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewStatus: TextView = view.textViewStatus
        private val imageViewAdd: ImageView = view.imageViewAdd

        fun bind(status: String) {
            textViewStatus.text = status

            when (status) {
                resources.getString(R.string.in_progress) -> textViewStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_green_light)
                )
                resources.getString(R.string.on_hold) -> textViewStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_orange_dark)
                )
                resources.getString(R.string.scheduled) -> textViewStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_blue_light)
                )
                resources.getString(R.string.cancelled) -> textViewStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_red_dark)
                )
                resources.getString(R.string.completed) -> textViewStatus.setTextColor(
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                )
            }

            if (selectedStatus == status) {
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
                        val status = getItem(adapterPosition)
                        if (selectedStatus != status) {
                            selectedStatus = status
                            notifyDataSetChanged()
                            onAdapterClick.onSelectStatus(status)
                        }
                    }
                }
            }
        }
    }

}