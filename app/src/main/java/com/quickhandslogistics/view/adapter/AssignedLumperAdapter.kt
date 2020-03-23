package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_assigned_lumpers.view.*


class AssignedLumperAdapter(val context: Activity, private val canReplace: Boolean) :
    Adapter<WorkItemHolder>() {

    var replacedPosition: Int = -1

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_assigned_lumpers, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.lumperText?.text = faker.name.firstName() + " " + faker.name.lastName()

        Picasso.get().load(R.drawable.ic_basic_info_placeholder).error(R.drawable.ic_basic_info_placeholder)
            .into(holder.profilePic)

        if (position == 1 || position == 3) {
            holder.viewAttendanceStatus.setBackgroundResource(R.drawable.offline_dot)
        }

        if (position == replacedPosition) {
            holder.buttonReplace.visibility = View.GONE
            holder.text_replaced.visibility = View.VISIBLE
        } else {
            holder.text_replaced.visibility = View.GONE
            if (canReplace) {
                if (position == 1 || position == 3) {
                    holder.buttonReplace.visibility = View.VISIBLE
                } else {
                    holder.buttonReplace.visibility = View.GONE
                }
            } else {
                holder.buttonReplace.visibility = View.GONE
            }
        }

        holder.buttonReplace.setOnClickListener {
            showAssignLumperDialog(context, position)
        }

    }

    fun showAssignLumperDialog(activity: Activity, position: Int) {

        val dialogAsk = Dialog(activity)

        dialogAsk?.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialogAsk?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAsk?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogAsk?.setCancelable(false)
        dialogAsk.setContentView(R.layout.dialog_assign_lumper_confirmation)

        val window = dialogAsk.window
        window?.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textYes = dialogAsk.findViewById<Button>(R.id.text_yes)
        val textNo = dialogAsk.findViewById<Button>(R.id.text_no)

        textYes.setOnClickListener {
            replacedPosition = position
            notifyDataSetChanged()
            dialogAsk.dismiss()
        }

        textNo.setOnClickListener {
            dialogAsk.dismiss()
        }

        dialogAsk.show()
    }

}

class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.textViewLumperName
    var viewAttendanceStatus: View = view.viewAttendanceStatus
    var profilePic = view.circleImageViewProfile
    var buttonReplace = view.button_replace
    var text_replaced = view.text_replaced

}

