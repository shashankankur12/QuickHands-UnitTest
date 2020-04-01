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
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import kotlinx.android.synthetic.main.layout_item_schedule.view.*

class ScheduleAdapter(
    private val resources: Resources,
    var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var scheduledData: ArrayList<ScheduleData> = ArrayList()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ScheduleViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_item_schedule, viewGroup, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(scheduleViewHolder: ScheduleViewHolder, position: Int) {
//        val item = getItem(position)
        scheduleViewHolder.bind()
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

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
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
            recyclerViewLumpersImagesList.apply {
                val lumperImages = ArrayList<ImageData>()
                for (i in 1..5) {
                    lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                }
                adapter = LumperImagesAdapter(lumperImages, this@ScheduleViewHolder)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onScheduleItemClick()
                }
            }
        }

        override fun onLumperImageItemClick() {
            adapterItemClickListener.onLumperImagesClick()
        }
    }
}