package com.quickhandslogistics.modified.views.scheduleTime

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.scheduleTime.EditScheduleTimeAdapter
import com.quickhandslogistics.modified.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.scheduleTime.RequestLumpersPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_request_lumpers.*
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.*
import kotlinx.android.synthetic.main.content_request_lumpers.*
import java.util.*

class RequestLumpersActivity : BaseActivity(), View.OnClickListener,
    RequestLumpersContract.View, RequestLumpersContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var requestLumpersPresenter: RequestLumpersPresenter
    private lateinit var editScheduleTimeAdapter: EditScheduleTimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_lumpers)
        setupToolbar(getString(R.string.request_help))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
        }

        initializeUI()

        requestLumpersPresenter = RequestLumpersPresenter(this, resources)
        requestLumpersPresenter.fetchAllRequestsByDate(Date(selectedTime))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            data?.extras?.let { bundle ->
                val employeeDataList = bundle.getParcelableArrayList<EmployeeData>(ARG_LUMPERS_LIST)
                employeeDataList?.let {
                    editScheduleTimeAdapter.addLumpersList(employeeDataList)
                }
            }
        }
    }

    private fun initializeUI() {
        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetRequestLumpers)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerViewRequestLumpers.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
/*            editScheduleTimeAdapter = EditScheduleTimeAdapter(scheduleTimeList, this@RequestLumpersActivity)
            adapter = editScheduleTimeAdapter*/
        }

        editScheduleTimeAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (editScheduleTimeAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        buttonCreateNewRequest.setOnClickListener(this)
        bottomSheetBackgroundRequestLumpers.setOnClickListener(this)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundRequestLumpers.visibility = View.GONE
    }

    private fun showBottomSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            //workSheetItemStatusAdapter.updateInitialStatus(textViewStatus.text.toString())
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackgroundRequestLumpers.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    private fun showConfirmationDialog(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumperCount: String, notesDM: String) {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                requestLumpersPresenter.initiateScheduleTime(
                    scheduledLumpersIdsTimeMap, notes, if (requiredLumperCount.isNotEmpty()) requiredLumperCount.toInt() else 0,
                    notesDM, Date(selectedTime)
                )
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackgroundRequestLumpers.id -> closeBottomSheet()
                buttonCreateNewRequest.id -> showBottomSheet()
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showAllRequests() {

    }

    /** Adapter Listeners */
    override fun onUpdateClick(adapterPosition: Int) {

    }
}