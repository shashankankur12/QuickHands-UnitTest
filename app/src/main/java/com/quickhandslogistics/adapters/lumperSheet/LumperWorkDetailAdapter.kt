package com.quickhandslogistics.adapters.lumperSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.ContainerDetailItemAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.ValueUtils
import kotlinx.android.synthetic.main.item_lumper_work_detail.view.*

class LumperWorkDetailAdapter(private val resources: Resources, private var adapterItemClickListener: LumperWorkDetailContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<LumperWorkDetailAdapter.ViewHolder>() {

    private val lumperDaySheetList: ArrayList<LumperDaySheet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_lumper_work_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lumperDaySheetList.size
    }

    fun getItem(position: Int): LumperDaySheet {
        return lumperDaySheetList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val textViewWorkItemType: TextView = itemView.textViewWorkItemType
        private val textViewStartTime: TextView = itemView.textViewStartTime
        private val textViewCustomerNote: TextView = itemView.textViewCustomerNote
        private val textViewQHLNote: TextView = itemView.textViewQHLNote
        private val textViewStatus: TextView = itemView.textViewStatus
        private val clickableViewBO: View = itemView.clickableViewBO
        private val recyclerViewBO: RecyclerView = itemView.recyclerViewBO
        private val linearLayoutCustomerNotes: LinearLayout = itemView.linearLayoutCustomerNotes
        private val linearLayoutQHLNotes: LinearLayout = itemView.linearLayoutQHLNotes
        private val textViewWorkTime: TextView = itemView.textViewWorkTime
        private val textViewWaitingTime: TextView = itemView.textViewWaitingTime
        private val textViewBreakTime: TextView = itemView.textViewBreakTime

        init {
            recyclerViewBO.apply {
                layoutManager = LinearLayoutManager(context)
            }

            clickableViewBO.setOnClickListener(this)
            linearLayoutCustomerNotes.setOnClickListener(this)
            linearLayoutQHLNotes.setOnClickListener(this)
        }

        fun bind(lumperDaySheet: LumperDaySheet) {
            lumperDaySheet.workItemDetail?.let { workItemDetail ->
                val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
                textViewWorkItemType.text = workItemTypeDisplayName
                textViewStartTime.text = String.format(resources.getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))

                if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() && workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
                    linearLayoutCustomerNotes.visibility = View.VISIBLE
                    textViewCustomerNote.text = workItemDetail.notesQHLCustomer
                } else {
                    linearLayoutCustomerNotes.visibility = View.GONE
                }

                if (!workItemDetail.notesQHL.isNullOrEmpty() && workItemDetail.notesQHL != AppConstant.NOTES_NOT_AVAILABLE) {
                    linearLayoutQHLNotes.visibility = View.VISIBLE
                    textViewQHLNote.text = workItemDetail.notesQHL
                } else {
                    linearLayoutQHLNotes.visibility = View.GONE
                }

                ScheduleUtils.changeStatusUIByValue(resources, workItemDetail.status, textViewStatus)

                recyclerViewBO.adapter = ContainerDetailItemAdapter(
                    workItemDetail.buildingOps,
                    workItemDetail.buildingDetailData?.parameters
                )
            }

            lumperDaySheet.lumpersTimeSchedule?.let { timingDetail ->
                val startTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.startTime)
                val endTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.endTime)
                textViewWorkTime.text = String.format("%s - %s", if (startTime.isNotEmpty()) startTime else "NA", if (endTime.isNotEmpty()) endTime else "NA")

                val waitingTime = ValueUtils.getDefaultOrValue(timingDetail.waitingTime)
                if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                    textViewWaitingTime.text = String.format("%s Min", waitingTime)
                } else {
                    textViewWaitingTime.text = "NA"
                }

                val breakTimeStart = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.breakTimeStart)
                val breakTimeEnd = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.breakTimeEnd)
                textViewBreakTime.text = String.format("%s - %s", if (breakTimeStart.isNotEmpty()) breakTimeStart else "NA", if (breakTimeEnd.isNotEmpty()) breakTimeEnd else "NA")
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    clickableViewBO.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.onBOItemClick(workItemDetail)
                        }
                    }
                    linearLayoutCustomerNotes.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.onNotesItemClick(workItemDetail.notesQHLCustomer)
                        }
                    }
                    linearLayoutQHLNotes.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.onNotesItemClick(workItemDetail.notesQHL)
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun updateWorkDetails(lumperDaySheetList: ArrayList<LumperDaySheet>) {
        this.lumperDaySheetList.clear()
        this.lumperDaySheetList.addAll(lumperDaySheetList)
        notifyDataSetChanged()
    }
}