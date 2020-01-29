package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.quickhandslogistics.R
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root
import kotlinx.android.synthetic.main.item_lumper_layout.view.image_lumper_logo
import kotlinx.android.synthetic.main.item_lumper_layout.view.text_lumper
import kotlinx.android.synthetic.main.layout_assigned_lumpers.view.*
import android.widget.LinearLayout
import com.quickhandslogistics.view.activities.LumperListActivity


class AssignedLumperAdapter(val items: ArrayList<String>, val context: Activity) :
    Adapter<AssignedLumperHolder>() {

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignedLumperHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_assigned_lumpers, parent, false)
        return AssignedLumperHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: AssignedLumperHolder, position: Int) {
        holder.lumperText?.text = faker.name.firstName() + " " + faker.name.lastName()

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder).into(holder.profilePic)

        if (position == 2 || position == 4) {
            holder.textStatus.setBackgroundResource(R.drawable.chip_complete)
            holder.textStatus.text = "No Show"
        }

        holder.constraintRoot.setOnClickListener {
            showDialog(faker.name.firstName() + " " + faker.name.lastName(), context)
        }
    }
}


class AssignedLumperHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var textStatus = view.text_status
    var profilePic = view.image_lumper_logo
    var constraintRoot = view.constraint_root
}

fun showDialog(name: String, activity: Activity) {
    val dialog = Dialog(activity)

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.layout_dialog_lumper_attendence)

    val window = dialog.getWindow()
    window?.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    var textName = dialog.findViewById<TextView>(R.id.text_lumper_name)
    var radioAttendance = dialog.findViewById<RadioGroup>(R.id.radio_attendance)
    var comment = dialog.findViewById<EditText>(R.id.text_comment)
    var buttonSubmit = dialog.findViewById<TextView>(R.id.text_submit)
    var group = dialog.findViewById<Group>(R.id.group_comment)

    textName.text = name

    radioAttendance.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(radioGroup: RadioGroup?, p1: Int) {

            if (radioGroup?.checkedRadioButtonId == R.id.radio_no_show) {
                group.visibility = View.VISIBLE
            } else {
                group.visibility = View.GONE
            }
        }
    })

    buttonSubmit.setOnClickListener {

        dialog.dismiss()

        if (radioAttendance?.checkedRadioButtonId == R.id.radio_no_show) {
            showAssignLumperDialog(activity)
        }
    }

    dialog.show()
}

fun showAssignLumperDialog(activity: Activity) {

    val dialogAsk = Dialog(activity)

    dialogAsk.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogAsk.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialogAsk.window?.attributes?.windowAnimations = R.style.dialogAnimation
    dialogAsk.setCancelable(false)
    dialogAsk.setContentView(R.layout.dialog_assign_lumper_confirmation)

    val window = dialogAsk.window
    window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    var textYes = dialogAsk.findViewById<TextView>(R.id.text_yes)
    var textNo = dialogAsk.findViewById<TextView>(R.id.text_no)

    textYes.setOnClickListener {
        activity.startActivity(Intent(activity, LumperListActivity::class.java))
        activity.overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        dialogAsk.dismiss()
    }

    textNo.setOnClickListener {
        dialogAsk.dismiss()
    }

    dialogAsk.show()
}