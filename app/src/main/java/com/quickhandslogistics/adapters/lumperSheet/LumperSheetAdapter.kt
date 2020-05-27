package com.quickhandslogistics.adapters.lumperSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_sheet_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperSheetAdapter(private var resources: Resources, var adapterItemClickListener: LumperSheetContract.View.OnAdapterItemClickListener) :
    Adapter<LumperSheetAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""

    private var tempLumperIds: ArrayList<String> = ArrayList()
    private var lumperInfoList: ArrayList<LumpersInfo> = ArrayList()
    private var filteredEmployeesList: ArrayList<LumpersInfo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_sheet_layout, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredEmployeesList.size else lumperInfoList.size
    }

    private fun getItem(position: Int): LumpersInfo {
        return if (searchEnabled) filteredEmployeesList[position] else lumperInfoList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewStatus: TextView = view.textViewStatus

        fun bind(employeeData: LumpersInfo) {
            UIUtils.showEmployeeProfileImage(context, employeeData.lumperImageUrl, circleImageViewProfile)
            UIUtils.updateProfileBorder(context, tempLumperIds.contains(employeeData.lumperId), circleImageViewProfile)
            textViewLumperName.text = ValueUtils.getDefaultOrValue(employeeData.lumperName).capitalize()
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData.lumperEmployeeId)

            if (ValueUtils.getDefaultOrValue(employeeData.sheetSigned)) {
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
                        val lumperInfo = getItem(adapterPosition)
                        adapterItemClickListener.onItemClick(lumperInfo)
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
            filteredEmployeesList.addAll(lumperInfoList)
        } else {
            for (data in lumperInfoList) {
                val term = ValueUtils.getDefaultOrValue(data.lumperName)
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredEmployeesList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun updateLumperSheetData(lumperInfoList: ArrayList<LumpersInfo>, tempLumperIds: ArrayList<String>) {
        this.lumperInfoList.clear()
        this.lumperInfoList.addAll(lumperInfoList)

        this.tempLumperIds.clear()
        this.tempLumperIds.addAll(tempLumperIds)
        notifyDataSetChanged()
    }
}