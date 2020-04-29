package com.quickhandslogistics.modified.adapters.workSheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_add_lumpers.view.*
import java.util.*
import kotlin.collections.ArrayList

class AllWorkScheduleCancelAdapter(
    private val onAdapterClick: AllWorkScheduleCancelContract.View.OnAdapterItemClickListener
) : Adapter<AllWorkScheduleCancelAdapter.ViewHolder>() {

    private var employeeDataList: ArrayList<EmployeeData> = ArrayList()
    private var filteredEmployeeDataList: ArrayList<EmployeeData> = ArrayList()

    private var selectedLumperIdsList: ArrayList<String> = ArrayList()

    private var searchEnabled = false
    private var searchTerm = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_lumpers, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredEmployeeDataList.size else employeeDataList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return if (searchEnabled) filteredEmployeeDataList[position] else employeeDataList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedLumper(): ArrayList<String> {
        return selectedLumperIdsList
    }

    fun updateLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        setSearchEnabled(false)
        this.employeeDataList.clear()
        this.employeeDataList.addAll(employeeDataList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewShiftHours: TextView = view.textViewShiftHours
        private val imageViewAdd: ImageView = view.imageViewAdd

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

            if (StringUtils.isNullOrEmpty(employeeData.shiftHours)) {
                textViewShiftHours.visibility = View.GONE
            } else {
                textViewShiftHours.visibility = View.VISIBLE
                textViewShiftHours.text =
                    String.format("(Shift Hours: %s)", employeeData.shiftHours)
            }

            if (selectedLumperIdsList.contains(employeeData.id!!)) {
                imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick)
            } else {
                imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick_blank)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val employeeData = getItem(adapterPosition)
                        if (selectedLumperIdsList.contains(employeeData.id!!)) {
                            selectedLumperIdsList.remove(employeeData.id!!)
                        } else {
                            selectedLumperIdsList.add(employeeData.id!!)
                        }
                        onAdapterClick.onSelectLumper(selectedLumperIdsList.size)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredEmployeeDataList.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredEmployeeDataList.clear()
        if (searchTerm.isEmpty()) {
            filteredEmployeeDataList.addAll(employeeDataList)
        } else {
            for (data in employeeDataList) {
                val term = "${data.firstName} ${data.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredEmployeeDataList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}