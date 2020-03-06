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
import kotlinx.android.synthetic.main.layout_add_lumpers.view.*


class AddLumperAdapter(val context: Activity) :
    Adapter<AddLumperAdapter.WorkItemHolder>() {

    var addedLumpersList: ArrayList<String> = ArrayList()
    var lumpers: ArrayList<String> = ArrayList()

    init {
        lumpers.add("Gene Hand")
        lumpers.add("Frida Moore")
        lumpers.add("Virgil Ernser")
        lumpers.add("Philip Von")
    }

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
        holder.lumperText?.text = lumpers[position]

        Picasso.get().load(faker.avatar.image()).error(R.drawable.ic_basic_info_placeholder)
            .into(holder.profilePic)

        if (addedLumpersList.contains(lumpers[position])) {
            holder.imageViewAdd.setImageResource(R.drawable.ic_check_box)
        } else {
            holder.imageViewAdd.setImageResource(R.drawable.ic_check_box_outline)
        }

        holder.constraintRoot.setOnClickListener {
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
        var lumperText = view.text_lumper
        var profilePic = view.image_lumper_logo
        var imageViewAdd = view.imageViewAdd
        var constraintRoot = view.constraint_root
    }
}