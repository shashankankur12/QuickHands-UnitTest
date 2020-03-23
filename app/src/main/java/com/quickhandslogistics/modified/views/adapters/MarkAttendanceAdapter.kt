package com.quickhandslogistics.modified.views.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_recyclerview_mark_attendance.view.*


class MarkAttendanceAdapter(val context: Activity) :
    Adapter<MarkAttendanceAdapter.WorkItemHolder>() {
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
            LayoutInflater.from(context)
                .inflate(R.layout.item_recyclerview_mark_attendance, parent, false)
        return WorkItemHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.textViewLumperName.text = lumpers[position]

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder)
            .into(holder.circleImageViewProfile)

        if (showStatus[position] == "SHOW") {
            holder.viewAttendanceStatus.setBackgroundResource(R.drawable.online_dot)
            holder.switchAttendance.isChecked = true
            holder.textViewAddTime.visibility = View.VISIBLE
            holder.linearLayoutLumperTime.visibility = View.VISIBLE
            holder.textViewNoTimeLoggedIn.visibility = View.GONE
        } else {
            holder.viewAttendanceStatus.setBackgroundResource(R.drawable.offline_dot)
            holder.switchAttendance.isChecked = false
            holder.textViewAddTime.visibility = View.GONE
            holder.linearLayoutLumperTime.visibility = View.GONE
            holder.textViewNoTimeLoggedIn.visibility = View.VISIBLE
        }

        holder.switchAttendance.setOnClickListener {
            showStatus[position] = if (holder.switchAttendance.isChecked) "SHOW" else "NO SHOW"
            notifyDataSetChanged()
        }
    }

    class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewLumperName: TextView = view.textViewLumperName
        var viewAttendanceStatus: View = view.viewAttendanceStatus
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var switchAttendance: Switch = view.switchAttendance
        var textViewAddTime: TextView = view.textViewAddTime
        var textViewNoTimeLoggedIn: TextView = view.textViewNoTimeLoggedIn
        var linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime
        var textViewShiftTime: TextView = view.textViewShiftTime
        var textViewLunchTime: TextView = view.textViewLunchTime
    }
}
