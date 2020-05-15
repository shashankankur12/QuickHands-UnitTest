package com.quickhandslogistics.modified.adapters.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.controls.CustomTextView
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class DisplayLumpersListAdapter(
    private val lumpersList: ArrayList<EmployeeData>, var adapterItemClickListener: LumpersContract.View.OnAdapterItemClickListener
) : RecyclerView.Adapter<DisplayLumpersListAdapter.ViewHolder>() {

    private var searchTerm = ""
    private var searchEnabled = false

    private val filteredLumpersList: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredLumpersList.size else lumpersList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return if (searchEnabled) filteredLumpersList[position] else lumpersList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewShiftHours: CustomTextView = view.textViewShiftHours
        private val imageViewCall: ImageView = view.imageViewCall

        fun bind(employeeData: EmployeeData) {
            UIUtils.showEmployeeProfileImage(context, employeeData.profileImageUrl, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
            textViewShiftHours.text = UIUtils.getDisplayShiftHours(employeeData)

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
                        lumperData.phone?.let { phone ->
                            adapterItemClickListener.onPhoneViewClick(textViewLumperName.text.toString(), phone)
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