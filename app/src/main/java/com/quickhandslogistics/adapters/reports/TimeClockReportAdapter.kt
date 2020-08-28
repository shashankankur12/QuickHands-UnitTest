package com.quickhandslogistics.adapters.reports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.TimeClockReportContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_add_lumpers.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimeClockReportAdapter(private val onAdapterClick: TimeClockReportContract.View.OnAdapterItemClickListener) : Adapter<TimeClockReportAdapter.ViewHolder>() {

    private var employeeDataList: ArrayList<EmployeeData> = ArrayList()
    private var filteredEmployeeDataList: ArrayList<EmployeeData> = ArrayList()

    private var selectedLumperIdsList: ArrayList<String> = ArrayList()
    private var selectedLumpersMap: HashMap<String, EmployeeData> = HashMap()

    private var searchEnabled = false
    private var searchTerm = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_add_lumpers, parent, false)
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

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewShiftHours: CustomTextView = view.textViewShiftHours
        private val imageViewAdd: ImageView = view.imageViewAdd
        private val viewAttendanceStatus: View = view.viewAttendanceStatus

        fun bind(employeeData: EmployeeData) {
            UIUtils.showEmployeeProfileImage(context, employeeData.profileImageUrl, circleImageViewProfile)
            UIUtils.updateProfileBorder(context, employeeData.isTemporaryAssigned, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
            textViewShiftHours.text = UIUtils.getDisplayShiftHours(employeeData)
            viewAttendanceStatus.visibility=View.GONE

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
                            selectedLumpersMap.remove(employeeData.id!!)
                        } else {
                            selectedLumperIdsList.add(employeeData.id!!)
                            selectedLumpersMap[employeeData.id!!] = employeeData
                        }
                        onAdapterClick.onLumperSelectionChanged()
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun isSearchEnabled(): Boolean {
        return searchEnabled
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

    fun getSelectedLumperIdsList(): ArrayList<String> {
        return selectedLumperIdsList
    }

    fun updateLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        setSearchEnabled(false)
        this.employeeDataList.clear()
        this.employeeDataList.addAll(employeeDataList)
        notifyDataSetChanged()
    }

    fun clearAllSelection() {
        setSearchEnabled(false)
        selectedLumpersMap.clear()
        selectedLumperIdsList.clear()

        onAdapterClick.onLumperSelectionChanged()
        notifyDataSetChanged()
    }

    fun invokeSelectAll() {
        if (itemCount > 0) {
//            val selectedCount = getSelectedLumperIdsList().size
//            if (selectedCount == itemCount) {
//                clearAllSelection()
//            } else {
                selectAllLumpers()
//            }
        }
    }

    private fun selectAllLumpers() {
        setSearchEnabled(false)
        for (employeeData in employeeDataList) {
            if (!selectedLumperIdsList.contains(employeeData.id!!)) {
                selectedLumperIdsList.add(employeeData.id!!)
                selectedLumpersMap[employeeData.id!!] = employeeData
            }
        }
        onAdapterClick.onLumperSelectionChanged()
        notifyDataSetChanged()
    }
}