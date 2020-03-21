package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.activities.LumperDetailActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.schdule_lumper_image_list.view.*


class SchduleLumperImagesAdapter(var lumperImages: ArrayList<ImageData>, val context: Context) :
    RecyclerView.Adapter<SchduleLumperImagesAdapter.ScheduleImageViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): SchduleLumperImagesAdapter.ScheduleImageViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.schdule_lumper_image_list, viewGroup, false)
        return ScheduleImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lumperImages.size
    }

    inner class ScheduleImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var circleImageView: CircleImageView = itemView.circle_view_1
        var textNLumpers: TextView = itemView.textN

        fun bind(imageData: ImageData) {
            circleImageView.setImageResource(imageData.image)
            //  userEmail.text = country.email
            //  imageView.loadImage(country.avatar)
        }
    }

    override fun onBindViewHolder(scheduleImageViewHolder: ScheduleImageViewHolder, position: Int) {
        /*val totalLumper = lumperImages.size - 4
        val total :String = totalLumper.toString()
        *//*when (lumperImages.size) {
            5 -> scheduleImageViewHolder.textNLumpers.visibility = View.VISIBLE

        }
        scheduleImageViewHolder.textNLumpers.text = "#${total}"*/
       // scheduleImageViewHolder.bind(lumperImages[position])

        scheduleImageViewHolder.circleImageView.setImageResource(lumperImages[position].image)
        if(position  > 4)
            scheduleImageViewHolder.textNLumpers.visibility = View.VISIBLE
        else
            scheduleImageViewHolder.textNLumpers.visibility = View.GONE

        scheduleImageViewHolder.circleImageView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, LumperDetailActivity::class.java)
            context.startActivity(intent)
        })
    }
    }
