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
import com.quickhandslogistics.modified.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_sheet_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperSheetAdapter(
    private var resources: Resources, var adapterItemClickListener: LumperSheetContract.View.OnAdapterItemClickListener
) : Adapter<LumperSheetAdapter.LumperViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""

    private var lumperInfoList: ArrayList<LumpersInfo> = ArrayList()
    private var filteredEmployeesList: ArrayList<LumpersInfo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LumperViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_sheet_layout, parent, false)
        return LumperViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredEmployeesList.size else lumperInfoList.size
    }

    private fun getItem(position: Int): LumpersInfo {
        return if (searchEnabled) filteredEmployeesList[position] else lumperInfoList[position]
    }

    override fun onBindViewHolder(holder: LumperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLumperSheetData(lumperInfoList: ArrayList<LumpersInfo>) {
        this.lumperInfoList.clear()
        this.lumperInfoList.addAll(lumperInfoList)
        notifyDataSetChanged()
    }

    inner class LumperViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val textViewStatus: TextView = view.textViewStatus

        fun bind(employeeData: LumpersInfo) {
            if (!StringUtils.isNullOrEmpty(employeeData.lumperImageUrl)) {
                Glide.with(context).load(employeeData.lumperImageUrl).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(circleImageViewProfile)
            } else {
                Glide.with(context).clear(circleImageViewProfile);
            }

            textViewLumperName.text = ValueUtils.getDefaultOrValue(employeeData.lumperName).capitalize()

            if (StringUtils.isNullOrEmpty(employeeData.lumperEmployeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.lumperEmployeeId)
            }

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
}
