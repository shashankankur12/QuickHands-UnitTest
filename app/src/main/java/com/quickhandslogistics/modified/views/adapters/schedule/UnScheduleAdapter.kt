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
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import kotlinx.android.synthetic.main.layout_item_unschedule.view.*

class UnScheduleAdapter(
    private val resources: Resources,
    var adapterItemClickListener: UnScheduleContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<UnScheduleAdapter.UnScheduleViewHolder>() {

    private var scheduledData: ArrayList<ScheduleData> = ArrayList()

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
//        val item = getItem(position)
        unScheduleViewHolder.bind()
    }

    override fun getItemCount(): Int {
        return 5
    }

    fun getItem(position: Int): ScheduleData {
        return scheduledData[position]
    }

    fun updateList(scheduledData: ArrayList<ScheduleData>) {
        this.scheduledData.clear()
        this.scheduledData = scheduledData
        notifyDataSetChanged()
    }

    inner class UnScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, LumperImagesContract.OnItemClickListener {

        var textViewBuildingName: TextView = itemView.textViewBuildingName
        var textViewScheduleType: TextView = itemView.textViewScheduleType
        var textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        var recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind() {
//            if (adapterPosition == 0 || getItem(adapterPosition - 1).startDate != workItemDetail.startDate) {
//                workItemDetail.startDate?.let {
//                    textViewDate.text = DateUtils.changeDateString(
//                        DateUtils.PATTERN_API_REQUEST_PARAMETER,
//                        DateUtils.PATTERN_NORMAL,
//                        it
//                    )
//                }
//                textViewDate.visibility = View.VISIBLE
//            } else {
//                textViewDate.visibility = View.GONE
//            }
            recyclerViewLumpersImagesList.apply {
                val lumperImages = ArrayList<ImageData>()
                for (i in 1..5) {
                    lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                }
                adapter = LumperImagesAdapter(lumperImages, this@UnScheduleViewHolder)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onUnScheduleItemClick()
                }
            }
        }

        override fun onLumperImageItemClick() {
            adapterItemClickListener.onLumperImagesClick()
        }
    }
}