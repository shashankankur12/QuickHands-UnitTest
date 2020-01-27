package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.DialogHelper
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root
import kotlinx.android.synthetic.main.item_lumper_layout.view.image_lumper_logo
import kotlinx.android.synthetic.main.item_lumper_layout.view.text_last_name
import kotlinx.android.synthetic.main.item_lumper_layout.view.text_lumper
import kotlinx.android.synthetic.main.layout_assigned_lumpers.view.*

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
        holder?.lumperText?.text = faker.name.firstName() + " " + faker.name.lastName()

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder).into(holder?.profilePic)

        holder?.delete.setOnClickListener {
            DialogHelper.showDialog("Are you sure you want to delete this lumper ?", context)
        }
    }
}


class AssignedLumperHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var lumperLastName = view.text_last_name
    var profilePic = view.image_lumper_logo
    var delete = view.image_delete
    var constraintRoot = view.constraint_root
}