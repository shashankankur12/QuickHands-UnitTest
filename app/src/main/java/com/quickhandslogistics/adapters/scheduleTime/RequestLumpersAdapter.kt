package com.quickhandslogistics.adapters.scheduleTime

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL_Week
import com.quickhandslogistics.utils.DateUtils.Companion.changeUTCDateStringToLocalDateString
import com.quickhandslogistics.utils.DateUtils.Companion.convertMillisecondsToTimeString
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.item_request_lumpers.view.*

class RequestLumpersAdapter(private val resources: Resources, private val isPastDate: Boolean, private val onAdapterClick: RequestLumpersContract.View.OnAdapterItemClickListener) :
    Adapter<RequestLumpersAdapter.ViewHolder>() {

    private val requestList: ArrayList<RequestLumpersRecord> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_request_lumpers, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    private fun getItem(position: Int): RequestLumpersRecord {
        return requestList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewStatus: TextView = view.textViewStatus
        private val textViewRequestedLumpersCount: TextView = view.textViewRequestedLumpersCount
        private val textViewRequestedAt: TextView = view.textViewRequestedAt
        private val textViewNote: TextView = view.textViewNote
        private val textViewUpdateRequest: TextView = view.textViewUpdateRequest
        private val textViewRequestStart: TextView = view.textViewRequestStart
        private val textViewNoteForLumper: TextView = view.textViewNoteForLumper
        private val textViewLumperAssigned: TextView = view.textViewLumperAssigned
        private val textViewRequestCancelledAt: TextView = view.textViewRequestCancelledAt
        private val textViewCancelRequest: TextView = view.textViewCancelRequest
        private val linearLayoutNotes: LinearLayout = view.linearLayoutNotes
        private val recyclerViewTempLumperInfo: RecyclerView = view.recyclerViewTempLumperInfo

        fun bind(requestLumpersRecord: RequestLumpersRecord) {
            textViewRequestedLumpersCount.text = String.format(resources.getString(R.string.requested_lumpers_s), requestLumpersRecord.requestedLumpersCount)
            textViewRequestedAt.text = UIUtils.getSpannedText(String.format(resources.getString(R.string.requested_maded_s), changeUTCDateStringToLocalDateString(PATTERN_API_RESPONSE, PATTERN_NORMAL_Week, requestLumpersRecord.createdAt!!)))
            textViewRequestStart.text = if (requestLumpersRecord.startTime!=null) UIUtils.getSpannedText(String.format(resources.getString(R.string.start_time_bold), convertMillisecondsToTimeString(
                requestLumpersRecord.startTime?.toLong()!!))) else "N/A"

            val assignedCount= if (requestLumpersRecord.lumpersAllocated.isNullOrEmpty()) 0 else requestLumpersRecord.lumpersAllocated!!.size
            val ratioCount= String.format("%s/%s",assignedCount,requestLumpersRecord.requestedLumpersCount)
            textViewLumperAssigned.text = UIUtils.getSpannedText(String.format(resources.getString(R.string.lumpers_dm_assigned_bold), ratioCount))
            textViewNote.text = requestLumpersRecord.notesForDM?.capitalize()

            if (!requestLumpersRecord.lumpersAllocated.isNullOrEmpty()) {
                recyclerViewTempLumperInfo.visibility=View.VISIBLE
                recyclerViewTempLumperInfo.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = RequestLumperInfoAdaptor(resources, requestLumpersRecord.lumpersAllocated!!
                    )
                }
            }else{
                recyclerViewTempLumperInfo.visibility=View.GONE
            }


            when (requestLumpersRecord.requestStatus) {
                AppConstant.REQUEST_LUMPERS_STATUS_PENDING -> {
                    textViewStatus.text = resources.getString(R.string.pending)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                    changeUpdateUIVisibility(!isPastDate)
                    textViewRequestCancelledAt.visibility= View.GONE
                }
                AppConstant.REQUEST_LUMPERS_STATUS_APPROVED -> {
                    textViewStatus.text = resources.getString(R.string.complete)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                    changeUpdateUIVisibility(false)
                    textViewRequestCancelledAt.visibility= View.GONE
                }
                AppConstant.REQUEST_LUMPERS_STATUS_REJECTED -> {
                    textViewStatus.text = resources.getString(R.string.not_approved)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                    changeUpdateUIVisibility(false)
                    textViewRequestCancelledAt.visibility= View.VISIBLE
                    showCancelledTime(requestLumpersRecord.updatedAt, false)
                }
                AppConstant.REQUEST_LUMPERS_STATUS_CANCELLED -> {
                    textViewStatus.text = resources.getString(R.string.cancelled)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                    changeUpdateUIVisibility(false)
                    textViewRequestCancelledAt.visibility= View.VISIBLE
                    showCancelledTime(requestLumpersRecord.updatedAt, true)
                }
            }

            textViewUpdateRequest.setOnClickListener(this)
            textViewCancelRequest.setOnClickListener(this)
            linearLayoutNotes.setOnClickListener(this)
            textViewNoteForLumper.setOnClickListener(this)
        }

        private fun changeUpdateUIVisibility(isShow: Boolean) {
            textViewUpdateRequest.isEnabled = isShow
            textViewCancelRequest.isEnabled = isShow
        }

        private fun showCancelledTime(cancelAt: String?, isShow: Boolean) {
             val textHeading= if(isShow) resources.getString(R.string.request_cancelled_bold) else resources.getString(R.string.request_not_approved_bold)
            cancelAt?.let {
                textViewRequestCancelledAt.text = UIUtils.getSpannedText(String.format(textHeading, changeUTCDateStringToLocalDateString(PATTERN_API_RESPONSE, PATTERN_NORMAL_Week, it)))

            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    linearLayoutNotes.id -> {
                        val record = getItem(adapterPosition)
                        onAdapterClick.onNotesItemClick(record.notesForDM)
                    }
                    textViewNoteForLumper.id -> {
                        val record = getItem(adapterPosition)
                        onAdapterClick.onNotesItemClick(record.notesForLumper)
                    }
                    textViewUpdateRequest.id -> {
                        val record = getItem(adapterPosition)
                        onAdapterClick.onUpdateItemClick(record)
                    }
                    textViewCancelRequest.id -> {
                        val record = getItem(adapterPosition)
                        onAdapterClick.onCancelItemClick(record)
                    }
                }
            }
        }
    }

    fun updateList(records: List<RequestLumpersRecord>) {
        this.requestList.clear()
        this.requestList.addAll(records)

        notifyDataSetChanged()
    }
}