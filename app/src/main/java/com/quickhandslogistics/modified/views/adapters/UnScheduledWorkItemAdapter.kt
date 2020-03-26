package com.quickhandslogistics.modified.views.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.models.lumpers.LumperListModel
import com.quickhandslogistics.modified.views.activities.schedule.WorkItemDetailActivity
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.view.activities.AddWorkItemLumpersActivity
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_unscheduled_work_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class UnScheduledWorkItemAdapter(
    private var adapterItemClickListener: OnAdapterItemClickListener,
    private val activity: Activity,
    private val sameDay: Boolean,
    private var lumpersCountList: ArrayList<Int>
) :  RecyclerView.Adapter<UnScheduledWorkItemAdapter.WorkItemViewHolder>() {

    var faker = Faker()
    var lumpersList: ArrayList<LumperListModel> = ArrayList()
    private lateinit var  scheduleLumperImageAdapter: LumperImagesAdapter

    init {
        lumpersList.add(LumperListModel("Gene ","Hand", "99896945685"))
        lumpersList.add(LumperListModel("Frida", "Moore","3845798347593"))
        lumpersList.add(LumperListModel("Virgil", "Ernser","3745638476584"))
        lumpersList.add(LumperListModel("Philip", "Von","56348563485684"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_unscheduled_work_item, parent, false)
        return WorkItemViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return lumpersList.size
    }

    fun getItem(position: Int): LumperListModel {
        return lumpersList[position]
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {

        holder.bind(getItem(position))

        //holder.lumperText?.text = "#${faker.company?.name()}"
        if (position == 0) {
            holder.text_startDate.text = "Start Time: 08:00 AM"
        } else if (position == 1) {
            holder.text_startDate.text = "Start Time: 10:00 AM"
        } else if (position == 2) {
            holder.text_startDate.text = "Start Time: 12:00 PM"
        } else if (position == 3) {
            holder.text_startDate.text = "Start Time: 02:00 PM"
        } else if (position == 4) {
            holder.text_startDate.text = "Start Time: 04:00 PM"
        }

        if (sameDay) {
            holder.button_add_lumpers.visibility = View.VISIBLE
            holder.circleImageArrow.visibility = View.GONE
            holder.recyclerviewImages.visibility = View.GONE
            holder.button_add_lumpers.setOnClickListener {
                val intent = Intent(activity, AddWorkItemLumpersActivity::class.java)
                intent.putExtra("position", position)
                activity.startActivityForResult(intent, 101)
                activity.overridePendingTransition(
                    R.anim.anim_next_slide_in,
                    R.anim.anim_next_slide_out
                )
            }
        } else {
            holder.button_add_lumpers.visibility = View.GONE
            holder.circleImageArrow.visibility = View.VISIBLE
            holder.recyclerviewImages.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {
                val intent = Intent(activity, WorkItemDetailActivity::class.java)
                intent.putExtra(WorkItemDetailActivity.ARG_ALLOW_UPDATE, false)
                activity.startActivity(intent)
                activity.overridePendingTransition(
                    R.anim.anim_next_slide_in,
                    R.anim.anim_next_slide_out
                )
            }
        }

        if (lumpersCountList[position] > 0) {

            holder.button_add_lumpers.visibility = View.GONE
            holder.circleImageArrow.visibility = View.VISIBLE
            holder.recyclerviewImages.visibility = View.VISIBLE
        } else {

            if (sameDay) {
                holder.button_add_lumpers.visibility = View.VISIBLE
                holder.circleImageArrow.visibility = View.GONE
                holder.recyclerviewImages.visibility = View.GONE

            } else {
                holder.button_add_lumpers.visibility = View.GONE
                holder.circleImageArrow.visibility = View.VISIBLE
                holder.recyclerviewImages.visibility = View.VISIBLE
            }
        }

       /* holder.recyclerviewImages.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val lumperImages = ArrayList<ImageData>()

            for (i in 1..7) {
                lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
            }
            addItemDecoration(OverlapDecoration())
            scheduleLumperImageAdapter = ScheduleLumperImagesAdapter(lumperImages, this@UnScheduledWorkItemAdapter)
            adapter = scheduleLumperImageAdapter
        }*/

        holder.recyclerviewImages.setOnClickListener(View.OnClickListener {
            adapterItemClickListener.onItemClick()
        })
    }

    fun updateCount(lumpersCountList: ArrayList<Int>) {
        this.lumpersCountList = lumpersCountList
        notifyDataSetChanged()
    }

   inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view), LumperImagesAdapter.OnAdapterItemClickListener {
        var lumperText = view.textViewContainerName
        var text_startDate = view.textViewStartTime
        var circleImageArrow = view.circleImageViewArrow
        var button_add_lumpers = view.textViewAddTime
        var recyclerviewImages = view.recyclerViewLumpersImagesList

        fun bind(lumperModelData: LumperListModel) {
            lumperText?.text = String.format(
                "%s %s",
                lumperModelData.name.toUpperCase(Locale.getDefault()),
                lumperModelData.lastName.toUpperCase(Locale.getDefault()))

            setLumperImagesRecycler()
        }

        private fun setLumperImagesRecycler() {
           recyclerviewImages.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val lumperImages = ArrayList<ImageData>()

                for (i in 1..7) {
                    lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
                }
                addItemDecoration(OverlapDecoration())
               scheduleLumperImageAdapter = LumperImagesAdapter(lumperImages, this@WorkItemViewHolder)
                adapter = scheduleLumperImageAdapter
            }
        }

        override fun onItemClick() {
            adapterItemClickListener.onItemClick()
        }
    }

    interface OnAdapterItemClickListener {
        fun onItemClick()
    }

}