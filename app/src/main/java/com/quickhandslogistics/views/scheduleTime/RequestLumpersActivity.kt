package com.quickhandslogistics.views.scheduleTime

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.scheduleTime.RequestLumpersAdapter
import com.quickhandslogistics.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.presenters.scheduleTime.RequestLumpersPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.AppUtils.Companion.hideSoftKeyboard
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_LUMPERS_COUNT
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.activity_request_lumpers.*
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.*
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.textViewTitle
import kotlinx.android.synthetic.main.content_request_lumpers.*
import java.util.*
import kotlin.collections.ArrayList

class RequestLumpersActivity : BaseActivity(), View.OnClickListener,
    RequestLumpersContract.View, RequestLumpersContract.View.OnAdapterItemClickListener,
    TextWatcher {

    private var isPastDate = false
    private var selectedTime: Long = 0
    private var scheduledLumpersCount: Int = 0
    private var scheduledLumpersAssignedCount: Int = 0
    private var scheduledLumpersTotalCount: Int = 0
    private var dateString: String = ""
    private var timeInMillis: Long = 0
    private var startTime: Long = 0

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var records: ArrayList<RequestLumpersRecord> = ArrayList()

    private lateinit var requestLumpersPresenter: RequestLumpersPresenter
    private lateinit var requestLumpersAdapter: RequestLumpersAdapter

    companion object {
        const val LUMPER_REQUEST_LIST = "LUMPER_REQUEST_LIST"
        const val LUMPER_DATE_HEADER = "LUMPER_DATE_HEADER"
        const val LUMPER_COUNT = "LUMPER_COUNT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_lumpers)
        setupToolbar(getString(R.string.request_lumpers))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            scheduledLumpersCount = bundle.getInt(ARG_SCHEDULED_LUMPERS_COUNT, 0)

            isPastDate = !DateUtils.isFutureDate(selectedTime) && !DateUtils.isCurrentDate(selectedTime)
        }

        initializeUI()

        requestLumpersPresenter = RequestLumpersPresenter(this, resources)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_DATE_HEADER)) {
                dateString = savedInstanceState.getString(LUMPER_DATE_HEADER)!!
                scheduledLumpersCount = savedInstanceState.getInt(LUMPER_COUNT)!!
                showHeaderInfo(dateString)
            }
            if (savedInstanceState.containsKey(LUMPER_REQUEST_LIST)) {
                records = savedInstanceState.getParcelableArrayList(LUMPER_REQUEST_LIST)!!
                showAllRequests(records)
            }
        } ?: run {
            requestLumpersPresenter.fetchAllRequestsByDate(Date(selectedTime))
        }
        refreshData()
    }

    private fun refreshData() {
        swipe_pull_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            requestLumpersPresenter.fetchAllRequestsByDate(Date(selectedTime))
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (records != null)
            outState.putParcelableArrayList(LUMPER_REQUEST_LIST, records)
            outState.putString(LUMPER_DATE_HEADER, dateString)
            outState.putInt(LUMPER_COUNT, scheduledLumpersCount)

        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            closeBottomSheet()
        } else {
            super.onBackPressed()
        }
    }

    private fun initializeUI() {
        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetRequestLumpers)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        startTime=Date().time

        recyclerViewRequestLumpers.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            requestLumpersAdapter = RequestLumpersAdapter(resources, isPastDate, this@RequestLumpersActivity)
            adapter = requestLumpersAdapter
        }

        requestLumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (requestLumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        if (isPastDate) {
            textViewEmptyData.text = getString(R.string.empty_request_lumpers_list_info_message_past)
            buttonCreateNewRequest.visibility = View.GONE
        } else {
            textViewEmptyData.text = getString(R.string.empty_request_lumpers_list_info_message)
            buttonCreateNewRequest.visibility = View.VISIBLE
        }

        addNotesTouchListener(editTextDMNotes)

        buttonCreateNewRequest.setOnClickListener(this)
        bottomSheetBackgroundRequestLumpers.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
        buttonCancelRequest.setOnClickListener(this)
        buttonCancelNote.setOnClickListener(this)
        textViewStartTime.setOnClickListener(this)
//        editTextLumpersRequired.addTextChangedListener(this)
//        editTextDMNotes.addTextChangedListener(this)
    }

    private fun closeBottomSheet() {
        hideSoftKeyboard(this)
        constraintLayoutBottomSheetRequestLumpers.visibility=View.GONE
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundRequestLumpers.visibility = View.GONE
    }

    private fun showCancelRequestConfirmationDialog(requestLumperId: String?) {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.cancel_lumper_request_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                if (!requestLumperId.isNullOrEmpty()) {
                    requestLumpersPresenter.cancelRequestForLumpers(requestLumperId, Date(selectedTime))
                }
            }

            override fun onCancelClick() {
            }
        })
    }

    private fun showSubmitRequestConfirmationDialog(
        requiredLumperCount: String,
        notesDM: String,
        noteLumper: String
    ) {
        val requestLumperId = buttonSubmit.getTag(R.id.requestLumperId) as String?
        if (requestLumperId.isNullOrEmpty()) {
            CustomProgressBar.getInstance().showWarningDialog(getString(R.string.request_lumpers_alert_message), activity, object : CustomDialogWarningListener {
                    override fun onConfirmClick() {
                        closeBottomSheet()
                        requestLumpersPresenter.createNewRequestForLumpers(requiredLumperCount, notesDM, Date(selectedTime), noteLumper)

                    }
                    override fun onCancelClick() {}
                })
        } else {
            closeBottomSheet()
            requestLumpersPresenter.updateRequestForLumpers(requestLumperId, requiredLumperCount, notesDM, Date(selectedTime))
        }
    }

    private fun showBottomSheetWithData(record: RequestLumpersRecord? = null) {
        constraintLayoutBottomSheetRequestLumpers.visibility=View.VISIBLE
        record?.also {
            textViewTitle.text = getString(R.string.update_request)
            buttonSubmit.text = getString(R.string.update)
            val requestedLumpersCount = ValueUtils.getDefaultOrValue(record.requestedLumpersCount)
            editTextLumpersRequired.setText("$requestedLumpersCount")
            editTextDMNotes.setText(record.notesForDM)
            buttonSubmit.setTag(R.id.requestLumperId, record.id)
            editTextLumpersRequired.setTag(R.id.requirment, requestedLumpersCount)
            editTextDMNotes.setTag(R.id.note, record.notesForDM)
        } ?: run {
            textViewTitle.text = getString(R.string.create_new_request)
            buttonSubmit.text = getString(R.string.submit)
            editTextLumpersRequired.setText("")
            editTextDMNotes.setText("")
            editTextLumperNotes.setText("")
            textViewStartTime.text = DateUtils.convertMillisecondsToTimeString(startTime)
            buttonSubmit.setTag(R.id.requestLumperId, "")
            editTextLumpersRequired.setTag(R.id.requirment, "")
            editTextDMNotes.setTag(R.id.note, "")
            editTextLumperNotes.setTag(R.id.lumperNote, "")

        }

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackgroundRequestLumpers.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    private fun requestLumperAction() {
        val requiredLumperCount = editTextLumpersRequired.text.toString()
        val notesDM = editTextDMNotes.text.toString()
        val noteLumper = editTextLumperNotes.text.toString()
        when {
            requiredLumperCount.isEmpty() -> {
                CustomProgressBar.getInstance().showInfoDialog(getString(R.string.requirement),getString(R.string.request_lumper_number_message), activity)
            }
            notesDM.isEmpty() -> {
                CustomProgressBar.getInstance().showInfoDialog(getString(R.string.requirement),getString(R.string.request_lumper_DM_note_message), activity)
            }
            noteLumper.isEmpty() -> {
                CustomProgressBar.getInstance().showInfoDialog(getString(R.string.requirement),getString(R.string.request_lumper_lumper_note_message), activity)
            }
            requiredLumperCount.toInt()==0 -> {
                CustomProgressBar.getInstance().showMessageDialog(getString(R.string.request_valid_message), activity)
            }
            else -> {
                showSubmitRequestConfirmationDialog(requiredLumperCount, notesDM, noteLumper)
            }
        }
    }

    override fun afterTextChanged(text: Editable?) {
        if (text === editTextLumpersRequired.editableText) {
            var primaryRequest=editTextLumpersRequired.getTag(R.id.requirment)
            buttonSubmit.isEnabled = !primaryRequest.equals(text)

        } else if (text === editTextDMNotes.editableText) {
            var primaryNote=editTextDMNotes.getTag(R.id.note)
            buttonSubmit.isEnabled = !primaryNote.equals(text)

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackgroundRequestLumpers.id ->{
                    closeBottomSheet()
                }
                buttonCreateNewRequest.id -> {
                    showBottomSheetWithData()
                }
                buttonSubmit.id -> {
                   requestLumperAction()
                }
                buttonCancelRequest.id ->{ super.onBackPressed() }
                buttonCancelNote.id ->{
                    closeBottomSheet()
                }
                textViewStartTime.id ->{
                    editTime()
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showAllRequests(records: ArrayList<RequestLumpersRecord>) {
        this.records=records
        swipe_pull_refresh?.isRefreshing = false
        setTotalLumperRequest(records)
        requestLumpersAdapter.updateList(records)
    }

    private fun editTime() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTime

        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            activity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                textViewStartTime.text =
                    DateUtils.convertMillisecondsToTimeString(calendar.timeInMillis)
                startTime = calendar.timeInMillis
            }, mHour, mMinute, false
        ).show()
    }

    override fun showHeaderInfo(dateString: String)
    {
        this.dateString=dateString
        textViewDate.visibility=View.GONE
        textViewTotalCount.text = String.format(getString(R.string.total_lumpers_assigned_s), scheduledLumpersAssignedCount)
        textViewTotalRequestCount.text = String.format(getString(R.string.total_lumpers_requested), scheduledLumpersTotalCount)
    }

    override fun showSuccessDialog(message: String, date: Date) {
        CustomProgressBar.getInstance().showSuccessDialog(message, activity, object : CustomDialogListener {
            override fun onConfirmClick() {
                requestLumpersPresenter.fetchAllRequestsByDate(date)
            }
        })
    }

    private fun setTotalLumperRequest(records: ArrayList<RequestLumpersRecord>) {
        var totalRequestCount: Int = 0
        var assignedLumersRequestCount: Int = 0
        records.forEach {
            if (!it.requestStatus!!.equals(AppConstant.REQUEST_LUMPERS_STATUS_CANCELLED))
                totalRequestCount += it.requestedLumpersCount!!
            if (!it.lumpersAllocated.isNullOrEmpty())
                assignedLumersRequestCount += it.lumpersAllocated!!.size
        }
        scheduledLumpersTotalCount = totalRequestCount
        scheduledLumpersAssignedCount = assignedLumersRequestCount

        textViewTotalCount.text = String.format(getString(R.string.total_lumpers_assigned_s), scheduledLumpersAssignedCount)
        textViewTotalRequestCount.text = String.format(getString(R.string.total_lumpers_requested), scheduledLumpersTotalCount)
    }

    /** Adapter Listeners */
    override fun onNotesItemClick(notes: String?) {
        notes?.let {
            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), notes, activity)
        }
    }

    override fun onUpdateItemClick(record: RequestLumpersRecord) {
        showBottomSheetWithData(record)
    }

    override fun onCancelItemClick(record: RequestLumpersRecord) {
        showCancelRequestConfirmationDialog(record.id)
    }
}