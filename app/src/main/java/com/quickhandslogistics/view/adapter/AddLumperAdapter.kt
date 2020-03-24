package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_add_lumpers.view.*

class AddLumperAdapter(val context: Activity) :
    Adapter<AddLumperAdapter.WorkItemHolder>() {

    var addedLumpersList: ArrayList<String> = ArrayList()
    var lumpers: ArrayList<String> = ArrayList()
    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_add_lumpers, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {

        for (i in 1..7) {
            lumpers.add(faker.name.firstName()+ faker.name.lastName())
        }
        holder.lumperText?.text = lumpers[position]

        Picasso.get().load(R.drawable.ic_basic_info_placeholder).error(R.drawable.ic_basic_info_placeholder)
            .into(holder.profilePic)

        if (addedLumpersList.contains(lumpers[position])) {
            holder.imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick)
        } else {
            holder.imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick_blank)
        }

        holder.itemView.setOnClickListener {
            val lumper = lumpers[position]
            if (addedLumpersList.contains(lumper)) {
                addedLumpersList.remove(lumper)
            } else {
                addedLumpersList.add(lumper)
            }
            notifyDataSetChanged()
        }

    }

    fun getSelectedLumper(): ArrayList<String> {
        return addedLumpersList
    }

    class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperText = view.textViewLumperName
        var profilePic = view.circleImageViewProfile
        var imageViewAdd = view.viewAttendanceStatus
    }
}