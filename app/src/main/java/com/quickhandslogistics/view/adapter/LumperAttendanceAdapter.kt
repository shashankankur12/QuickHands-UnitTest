package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_recyclerview_mark_attendance.view.*


class LumperAttendanceAdapter(val context: Activity) :
    Adapter<LumperAttendanceAdapter.WorkItemHolder>() {
    var showStatus: ArrayList<String> = ArrayList()
    var lumpers: ArrayList<String> = ArrayList()

    init {
        showStatus.add("SHOW")
        showStatus.add("NO SHOW")
        showStatus.add("SHOW")
        showStatus.add("NO SHOW")

        lumpers.add("Gene Hand")
        lumpers.add("Frida Moore")
        lumpers.add("Virgil Ernser")
        lumpers.add("Philip Von")
    }

    var faker = Faker()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkItemHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_recyclerview_mark_attendance, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.lumperText?.text = lumpers[position]

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder)
            .into(holder.profilePic)

        if (showStatus[position] == "SHOW") {
            holder.textStatus.setBackgroundResource(R.drawable.chip_in_progress)
            holder.textStatus.text = "SHOW"
            holder.switch_show.isChecked = true
        } else {
            holder.textStatus.setBackgroundResource(R.drawable.chip_complete)
            holder.textStatus.text = "NO SHOW"
            holder.switch_show.isChecked = false
        }

        holder.switch_show.setOnClickListener {
            showStatus[position] = if (holder.switch_show.isChecked) "SHOW" else "NO SHOW"
            notifyDataSetChanged()
        }
    }

    class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lumperText = view.text_lumper
        var textStatus = view.text_status
        var profilePic = view.image_lumper_logo
        var switch_show = view.switch_show
        var constraintRoot = view.constraint_root
    }
}
