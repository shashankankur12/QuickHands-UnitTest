package com.quickhandslogistics.views.scheduleTime

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.scheduleTime.RequestLumpersAdapter
import com.quickhandslogistics.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.presenters.scheduleTime.RequestLumpersPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_LUMPERS_COUNT
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.activity_request_lumpers.*
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.*
import kotlinx.android.synthetic.main.content_request_lumpers.*
import java.util.*

class RequestLumpersActivity : BaseActivity(), View.OnClickListener,
    RequestLumpersContract.View, RequestLumpersContract.View.OnAdapterItemClickListener {

    private var isFutureDate = false
    private var selectedTime: Long = 0
    private var scheduledLumpersCount: Int = 0

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var requestLumpersPresenter: RequestLumpersPresenter
    private lateinit var requestLumpersAdapter: RequestLumpersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_lumpers)
        setupToolbar(getString(R.string.request_lumpers))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            scheduledLumpersCount = bundle.getInt(ARG_SCHEDULED_LUMPERS_COUNT, 0)

            isFutureDate = DateUtils.isFutureDate(selectedTime)
        }

        initializeUI()

        requestLumpersPresenter = RequestLumpersPresenter(this, resources)
        requestLumpersPresenter.fetchAllRequestsByDate(Date(selectedTime))
    }

    private fun initializeUI() {
        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetRequestLumpers)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerViewRequestLumpers.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            requestLumpersAdapter = RequestLumpersAdapter(resources, isFutureDate, this@RequestLumpersActivity)
            adapter = requestLumpersAdapter
        }

        requestLumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (requestLumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        if (isFutureDate) {
            textViewEmptyData.text = getString(R.string.empty_request_lumpers_list_info_message)
            buttonCreateNewRequest.visibility = View.VISIBLE
        } else {
            textViewEmptyData.text = getString(R.string.empty_request_lumpers_list_info_message_past)
            buttonCreateNewRequest.visibility = View.GONE
        }

        buttonCreateNewRequest.setOnClickListener(this)
        bottomSheetBackgroundRequestLumpers.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundRequestLumpers.visibility = View.GONE
    }

    private fun showConfirmationDialog(requiredLumperCount: String, notesDM: String) {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                closeBottomSheet()
                val requestLumperId = buttonSubmit.getTag(R.id.requestLumperId) as String?
                if (requestLumperId.isNullOrEmpty()) {
                    requestLumpersPresenter.createNewRequestForLumpers(requiredLumperCount, notesDM, Date(selectedTime))
                } else {
                    requestLumpersPresenter.updateRequestForLumpers(requestLumperId, requiredLumperCount, notesDM, Date(selectedTime))
                }
            }

            override fun onCancelClick() {
            }
        })
    }

    private fun showBottomSheetWithData(record: RequestLumpersRecord? = null) {
        record?.also {
            textViewTitle.text = getString(R.string.update_request)
            val requestedLumpersCount = ValueUtils.getDefaultOrValue(record.requestedLumpersCount)
            editTextLumpersRequired.setText("$requestedLumpersCount")
            editTextDMNotes.setText(record.notesForDM)
            buttonSubmit.setTag(R.id.requestLumperId, record.id)
        } ?: run {
            textViewTitle.text = getString(R.string.create_new_request)
            editTextLumpersRequired.setText("")
            editTextDMNotes.setText("")
            buttonSubmit.setTag(R.id.requestLumperId, "")
        }

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackgroundRequestLumpers.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackgroundRequestLumpers.id -> closeBottomSheet()
                buttonCreateNewRequest.id -> {
                    showBottomSheetWithData()
                }
                buttonSubmit.id -> {
                    val requiredLumperCount = editTextLumpersRequired.text.toString()
                    val notesDM = editTextDMNotes.text.toString()
                    if ((requiredLumperCount.isNotEmpty() && notesDM.isEmpty()) || (requiredLumperCount.isEmpty() && notesDM.isNotEmpty())) {
                        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, getString(R.string.request_help_message))
                    } else {
                        showConfirmationDialog(requiredLumperCount, notesDM)
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showAllRequests(records: List<RequestLumpersRecord>) {
        requestLumpersAdapter.updateList(records)
    }

    override fun showHeaderInfo(dateString: String) {
        textViewDate.text = dateString
        textViewTotalCount.text = String.format(getString(R.string.total_lumpers_assigned_s), scheduledLumpersCount)
    }

    override fun showSuccessDialog(date: Date) {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.request_placed_success_message),
            activity, object : CustomDialogListener {
                override fun onConfirmClick() {
                    requestLumpersPresenter.fetchAllRequestsByDate(date)
                }
            })
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
}