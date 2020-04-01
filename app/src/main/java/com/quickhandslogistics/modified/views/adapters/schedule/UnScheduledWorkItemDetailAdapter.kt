package com.quickhandslogistics.modified.views.adapters.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.*

class UnScheduledWorkItemDetailAdapter :
    Adapter<UnScheduledWorkItemDetailAdapter.WorkItemHolder>() {

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lumper_layout, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return 7
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.textViewLumperName.text = faker.name.firstName() + " " + faker.name.lastName()

        Picasso.get().load(R.drawable.ic_basic_info_placeholder)
            .error(R.drawable.ic_basic_info_placeholder)
            .into(holder.circleImageViewProfile)
    }

    inner class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewLumperName: TextView = view.textViewLumperName
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var imageViewCall: ImageView = view.imageViewCall

        init {
            imageViewCall.visibility = View.GONE
        }
    }
}