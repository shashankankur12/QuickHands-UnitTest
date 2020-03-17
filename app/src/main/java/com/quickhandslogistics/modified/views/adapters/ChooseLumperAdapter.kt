package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.LumperData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_choose_lumper.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChooseLumperAdapter(var adapterItemClickListener: ChooseLumperContract.View.OnAdapterItemClickListener) :
    Adapter<ChooseLumperAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""
    var items: ArrayList<LumperData> = ArrayList()
    private var filteredItems: ArrayList<LumperData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_choose_lumper, parent, false)
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

        var textViewLumperName: TextView = view.textViewLumperName
        var textViewEmployeeId: TextView = view.textViewEmployeeId
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var textViewShiftHours: TextView = view.textViewShiftHours
        var buttonSelect: Button = view.buttonSelect

        fun bind(lumperData: LumperData) {
            if (lumperData.firstName != null && lumperData.lastName != null) {
                textViewLumperName.text =
                    String.format("%s %s", lumperData.firstName, lumperData.lastName)
            }

            lumperData.employeeId?.also {
                textViewEmployeeId.text = String.format("(Emp ID: %s)", lumperData.employeeId)
            } ?: run {
                textViewEmployeeId.text = "(Emp ID: -)"
            }

            lumperData.shiftHours?.also {
                textViewShiftHours.text = String.format("(Shift Hours: %s)", lumperData.shiftHours)
            } ?: run {
                textViewShiftHours.text = "Shift Hours: -"
            }

            buttonSelect.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val lumperData = getItem(adapterPosition)
                        adapterItemClickListener.onClickLumperDetail(lumperData)
                    }
                    buttonSelect.id -> {
                        val lumperData = getItem(adapterPosition)
                        adapterItemClickListener.onSelectLumper(lumperData)
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
                val term = "${data.firstName} ${data.lastName}"
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}