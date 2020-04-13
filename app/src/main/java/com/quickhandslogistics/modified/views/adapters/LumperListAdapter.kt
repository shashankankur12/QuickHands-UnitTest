package com.quickhandslogistics.modified.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperListAdapter(
    private val lumpersList: ArrayList<EmployeeData>,
    var adapterItemClickListener: LumpersContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<LumperListAdapter.WorkItemHolder>() {

    private val filteredLumpersList: ArrayList<EmployeeData> = ArrayList()

    private var searchEnabled = false
    private var searchTerm = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_layout, parent, false)
        return WorkItemHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredLumpersList.size else lumpersList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return if (searchEnabled) filteredLumpersList[position] else lumpersList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkItemHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewShiftHours: TextView = view.textViewShiftHours
        private val imageViewCall: ImageView = view.imageViewCall

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

            imageViewCall.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val lumperData = getItem(adapterPosition)
                        adapterItemClickListener.onItemClick(lumperData)
                    }
                    imageViewCall.id -> {
                        val lumperData = getItem(adapterPosition)
                        lumperData.phone?.let { it1 ->
                            adapterItemClickListener.onPhoneViewClick(
                                textViewLumperName.text.toString(),
                                it1
                            )
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredLumpersList.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredLumpersList.clear()
        if (searchTerm.isEmpty()) {
            filteredLumpersList.addAll(lumpersList)
        } else {
            for (data in lumpersList) {
                val term = "${data.firstName} ${data.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredLumpersList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}