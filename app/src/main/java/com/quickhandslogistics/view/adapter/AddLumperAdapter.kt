package com.quickhandslogistics.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.squareup.picasso.Picasso
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.layout_add_lumpers.view.*
import java.util.*
import kotlin.collections.ArrayList

class AddLumperAdapter(val context: Activity) :
    Adapter<AddLumperAdapter.WorkItemHolder>() {
    var addedLumpersList: ArrayList<LumperModel> = ArrayList()
    var lumpers: ArrayList<LumperModel> = ArrayList()
    var lumpersFilter: ArrayList<LumperModel> = ArrayList()
    private var searchEnabled = false
    private var searchTerm = ""

    init {
        lumpers.add(LumperModel("Gene ","Hand","ffr"))
        lumpers.add(LumperModel("Frida", "Moore",""))
        lumpers.add(LumperModel("Virgil", "Ernser", ""))
        lumpers.add(LumperModel("Philip", "Von", ""))
    }

    var faker = Faker()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_add_lumpers, parent, false)
        return WorkItemHolder(view)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) lumpersFilter.size else lumpers.size
    }

    private fun getItem(position: Int): LumperModel {
        return if (searchEnabled) lumpersFilter[position] else lumpers[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {

        holder.bind(getItem(position))

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

    fun getSelectedLumper(): ArrayList<LumperModel> {
        return lumpers
    }

    fun updateLumpersData(lumperDataList: ArrayList<LumperModel>) {
        lumpers.clear()
        lumpers.addAll(lumperDataList)
        notifyDataSetChanged()
    }

    class WorkItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewLumperName = view.textViewLumperName
        var imageProfile = view.circleImageViewProfile
        var imageViewAdd = view.viewAttendanceStatus

        fun bind(lumperModelData: LumperModel) {
            textViewLumperName.text = String.format(
                "%s %s",
                lumperModelData.name.toUpperCase(Locale.getDefault()),
                lumperModelData.lastName.toUpperCase(Locale.getDefault())
            )

            Picasso.get().load(R.drawable.ic_basic_info_placeholder)
                .error(R.drawable.ic_basic_info_placeholder)
                .into(imageProfile)

        }
        }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            lumpersFilter.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        lumpersFilter.clear()
        if (searchTerm.isEmpty()) {
            lumpersFilter.addAll(lumpers)
        } else {
            for (data in lumpers) {
                val term = "${data.name} ${data.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    lumpersFilter.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
    }