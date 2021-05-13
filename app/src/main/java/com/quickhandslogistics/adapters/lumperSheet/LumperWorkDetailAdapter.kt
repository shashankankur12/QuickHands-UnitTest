package com.quickhandslogistics.adapters.lumperSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.ContainerDetailItemAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ScheduleUtils.calculatePercent
import com.quickhandslogistics.utils.ValueUtils.isNumeric
import kotlinx.android.synthetic.main.item_lumper_work_detail.view.*
import java.util.*
import kotlin.collections.ArrayList


class LumperWorkDetailAdapter(
        private val resources: Resources, private val sharedPref: SharedPref, private var context : Context,
        private var adapterItemClickListener: LumperWorkDetailContract.View.OnAdapterItemClickListener
) :
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
        private val relativeLayoutBO: RelativeLayout = itemView.relativeLayoutBO
        private val recyclerViewBO: RecyclerView = itemView.recyclerViewBO
        private val linearLayoutCustomerNotes: LinearLayout = itemView.linearLayoutCustomerNotes
        private val linearLayoutQHLNotes: LinearLayout = itemView.linearLayoutQHLNotes
        private val textViewWorkTime: TextView = itemView.textViewWorkTime
        private val textViewWaitingTime: TextView = itemView.textViewWaitingTime
        private val textViewBreakTime: TextView = itemView.textViewBreakTime
        private val textViewWorkDone: TextView = itemView.textViewWorkDone
        private val textViewRequestCorrection: TextView = itemView.textViewRequestCorrection
        private val textViewCancelCorrection: TextView = itemView.textViewCancelCorrection
        private val textViewUpdateCorrection: TextView = itemView.textViewUpdateCorrection
        private val textViewScheduleNote: TextView = itemView.textViewScheduleNote
        private val textViewIsScheduleLead: TextView = itemView.textViewIsScheduleLead

        var parameters = ArrayList<String>()

        init {
            recyclerViewBO.apply {
                layoutManager = GridLayoutManager(context, 2)
            }

            parameters = ScheduleUtils.getBuildingParametersList(sharedPref)

            clickableViewBO.setOnClickListener(this)
            linearLayoutCustomerNotes.setOnClickListener(this)
            linearLayoutQHLNotes.setOnClickListener(this)
            textViewRequestCorrection.setOnClickListener(this)
            textViewCancelCorrection.setOnClickListener(this)
            textViewUpdateCorrection.setOnClickListener(this)
            textViewScheduleNote.setOnClickListener(this)
        }

        fun bind(lumperDaySheet: LumperDaySheet) {
            var totalcase = ""
            lumperDaySheet.workItemDetail?.let { workItemDetail ->
                val container = when (workItemDetail.type) {
                    AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND -> {
                        if (workItemDetail.origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                            resources.getString(R.string.unfinished_out_bound_bold_has)
                        } else resources.getString(R.string.out_bound_bold_has)

                    }
                    AppConstant.WORKSHEET_WORK_ITEM_LIVE -> {
                        if (workItemDetail.origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                            resources.getString(R.string.unfinished_live_load_bold_has)
                        } else resources.getString(R.string.live_load_bold_has)
                    }
                    else -> {
                        if (workItemDetail.origin == AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME) {
                            resources.getString(R.string.unfinished_no_of_drops_bold_has)
                        } else resources.getString(R.string.no_of_drops_bold_has)
                    }
                }
                workItemDetail.label?.let {
                    textViewWorkItemType.text = UIUtils.getSpannableText(container, it)
                }

                if (workItemDetail.schedule!=null) {
                    textViewScheduleNote.text = ScheduleUtils.scheduleTypeNote(
                        workItemDetail.schedule,
                        resources
                    )
                }
                textViewScheduleNote.isEnabled=!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals(
                    "NA"
                )

//                val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplay(workItemDetail.type, resources)
//                textViewWorkItemType.text = workItemDetail.type?.toLowerCase()?.capitalize()
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

                if (!parameters.isNullOrEmpty()) {
                    relativeLayoutBO.visibility = View.VISIBLE
                    recyclerViewBO.adapter = ContainerDetailItemAdapter(workItemDetail.buildingOps, parameters)
                } else {
                    relativeLayoutBO.visibility = View.GONE
                }
                var cases=getTotalCases(workItemDetail.buildingOps)
                totalcase = if (!cases.isNullOrEmpty() && isNumeric(cases!!)) cases else ""
                textViewIsScheduleLead.visibility = if (workItemDetail.isScheduledByLead!!) View.VISIBLE else View.GONE
            }

            lumperDaySheet.lumpersTimeSchedule?.let { timingDetail ->
                val startTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.startTime)
                val endTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.endTime)
                if (startTime.isNotEmpty() && endTime.isNotEmpty())
                    textViewWorkTime.text = String.format("%s - %s : %s", startTime, endTime, DateUtils.getDateTimeCalculeted(timingDetail.startTime!!, timingDetail.endTime!!))
                else textViewWorkTime.text = String.format("%s - %s", if (startTime.isNotEmpty()) startTime else "NA", if (endTime.isNotEmpty()) endTime else "NA")

                val waitingTime = ValueUtils.getDefaultOrValue(timingDetail.waitingTime)
                if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                    textViewWaitingTime.text = String.format("%s Min", waitingTime)
                } else {
                    textViewWaitingTime.text = "NA"
                }

                val breakTimeStart = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.breakTimeStart)
                val breakTimeEnd = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.breakTimeEnd)

                if (breakTimeStart.isNotEmpty() && breakTimeEnd.isNotEmpty())
                    textViewBreakTime.text = String.format("%s - %s :%s", breakTimeStart, breakTimeEnd, DateUtils.getDateTimeCalculeted(timingDetail.breakTimeStart!!, timingDetail.breakTimeEnd!!))
                else textViewBreakTime.text = String.format("%s - %s", if (breakTimeStart.isNotEmpty()) breakTimeStart else "NA", if (breakTimeEnd.isNotEmpty()) breakTimeEnd else "NA")

                if (!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt()!=0) {
                    if (!totalcase.isNullOrEmpty()) {
                        val percent = String.format("%.2f", calculatePercent(timingDetail.partWorkDone!!, totalcase)) + "%"
                        textViewWorkDone.text = String.format("%s / %s : %s", timingDetail.partWorkDone, totalcase, percent)
                    }
                } else {
                    textViewWorkDone.text ="NA"
                }

            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    clickableViewBO.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.onBOItemClick(workItemDetail, parameters)
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
                    textViewRequestCorrection.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.requestCorrection(workItemDetail.id)
                        }
                    }
                    textViewCancelCorrection.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.cancelRequestCorrection(workItemDetail.id)
                        }
                    }
                    textViewUpdateCorrection.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            adapterItemClickListener.requestCorrection(workItemDetail.id)
                        }
                    }
                    textViewScheduleNote.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            if (!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals(
                                    "NA"
                                )
                            ) {
                                val title =
                                    ScheduleUtils.scheduleNotePopupTitle(
                                        workItemDetail.schedule,
                                        resources
                                    )
                                CustomProgressBar.getInstance()
                                    .showInfoDialog(
                                        title,
                                        workItemDetail.schedule?.scheduleNote!!,
                                        context
                                    )
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }
    private fun getTotalCases(permeters: HashMap<String, String>?): String? {
        var cases: String = ""
        if (!permeters.isNullOrEmpty() && permeters.size > 0) {
            cases = permeters.get("Cases").toString()
        }
        return cases
    }
    fun updateWorkDetails(lumperDaySheetList: ArrayList<LumperDaySheet>) {
        this.lumperDaySheetList.clear()

        for (lumperDaySheet in lumperDaySheetList) {
//            if (lumperDaySheet.workItemDetail?.status == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
                this.lumperDaySheetList.add(lumperDaySheet)
//            }
        }
        notifyDataSetChanged()
    }
}