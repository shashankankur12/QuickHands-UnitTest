package com.quickhandslogistics.adapters.lumperSheet

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.ContainerDetailItemAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.data.lumperSheet.CorrectionRequest
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
        private val waitingTimeHeader: TextView = itemView.waitingTimeHeader
        private val noteForCustomerHeader: TextView = itemView.noteForCustomerHeader
        private val textViewWaitingTime: TextView = itemView.textViewWaitingTime
        private val textViewBreakTime: TextView = itemView.textViewBreakTime
        private val textViewWorkDone: TextView = itemView.textViewWorkDone
        private val textViewRequestCorrection: TextView = itemView.textViewRequestCorrection
        private val textViewCancelCorrection: TextView = itemView.textViewCancelCorrection
        private val textViewUpdateCorrection: TextView = itemView.textViewUpdateCorrection
        private val textViewScheduleNote: TextView = itemView.textViewScheduleNote
        private val textViewIsScheduleLead: TextView = itemView.textViewIsScheduleLead
        private val editLumperParams: ImageView = itemView.editLumperParams

        init {
            recyclerViewBO.apply {
                layoutManager = GridLayoutManager(context, 2)
            }

            clickableViewBO.setOnClickListener(this)
            linearLayoutCustomerNotes.setOnClickListener(this)
            linearLayoutQHLNotes.setOnClickListener(this)
            textViewRequestCorrection.setOnClickListener(this)
            textViewCancelCorrection.setOnClickListener(this)
            textViewUpdateCorrection.setOnClickListener(this)
            textViewScheduleNote.setOnClickListener(this)
            editLumperParams.setOnClickListener(this)
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
                workItemDetail.containerNumber?.let {
                    textViewWorkItemType.text = UIUtils.getSpannableText(container, it.toString())
                }

                workItemDetail.schedule?.let {
                    textViewScheduleNote.text = ScheduleUtils.scheduleTypeNote(
                            it,
                            resources
                    )
                }
                textViewScheduleNote.isEnabled=!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals(
                    "NA"
                )
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

                if (!workItemDetail.buildingParams.isNullOrEmpty()) {
                    relativeLayoutBO.visibility = View.VISIBLE
                    recyclerViewBO.adapter = ContainerDetailItemAdapter(workItemDetail.buildingOps, workItemDetail.buildingParams, workItemDetail.isCompleted, resources)
                } else {
                    relativeLayoutBO.visibility = View.GONE
                }
                val cases=getTotalCases(workItemDetail.buildingOps)
                totalcase = if (!cases.isNullOrEmpty() && isNumeric(cases!!)) cases else ""
                textViewIsScheduleLead.visibility = if (workItemDetail.isScheduledByLead!!) View.VISIBLE else View.GONE

                if (workItemDetail.corrections!=null && workItemDetail.corrections?.status == AppConstant.CORRECTION_PENDING ){
                    textViewRequestCorrection.isEnabled= false
                    textViewUpdateCorrection.visibility= View.VISIBLE
                    textViewCancelCorrection.visibility= View.VISIBLE
                    setCorrectionStatus(workItemDetail.corrections)

                }else{
                    textViewRequestCorrection.isEnabled= true
                    textViewRequestCorrection.text= resources.getString(R.string.request_correction)
                    textViewRequestCorrection.setTextColor(resources.getColor(R.color.buttonRed))
                    textViewUpdateCorrection.visibility= View.GONE
                    textViewCancelCorrection.visibility= View.GONE
                }

                if (workItemDetail.isCompleted == true){
                    textViewWaitingTime.setTextColor(resources.getColor(R.color.detailHeader))
                    waitingTimeHeader.setTextColor(resources.getColor(R.color.detailHeader))
                    textViewCustomerNote.setTextColor(resources.getColor(R.color.detailHeader))
                    noteForCustomerHeader.setTextColor(resources.getColor(R.color.detailHeader))
                }else {
                    textViewWaitingTime.setTextColor(resources.getColor(R.color.scheduleDetail))
                    textViewCustomerNote.setTextColor(resources.getColor(R.color.scheduleDetail))
                    noteForCustomerHeader.setTextColor(resources.getColor(R.color.scheduleDetail))
                    waitingTimeHeader.setTextColor(resources.getColor(R.color.buildingTitle))
                }
            }

            lumperDaySheet.lumpersTimeSchedule?.let { timingDetail ->
                val startTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.startTime)
                val endTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, timingDetail.endTime)
                if (startTime.isNotEmpty() && endTime.isNotEmpty())
                    textViewWorkTime.text = String.format("%s - %s : %s", startTime, endTime, DateUtils.getDateTimeCalculeted(timingDetail.startTime!!, timingDetail.endTime!!))
                else textViewWorkTime.text = String.format("%s - %s", if (startTime.isNotEmpty()) startTime else AppConstant.NOTES_NOT_AVAILABLE, if (endTime.isNotEmpty()) endTime else AppConstant.NOTES_NOT_AVAILABLE)

                val waitingTime = ValueUtils.getDefaultOrValue(timingDetail.waitingTime)
                if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                    val waitingTimeHours = ValueUtils.getHoursFromMinutes(timingDetail.waitingTime)
                    val waitingTimeMinutes = ValueUtils.getRemainingMinutes(timingDetail.waitingTime)
                    textViewWaitingTime.text = String.format("%s H %s M", waitingTimeHours, waitingTimeMinutes )
                } else {
                    textViewWaitingTime.text = AppConstant.NOTES_NOT_AVAILABLE
                }

                var dateTime: Long = 0
                timingDetail.breakTimes?.let {
                    for (pauseTime in it) {
                        dateTime += ( DateUtils.getMillisecondsFromDateString(DateUtils.PATTERN_API_RESPONSE, pauseTime.endTime)-DateUtils.getMillisecondsFromDateString(DateUtils.PATTERN_API_RESPONSE, pauseTime.startTime))
                    }
                }
                if (dateTime>0)
                    textViewBreakTime.text =DateUtils.getDateTimeCalculatedLong(dateTime)

                else textViewBreakTime.text=AppConstant.NOTES_NOT_AVAILABLE

                if (!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt()!=0) {
                    if (!totalcase.isNullOrEmpty()) {
                        val percent = String.format("%.2f", calculatePercent(timingDetail.partWorkDone!!, totalcase)) + "%"
                        textViewWorkDone.text = String.format("%s / %s : %s", timingDetail.partWorkDone, totalcase, percent)
                    }
                } else {
                    textViewWorkDone.text =AppConstant.NOTES_NOT_AVAILABLE
                }

            }
        }

        private fun setCorrectionStatus(corrections: CorrectionRequest?) {
        corrections?.status?.let {
            when(it){
                AppConstant.CORRECTION_PENDING -> {
                    updateUiCorrectionTextColor(resources.getString(R.string.pending_correction), resources.getColor(R.color.color_yellow))
                    updateUICorrection(true)
                }
                AppConstant.CORRECTION_REJECTED -> {
                    updateUiCorrectionTextColor(resources.getString(R.string.rejected_correction), resources.getColor(R.color.detailHeader))
                    updateUICorrection(false)
                }
                AppConstant.CORRECTION_CANCELLED -> {
                    updateUiCorrectionTextColor(resources.getString(R.string.cancel_correction), resources.getColor(R.color.detailHeader))
                    updateUICorrection(false)
                }
                AppConstant.CORRECTION_APPROVED -> {
                    updateUiCorrectionTextColor(resources.getString(R.string.approved_correction), resources.getColor(R.color.buildingTitle))
                    updateUICorrection(false)
                }
            }
        }
        }

        private fun updateUiCorrectionTextColor(textString: String, color: Int) {
            textViewRequestCorrection.text= textString
            textViewRequestCorrection.setTextColor(color)
        }

        private fun updateUICorrection(isEnable: Boolean) {
            textViewCancelCorrection.isEnabled= isEnable
            textViewUpdateCorrection.isEnabled= isEnable
        }
        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    clickableViewBO.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.let { workItemDetail ->
                            if (!workItemDetail.buildingParams.isNullOrEmpty())
                                adapterItemClickListener.onBOItemClick(workItemDetail, workItemDetail.buildingParams!!)
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
                            adapterItemClickListener.requestCorrection(lumperDaySheet)
                    }
                    editLumperParams.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                            adapterItemClickListener.editLumperParams(lumperDaySheet)
                    }
                    textViewCancelCorrection.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                        lumperDaySheet.workItemDetail?.corrections?.id?.let {
                            adapterItemClickListener.cancelRequestCorrection(it)
                        }
                    }
                    textViewUpdateCorrection.id -> {
                        val lumperDaySheet = getItem(adapterPosition)
                            adapterItemClickListener.updateRequestCorrection(lumperDaySheet)
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