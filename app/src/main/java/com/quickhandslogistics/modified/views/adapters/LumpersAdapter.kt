package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import kotlinx.android.synthetic.main.item_lumper_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumpersAdapter(var adapterItemClickListener: LumpersContract.View.OnAdapterItemClickListener) :
    Adapter<LumpersAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""
    var items: ArrayList<LumperData> = ArrayList()
    private var filteredItems: ArrayList<LumperData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredItems.size else items.size
    }

    private fun getItem(position: Int): LumperData {
        return if (searchEnabled) filteredItems[position] else items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLumpersData(lumperDataList: java.util.ArrayList<LumperData>) {
        items.clear()
        items.addAll(lumperDataList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var lumperText: TextView = view.text_lumper
        var lumperCustId: TextView = view.text_cus_id
        var profilePic: ImageView = view.image_lumper_logo
        var lumperBuilding: TextView = view.text_building_name
        var imagePhone: ImageView = view.image_phone

        fun bind(lumperData: LumperData) {
            if (lumperData.firstName != null && lumperData.lastName != null) {
                lumperText.text = String.format(
                    "%s %s",
                    lumperData.firstName.toUpperCase(Locale.getDefault()),
                    lumperData.lastName.toUpperCase(Locale.getDefault())
                )
            }
            lumperCustId.text = lumperData.email
            lumperBuilding.text = lumperData.role.toUpperCase(Locale.getDefault())

            imagePhone.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val lumperData = getItem(adapterPosition)
                        adapterItemClickListener.onItemClick(lumperData)
                    }
                    imagePhone.id -> {
                        val lumperData = getItem(adapterPosition)
                        adapterItemClickListener.onPhoneViewClick(lumperData.phone)
                    }
                }
            }
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredItems.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredItems.clear()
        if (searchTerm.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            for (data in items) {
//                val term = "${data.firstName} ${data.lastName}"
                val term = data.email
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}