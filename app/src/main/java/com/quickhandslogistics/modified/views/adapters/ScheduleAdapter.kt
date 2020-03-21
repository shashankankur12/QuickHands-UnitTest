package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import kotlinx.android.synthetic.main.layout_item_schedule.view.*

class ScheduleAdapter(var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var scheduledData: ArrayList<ScheduleData> = ArrayList()
    private lateinit var scheduleLumperImageAdapter: SchduleLumperImagesAdapter

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

        val item = getItem(position)
        scheduleViewHolder.bind(item)
    }

    override fun getItemCount(): Int {
        return scheduledData.size
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
        View.OnClickListener {

        var textViewBuildingName: TextView = itemView.textViewBuildingName
        var textViewCreatedDate: TextView = itemView.textViewCreatedDate
        var textViewScheduleType: TextView = itemView.textViewScheduleType
        var textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        var recyclerviewImages :RecyclerView = itemView.recyclerViewLumpersImagesList

        fun bind(item: ScheduleData) {
            itemView.setOnClickListener(this)
            setLumperImagesRecycler()
        }

        private fun setLumperImagesRecycler() {
            recyclerviewImages.apply {
                layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                val lumperImages = java.util.ArrayList<ImageData>()

                for (i in 1..5) {
                    lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                }
                scheduleLumperImageAdapter = SchduleLumperImagesAdapter(lumperImages,context)
                addItemDecoration(OverlapDecoration())
                adapter = scheduleLumperImageAdapter
                scheduleLayoutAnimation()
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        adapterItemClickListener.onItemClick()
                    }
                }
            }
        }
    }
}