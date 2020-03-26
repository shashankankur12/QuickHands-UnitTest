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

class LumperImagesAdapter(var lumperImages: ArrayList<ImageData>, var adapterItemClickListener: OnAdapterItemClickListener) :
    RecyclerView.Adapter<LumperImagesAdapter.ScheduleImageViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): LumperImagesAdapter.ScheduleImageViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.schdule_lumper_image_list, viewGroup, false)
        return ScheduleImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (lumperImages.size > 5) {
            return 5
        } else {
            return lumperImages.size
        }
    }

    inner class ScheduleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleImageView: CircleImageView = itemView.circle_view_1
        var textNLumpers: TextView = itemView.textN

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

        scheduleImageViewHolder.circleImageView.setOnClickListener {
            adapterItemClickListener.onItemClick()
        }

    }

    interface OnAdapterItemClickListener {
        fun onItemClick()
    }

}
