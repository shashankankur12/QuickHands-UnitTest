package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder)
            .into(holder.profilePic)

        if (position == 1 || position == 3) {
            holder.textStatus.setBackgroundResource(R.drawable.chip_complete)
            holder.textStatus.text = "NO SHOW"
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

        dialogAsk.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogAsk.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogAsk.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialogAsk.setCancelable(false)
        dialogAsk.setContentView(R.layout.dialog_assign_lumper_confirmation)

        val window = dialogAsk.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textYes = dialogAsk.findViewById<TextView>(R.id.text_yes)
        val textNo = dialogAsk.findViewById<TextView>(R.id.text_no)

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
    var lumperText = view.text_lumper
    var textStatus = view.text_status
    var profilePic = view.image_lumper_logo
    var buttonReplace = view.button_replace
    var text_replaced = view.text_replaced
    var constraintRoot = view.constraint_root
}

//fun showDialog(name: String, activity: Activity) {
//    val dialog = Dialog(activity)
//
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//    dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
//    dialog.setCancelable(false)
//    dialog.setContentView(R.layout.layout_dialog_lumper_attendence)
//
//    val window = dialog.getWindow()
//    window?.setLayout(
//        LinearLayout.LayoutParams.MATCH_PARENT,
//        LinearLayout.LayoutParams.WRAP_CONTENT
//    )
//
//    var textName = dialog.findViewById<TextView>(R.id.text_lumper_name)
//    var radioAttendance = dialog.findViewById<RadioGroup>(R.id.radio_attendance)
//  //  var comment = dialog.findViewById<EditText>(R.id.text_comment)
//    var buttonSubmit = dialog.findViewById<TextView>(R.id.text_submit)
//    var group = dialog.findViewById<Group>(R.id.group_comment)
//
//    textName.text = name
//
//    radioAttendance.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
//        override fun onCheckedChanged(radioGroup: RadioGroup?, p1: Int) {
//
//            if (radioGroup?.checkedRadioButtonId == R.id.radio_no_show) {
//                group.visibility = View.VISIBLE
//            } else {
//                group.visibility = View.GONE
//            }
//        }
//    })
//
//    buttonSubmit.setOnClickListener {
//
//        dialog.dismiss()
//
//        if (radioAttendance?.checkedRadioButtonId == R.id.radio_no_show) {
//            showAssignLumperDialog(activity)
//        }
//    }
//
//    dialog.show()
//}

