package com.quickhandslogistics.modified.adapters.lumperSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_sheet_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperSheetAdapter(
    private var resources: Resources,
    var adapterItemClickListener: LumperSheetContract.View.OnAdapterItemClickListener
) : Adapter<LumperSheetAdapter.LumperViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""

    private var employeesList: ArrayList<EmployeeData> = ArrayList()
    private var filteredEmployeesList: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LumperViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lumper_sheet_layout, parent, false)
        return LumperViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredEmployeesList.size else employeesList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return if (searchEnabled) filteredEmployeesList[position] else employeesList[position]
    }

    override fun onBindViewHolder(holder: LumperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeesList.clear()
        this.employeesList.addAll(employeeDataList)
        notifyDataSetChanged()
    }

    inner class LumperViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var textViewLumperName: TextView = view.textViewLumperName
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        var textViewEmployeeId: TextView = view.textViewEmployeeId
        var textViewStatus: TextView = view.textViewStatus

        fun bind(employeeData: EmployeeData) {
            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                Glide.with(context).load(employeeData.profileImageUrl)
                    .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                    .into(circleImageViewProfile)
            } else {
                Glide.with(context).clear(circleImageViewProfile);
            }

            textViewLumperName.text = String.format(
                "%s %s",
                ValueUtils.getDefaultOrValue(employeeData.firstName).capitalize(),
                ValueUtils.getDefaultOrValue(employeeData.lastName).capitalize()
            )

            if (StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            }

            if (adapterPosition % 2 == 0) {
                textViewStatus.text = resources.getString(R.string.complete)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
            } else {
                textViewStatus.text = resources.getString(R.string.pending)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
            }

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
            filteredEmployeesList.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredEmployeesList.clear()
        if (searchTerm.isEmpty()) {
            filteredEmployeesList.addAll(employeesList)
        } else {
            for (data in employeesList) {
                val term = "${data.firstName} ${data.lastName}"
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredEmployeesList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}
