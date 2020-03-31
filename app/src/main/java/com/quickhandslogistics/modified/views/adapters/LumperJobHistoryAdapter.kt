package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_lumper_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class LumperJobHistoryAdapter (var adapterItemClickListener: ChooseLumperContract.View.OnAdapterItemClickListener) :
Adapter<LumperJobHistoryAdapter.ViewHolder>() {
    private var searchEnabled = false
    private var searchTerm = ""
    var items: ArrayList<EmployeeData> = ArrayList()
    private var filteredItems: ArrayList<EmployeeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LumperJobHistoryAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_layout, parent, false)
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

    override fun onBindViewHolder(holder: LumperJobHistoryAdapter.ViewHolder, position: Int) {
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
        var imageViewCall: ImageView = view.imageViewCall

        fun bind(employeeData: EmployeeData) {

            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl))
                Picasso.get().load(employeeData.profileImageUrl).placeholder(R.drawable.dummy)
                    .error(R.drawable.dummy)
                    .into(circleImageViewProfile)

            textViewLumperName.text = String.format(
                "%s %s",
                ValueUtils.getDefaultOrValue(employeeData.firstName),
                ValueUtils.getDefaultOrValue(employeeData.lastName)
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
                            adapterItemClickListener.onClickLumperDetail(lumperData)
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