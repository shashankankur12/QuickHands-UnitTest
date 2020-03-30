package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_unscheduled_work_item.view.*

class UnScheduledWorkItemAdapter(
    private val context: Context,
    private var adapterItemClickListener: UnScheduleContract.View.OnAdapterItemClickListener
) : RecyclerView.Adapter<UnScheduledWorkItemAdapter.WorkItemViewHolder>() {

    private var lumpersCountList: ArrayList<Int> = ArrayList()
    private var unScheduledData: ArrayList<ScheduleData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_unscheduled_work_item, parent, false)
        return WorkItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return unScheduledData.size
    }

    private fun getItem(position: Int): ScheduleData {
        return unScheduledData[position]
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind(getItem(position), lumpersCountList[position])
    }

    fun updateList(
        unScheduledData: ArrayList<ScheduleData>,
        lumpersCountList: java.util.ArrayList<Int>
    ) {
        this.unScheduledData.clear()
        this.unScheduledData = unScheduledData

        this.lumpersCountList.clear()
        this.lumpersCountList = lumpersCountList
        notifyDataSetChanged()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, OnItemClickListener {

        var textViewDate: TextView = view.textViewDate
        var textViewStartTime: TextView = view.textViewStartTime
        var textViewScheduleType: TextView = view.textViewScheduleType
        var circleImageArrow: CircleImageView = view.circleImageViewArrow
        var textViewAddLumpers: TextView = view.textViewAddLumpers
        var recyclerViewLumpersImagesList: RecyclerView = view.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduledData: ScheduleData, lumperCount: Int) {
            textViewStartTime.text =
                String.format(context.getString(R.string.start_time_container), "08:00 AM")

            if (adapterPosition == 0 || getItem(adapterPosition - 1).time != scheduledData.time) {
                textViewDate.text = scheduledData.time
                textViewDate.visibility = View.VISIBLE
            } else {
                textViewDate.visibility = View.GONE
            }

            if (lumperCount > 0) {
                textViewAddLumpers.visibility = View.GONE
                circleImageArrow.visibility = View.VISIBLE
                recyclerViewLumpersImagesList.visibility = View.VISIBLE

                recyclerViewLumpersImagesList.apply {
                    val lumperImages = ArrayList<ImageData>()
                    for (i in 0..lumperCount) {
                        lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                    }
                    adapter = LumperImagesAdapter(lumperImages, this@WorkItemViewHolder)
                }
                itemView.setOnClickListener(this)
            } else {
                textViewAddLumpers.visibility = View.VISIBLE
                circleImageArrow.visibility = View.GONE
                recyclerViewLumpersImagesList.visibility = View.GONE

                textViewAddLumpers.setOnClickListener(this)
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onWorkItemClick()
                    textViewAddLumpers.id -> adapterItemClickListener.onAddLumperItemClick()
                }
            }
        }

        override fun onLumperImageItemClick() {
            adapterItemClickListener.onLumperImagesClick()
        }
    }
}