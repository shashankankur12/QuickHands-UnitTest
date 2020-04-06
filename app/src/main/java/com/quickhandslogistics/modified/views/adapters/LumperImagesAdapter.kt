package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.schdule_lumper_image_list.view.*

class LumperImagesAdapter(
    var lumpersList: ArrayList<EmployeeData>,
    var onItemClickListener: LumperImagesContract.OnItemClickListener
) :
    RecyclerView.Adapter<LumperImagesAdapter.ScheduleImageViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): LumperImagesAdapter.ScheduleImageViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.schdule_lumper_image_list, viewGroup, false)
        return ScheduleImageViewHolder(view, viewGroup.context)
    }

    override fun getItemCount(): Int {
        return if (lumpersList.size > 5) {
            5
        } else {
            lumpersList.size
        }
    }

    private fun getItem(position: Int): EmployeeData {
        return lumpersList[position]
    }

    fun updateData(lumpersList: ArrayList<EmployeeData>) {
        this.lumpersList.clear()
        this.lumpersList.addAll(lumpersList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ScheduleImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleImageViewHolder(
        itemView: View, private val context: Context
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var circleImageViewProfile: CircleImageView = itemView.circleImageViewProfile
        var textViewNumber: TextView = itemView.textViewNumber

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(employeeData: EmployeeData) {
            if (adapterPosition > 3) {
                Glide.with(context).clear(circleImageViewProfile);
                circleImageViewProfile.visibility = View.GONE
                textViewNumber.visibility = View.VISIBLE
                textViewNumber.text = "+${lumpersList.size - 4}"
            } else {
                if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                    Glide.with(context).load(employeeData.profileImageUrl)
                        .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                        .into(circleImageViewProfile)
                } else {
                    Glide.with(context).clear(circleImageViewProfile);
                }
                circleImageViewProfile.visibility = View.VISIBLE
                textViewNumber.visibility = View.GONE
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> onItemClickListener.onLumperImageItemClick(lumpersList)
                }
            }
        }
    }
}