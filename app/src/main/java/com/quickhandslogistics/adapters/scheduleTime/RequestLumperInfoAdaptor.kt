package com.quickhandslogistics.adapters.scheduleTime

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.scheduleTime.leadinfo.RequestLumperInfo
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL_Week
import kotlinx.android.synthetic.main.item_request_lumper_info.view.*
import java.util.*


class RequestLumperInfoAdaptor(private val resources: Resources, val lumpersAllocated: List<RequestLumperInfo>) :
    RecyclerView.Adapter<RequestLumperInfoAdaptor.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_request_lumper_info, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumpersAllocated.size
    }

    private fun getItem(position: Int): RequestLumperInfo {
        return lumpersAllocated?.get(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

        private val textViewLumperInfo: TextView = view.textViewLumperInfo

        fun bind(lumpersAllocated: RequestLumperInfo) {
            val countNumber = adapterPosition + 1
            val lumperName = lumpersAllocated.firstName + lumpersAllocated.lastName
            val lumperId = lumpersAllocated.employeeId
            val time = lumpersAllocated.updatedAt?.let { DateUtils.changeUTCDateStringToLocalDateString(PATTERN_API_RESPONSE, PATTERN_NORMAL_Week, it) }
            textViewLumperInfo.text = String.format(
                resources.getString(R.string.requested_lumper_info),
                countNumber,
                lumperName.capitalize(),
                lumperId,
                time
            )
        }

    }


}