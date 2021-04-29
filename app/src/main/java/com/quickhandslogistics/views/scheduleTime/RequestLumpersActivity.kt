package com.quickhandslogistics.views.scheduleTime

import android.app.Dialog
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
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.content_request_lumpers.*
import kotlinx.android.synthetic.main.custome_toolbar_layout.*
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
        setUpToolBarLayout()
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
                scheduledLumpersCount = savedInstanceState.getInt(LUMPER_COUNT)
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

    private fun setUpToolBarLayout() {
        textViewToolbar.setText(R.string.request_lumpers)
        textViewDate.visibility=View.VISIBLE
        textViewDate.text = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, Date())
        headerBackImage.setOnClickListener(this)
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
        super.onBackPressed()
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

    private fun showSubmitRequestConfirmationDialog(requiredLumperCount: String, notesDM: String, noteLumper: String, startTime: Long, requestLumperId:String) {
        if (requestLumperId.isNullOrEmpty()) {
            CustomProgressBar.getInstance().showWarningDialog(getString(R.string.request_lumpers_alert_message), activity, object : CustomDialogWarningListener {
                    override fun onConfirmClick() {
                        requestLumpersPresenter.createNewRequestForLumpers(requiredLumperCount, notesDM, Date(startTime), noteLumper, startTime.toString())

                    }
                    override fun onCancelClick() {}
                })
        } else {
            requestLumpersPresenter.updateRequestForLumpers(requestLumperId, requiredLumperCount, notesDM, Date(startTime), noteLumper, startTime.toString())
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
                buttonCreateNewRequest.id -> {
                    bottomSheetRequestDialog()
                }
                buttonCancelRequest.id ->{ super.onBackPressed() }
                headerBackImage.id->{
                    onBackPressed()
                }
            }
        }
    }

    private fun bottomSheetRequestDialog() {
        CustomBottomSheetDialog.createUpdateLumperRequest(
            activity,
            null, object : CustomBottomSheetDialog.IDialogOnLumperRequestClick {
                override fun onSendLumperRequest(dialog: Dialog, requiredLumper: String, noteForDm: String, noteForLumper: String, startTime: Long, lumperId: String) {
                    dialog.dismiss()
                    showSubmitRequestConfirmationDialog(requiredLumper, noteForDm, noteForLumper, startTime, lumperId)
                }

            })
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, activity!!)
        } else SnackBarFactory.createSnackBar(activity!!, mainConstraintLayout, message)
    }

    override fun showAllRequests(records: ArrayList<RequestLumpersRecord>) {
        this.records=records
        swipe_pull_refresh?.isRefreshing = false
        setTotalLumperRequest(records)
        requestLumpersAdapter.updateList(records)
    }

    override fun showHeaderInfo(dateString: String)
    {
        this.dateString=dateString
        textViewDateContainer.visibility=View.GONE
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
//            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), notes, activity)
            CustomerDialog.showNoteDialog(activity,getString(R.string.note),notes)
        }
    }

    override fun onUpdateItemClick(record: RequestLumpersRecord) {
        CustomBottomSheetDialog.createUpdateLumperRequest(
            activity,
            record, object : CustomBottomSheetDialog.IDialogOnLumperRequestClick {
                override fun onSendLumperRequest(dialog: Dialog, requiredLumper: String, noteForDm: String, noteForLumper: String, startTime: Long, lumperId: String) {
                    dialog.dismiss()
                    showSubmitRequestConfirmationDialog(requiredLumper, noteForDm, noteForLumper, startTime, lumperId)
                }

            })
    }

    override fun onCancelItemClick(record: RequestLumpersRecord) {
        showCancelRequestConfirmationDialog(record.id)
    }
}