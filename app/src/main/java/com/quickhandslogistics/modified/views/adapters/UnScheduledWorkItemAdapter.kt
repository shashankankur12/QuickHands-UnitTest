package com.quickhandslogistics.modified.views.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.utils.DateUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_unscheduled_work_item.view.*

class UnScheduledWorkItemAdapter(
    private val resources: Resources,
    private var adapterItemClickListener: UnScheduleContract.View.OnAdapterItemClickListener
) : RecyclerView.Adapter<UnScheduledWorkItemAdapter.WorkItemViewHolder>() {

    private var lumpersCountList: ArrayList<Int> = ArrayList()
    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_unscheduled_work_item, parent, false)
        return WorkItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    private fun getItem(position: Int): WorkItemDetail {
        return workItemsList[position]
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind(getItem(position), lumpersCountList[position])
    }

    fun updateList(
        workItemsList: ArrayList<WorkItemDetail>,
        lumpersCountList: ArrayList<Int>
    ) {
        this.workItemsList.clear()
        this.workItemsList = workItemsList

        this.lumpersCountList.clear()
        this.lumpersCountList = lumpersCountList
        notifyDataSetChanged()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, OnItemClickListener {

        private val textViewDate: TextView = view.textViewDate
        private val textViewStartTime: TextView = view.textViewStartTime
        private val textViewScheduleType: TextView = view.textViewScheduleType
        private val textViewDropItems: TextView = view.textViewDropItems
        private val circleImageArrow: CircleImageView = view.circleImageViewArrow
        private val textViewAddLumpers: TextView = view.textViewAddLumpers
        private val recyclerViewLumpersImagesList: RecyclerView = view.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(workItemDetail: WorkItemDetail, lumperCount: Int) {
            textViewStartTime.text = String.format(
                resources.getString(R.string.start_time_container),
                workItemDetail.startTime
            )

            when (workItemDetail.workItemType) {
                "drop" -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.no_of_drops),
                        workItemDetail.numberOfDrops
                    )
                    textViewDropItems.visibility = View.VISIBLE
                    textViewScheduleType.text = resources.getString(R.string.string_drops)
                }
                "load" -> {
                    textViewDropItems.visibility = View.GONE
                    textViewScheduleType.text = resources.getString(R.string.string_live_loads)
                }
            }

            if (adapterPosition == 0 || getItem(adapterPosition - 1).startDate != workItemDetail.startDate) {
                workItemDetail.startDate?.let {
                    textViewDate.text = DateUtils.changeDateString(
                        DateUtils.PATTERN_API_REQUEST_PARAMETER,
                        DateUtils.PATTERN_NORMAL,
                        it
                    )
                }
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