package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ImageData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.schdule_lumper_image_list.view.*

class LumperImagesAdapter(
    var lumperImages: ArrayList<ImageData>,
    var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LumperImagesAdapter.ScheduleImageViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): LumperImagesAdapter.ScheduleImageViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.schdule_lumper_image_list, viewGroup, false)
        return ScheduleImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (lumperImages.size > 5) {
            5
        } else {
            lumperImages.size
        }
    }

    override fun onBindViewHolder(scheduleImageViewHolder: ScheduleImageViewHolder, position: Int) {
        if (position > 3) {
            scheduleImageViewHolder.circleImageView.visibility = View.GONE
            scheduleImageViewHolder.textNLumpers.visibility = View.VISIBLE
            scheduleImageViewHolder.textNLumpers.text = "+${lumperImages.size - 4}"
        } else {
            scheduleImageViewHolder.circleImageView.setImageResource(lumperImages[position].image)
            scheduleImageViewHolder.circleImageView.visibility = View.VISIBLE
            scheduleImageViewHolder.textNLumpers.visibility = View.GONE
        }

    }

    inner class ScheduleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var circleImageView: CircleImageView = itemView.circle_view_1
        var textNLumpers: TextView = itemView.textN

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> onItemClickListener.onLumperImageItemClick()
                }
            }
        }
    }
}

interface OnItemClickListener {
    fun onLumperImageItemClick()
}
