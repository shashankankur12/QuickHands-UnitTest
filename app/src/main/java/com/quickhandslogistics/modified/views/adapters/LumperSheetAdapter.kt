package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.item_lumper_sheet_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperSheetAdapter(
    var adapterItemClickListener: LumperSheetContract.View.OnAdapterItemClickListener
) : Adapter<LumperSheetAdapter.LumperViewHolder>() {

    var faker = Faker()
    private var searchEnabled = false
    private var searchTerm = ""
    var items: ArrayList<LumperModel> = ArrayList()
    private var filteredItems: ArrayList<LumperModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LumperViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lumper_sheet_layout, parent, false)
        return LumperViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredItems.size else items.size
    }

    private fun getItem(position: Int): LumperModel {
        return if (searchEnabled) filteredItems[position] else items[position]
    }

    override fun onBindViewHolder(holder: LumperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLumpersData(lumperDataList: ArrayList<LumperModel>) {
        items.clear()
        items.addAll(lumperDataList)
        notifyDataSetChanged()
    }

    inner class LumperViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var lumperName = view.text_lumper_name
        var profilePic = view.image_lumper_logo
        var lumperDate = view.text_date
        var lumperStatus = view.text_status

        fun bind(lumperModelData: LumperModel) {
            lumperName.text = String.format(
                "%s %s",
                lumperModelData.name.toUpperCase(Locale.getDefault()),
                lumperModelData.lastName.toUpperCase(Locale.getDefault())
            )

            lumperDate.text = "20 Jan 2020"

            lumperStatus.text =
                String.format("%s", lumperModelData.status.toUpperCase(Locale.getDefault()))

            if (lumperModelData.status.equals(R.string.complete))
                lumperStatus.setBackgroundResource(R.drawable.chip_complete)
            else lumperStatus.setBackgroundResource(R.drawable.chip_in_progress)

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val lumperData = getItem(adapterPosition)
                        adapterItemClickListener.onItemClick(lumperData)
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

    fun filter() {
        filteredItems.clear()
        if (searchTerm.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            for (data in items) {
                val term = "${data.name} ${data.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun setStatusEnabled(searchTerm: String = "") {
        if (!searchEnabled) {
            this.searchTerm = ""
            items.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filterStatus()
    }

    fun filterStatus() {
        filteredItems.clear()
        if (searchTerm.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            for (data in items) {
                val term = data.status

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}
