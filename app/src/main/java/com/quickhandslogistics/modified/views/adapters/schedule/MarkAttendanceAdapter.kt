package com.quickhandslogistics.modified.views.adapters.schedule

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
import com.quickhandslogistics.modified.contracts.schedule.MarkAttendanceContract
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_recyclerview_mark_attendance.view.*
import java.util.*
import kotlin.collections.ArrayList


class MarkAttendanceAdapter(
    private var onAdapterClick: MarkAttendanceContract.View.OnAdapterItemClickListener
) :
    Adapter<MarkAttendanceAdapter.WorkItemHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""
    var lumperItems: ArrayList<Attendance> = ArrayList()
    private var filteredLumperItems: ArrayList<Attendance> = ArrayList()

    init {
        lumperItems.add(
            Attendance(
                "Gene Hand",
                true
            )
        )
        lumperItems.add(
            Attendance(
                "Frida Moore",
                false
            )
        )
        lumperItems.add(
            Attendance(
                "Virgil Ernser",
                true
            )
        )
        lumperItems.add(
            Attendance(
                "Philip Von",
                false
            )
        )
    }

    var faker = Faker()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkItemHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recyclerview_mark_attendance, parent, false)
        return WorkItemHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredLumperItems.size else lumperItems.size
    }

    private fun getItem(position: Int): Attendance {
        return if (searchEnabled) filteredLumperItems[position] else lumperItems[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        val lumper = getItem(position)
        holder.textViewLumperName.text = lumper.name

        Picasso.get().load(R.drawable.ic_basic_info_placeholder)
            .error(R.drawable.ic_basic_info_placeholder)
            .into(holder.circleImageViewProfile)

        if (lumper.status) {
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
            getItem(position).status = holder.switchAttendance.isChecked
            notifyDataSetChanged()
        }

        holder.textViewAddTime.setOnClickListener {
            onAdapterClick.onAddTimeClick()
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

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredLumperItems.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredLumperItems.clear()
        if (searchTerm.isEmpty()) {
            filteredLumperItems.addAll(lumperItems)
        } else {
            for (data in lumperItems) {
                val term = data.name
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredLumperItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class Attendance(var name: String, var status: Boolean)
}