package com.quickhandslogistics.modified.views.adapters.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.WorkItemDetailContract
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_work_item_detail_lumper.view.*

class ScheduledWorkItemDetailAdapter(
    private val onAdapterClick: WorkItemDetailContract.View.OnAdapterItemClickListener,
    private val allowUpdate: Boolean
) :
    Adapter<ScheduledWorkItemDetailAdapter.WorkItemHolder>() {

    var replacedPosition: Int = -1

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_work_item_detail_lumper, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.textViewLumperName.text = faker.name.firstName() + " " + faker.name.lastName()

        Picasso.get().load(R.drawable.ic_basic_info_placeholder)
            .error(R.drawable.ic_basic_info_placeholder)
            .into(holder.circleImageViewProfile)

        if (position == 1 || position == 3) {
            holder.viewAttendanceStatus.setBackgroundResource(R.drawable.offline_dot)
        }

        if (position == replacedPosition) {
            holder.linearLayoutLumperTime.visibility = View.GONE
            holder.buttonReplace.visibility = View.GONE
            holder.textViewReplaced.visibility = View.VISIBLE
        } else {
            holder.textViewReplaced.visibility = View.GONE
            if (allowUpdate) {
                if (position == 1 || position == 3) {
                    holder.linearLayoutLumperTime.visibility = View.GONE
                    holder.buttonReplace.visibility = View.VISIBLE
                } else {
                    holder.linearLayoutLumperTime.visibility = View.VISIBLE
                    holder.buttonReplace.visibility = View.GONE
                }
            } else {
                holder.buttonReplace.visibility = View.GONE
                if (position == 1 || position == 3) {
                    holder.linearLayoutLumperTime.visibility = View.GONE
                } else {
                    holder.linearLayoutLumperTime.visibility = View.VISIBLE
                }
            }
        }

        holder.buttonReplace.setOnClickListener {
            onAdapterClick.onReplaceItemClick(position)
        }
    }

    fun updateReplacePosition(position: Int) {
        replacedPosition = position
        notifyDataSetChanged()
    }

    inner class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewLumperName: TextView = view.textViewLumperName
        var viewAttendanceStatus: View = view.viewAttendanceStatus
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var buttonReplace: Button = view.buttonReplace
        var textViewReplaced: TextView = view.textViewReplaced
        var linearLayoutLumperTime: LinearLayout = view.linearLayoutLumperTime
    }
}