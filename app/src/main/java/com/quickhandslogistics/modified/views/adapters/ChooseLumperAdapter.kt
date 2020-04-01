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
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_choose_lumper.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChooseLumperAdapter(var adapterItemClickListener: ChooseLumperContract.View.OnAdapterItemClickListener) :
    Adapter<ChooseLumperAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""
    var items: ArrayList<EmployeeData> = ArrayList()
    private var filteredItems: ArrayList<EmployeeData> = ArrayList()

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

    private fun getItem(position: Int): EmployeeData {
        return if (searchEnabled) filteredItems[position] else items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLumpersData(employeeDataList: java.util.ArrayList<EmployeeData>) {
        items.clear()
        items.addAll(employeeDataList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var textViewLumperName: TextView = view.textViewLumperName
        var textViewEmployeeId: TextView = view.textViewEmployeeId
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var textViewShiftHours: TextView = view.textViewShiftHours

        fun bind(employeeData: EmployeeData) {
            if (employeeData.firstName != null && employeeData.lastName != null) {
                textViewLumperName.text =
                    String.format("%s %s", employeeData.firstName, employeeData.lastName)
            }

            employeeData.employeeId?.also {
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            } ?: run {
                textViewEmployeeId.text = "(Emp ID: -)"
            }

            employeeData.shiftHours?.also {
                textViewShiftHours.text =
                    String.format("(Shift Hours: %s)", employeeData.shiftHours)
            } ?: run {
                textViewShiftHours.text = "Shift Hours: -"
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
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