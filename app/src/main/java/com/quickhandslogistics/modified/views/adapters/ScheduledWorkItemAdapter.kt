package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_scheduled_work_item.view.*

class ScheduledWorkItemAdapter(
    private val context: Context,
    private var adapterItemClickListener: ScheduleDetailContract.View.OnAdapterItemClickListener,
    private val sameDay: Boolean
) :
    Adapter<ScheduledWorkItemAdapter.WorkItemViewHolder>() {

    private lateinit var scheduleImageAdapter: ScheduleLumperImagesAdapter
    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_scheduled_work_item, parent, false)
        return WorkItemViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var textViewContainerName: TextView = view.textViewContainerName
        var textViewStartTime: TextView = view.textViewStartTime
        var recyclerViewLumpersImagesList: RecyclerView = view.recyclerViewLumpersImagesList

        fun bind() {
            textViewContainerName.text = "#${faker.company?.name()}"
            textViewStartTime.text =
                String.format(context.getString(R.string.start_time_container), "08:00 AM")

            itemView.setOnClickListener(this)

            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val lumperImages = ArrayList<ImageData>()
                for (i in 1..5) {
                    lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                }
                scheduleImageAdapter = ScheduleLumperImagesAdapter(lumperImages, context)
                addItemDecoration(OverlapDecoration())
                adapter = scheduleImageAdapter
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onWorkItemClick(sameDay)
                }
            }
        }
    }
}