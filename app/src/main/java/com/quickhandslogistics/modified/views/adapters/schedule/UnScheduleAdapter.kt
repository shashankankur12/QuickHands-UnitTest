package com.quickhandslogistics.modified.views.adapters.schedule

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.layout_item_unschedule.view.*

class UnScheduleAdapter(
    private val resources: Resources,
    var adapterItemClickListener: UnScheduleContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<UnScheduleAdapter.UnScheduleViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetail> = ArrayList()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): UnScheduleViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_item_unschedule, viewGroup, false)
        return UnScheduleViewHolder(view)
    }

    override fun onBindViewHolder(unScheduleViewHolder: UnScheduleViewHolder, position: Int) {
        val item = getItem(position)
        unScheduleViewHolder.bind(item)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): ScheduleDetail {
        return workItemsList[position]
    }

    fun updateList(
        workItemsList: ArrayList<ScheduleDetail>,
        lumpersCountList: ArrayList<Int>
    ) {
        this.workItemsList.clear()
        this.workItemsList = workItemsList
        notifyDataSetChanged()
    }

    inner class UnScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, LumperImagesContract.OnItemClickListener {

        private val textViewDate: TextView = itemView.textViewDate
        private val textViewBuildingName: TextView = itemView.textViewBuildingName
        private val textViewScheduleType: TextView = itemView.textViewScheduleType
        private val textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduleDetail: ScheduleDetail) {
            textViewBuildingName.text = scheduleDetail.buildingName
            textViewScheduleType.text = scheduleDetail.scheduleTypeNames
            textViewWorkItemsCount.text = String.format(
                resources.getString(R.string.work_items_count),
                scheduleDetail.totalNumberOfWorkItems
            )

            if (adapterPosition == 0 || getItem(adapterPosition - 1).startDate != scheduleDetail.startDate) {
                scheduleDetail.startDate?.let {
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

            recyclerViewLumpersImagesList.apply {
                adapter = LumperImagesAdapter(ArrayList(), this@UnScheduleViewHolder)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onUnScheduleItemClick(getItem(adapterPosition))
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick()
        }
    }
}