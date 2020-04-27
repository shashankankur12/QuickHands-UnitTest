package com.quickhandslogistics.modified.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogWarningContract
import com.quickhandslogistics.modified.contracts.TimeClockAttendanceContract
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData
import com.quickhandslogistics.modified.presenters.TimeClockAttendancePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.schedule.TimeClockAttendanceAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.bottom_sheet_add_time.*
import kotlinx.android.synthetic.main.content_time_clock_attendance.*
import kotlinx.android.synthetic.main.fragment_time_clock_attendance.*

class TimeClockAttendanceFragment : BaseFragment(), View.OnClickListener, TextWatcher,
    TimeClockAttendanceContract.View, TimeClockAttendanceContract.View.OnAdapterItemClickListener {

    private lateinit var timeClockAttendancePresenter: TimeClockAttendancePresenter
    private lateinit var timeClockAttendanceAdapter: TimeClockAttendanceAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeClockAttendancePresenter = TimeClockAttendancePresenter(this, resources)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_clock_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()

        timeClockAttendancePresenter.fetchAttendanceList()
    }

    private fun initializeUI() {

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            timeClockAttendanceAdapter =
                TimeClockAttendanceAdapter(this@TimeClockAttendanceFragment)
            adapter = timeClockAttendanceAdapter
        }

        timeClockAttendanceAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                buttonSave.isEnabled = updatedData.size > 0

                textViewEmptyData.visibility =
                    if (timeClockAttendanceAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSave.setOnClickListener(this)

        buttonClockIn.setOnClickListener(this)
        buttonClockOut.setOnClickListener(this)
        buttonLunchIn.setOnClickListener(this)
        buttonLunchOut.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
    }

    /*
    * Native Views Listeners
    */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackground.id -> closeBottomSheet()
                buttonClockIn.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    timeClockAttendanceAdapter.updateClockInTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                buttonClockOut.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    timeClockAttendanceAdapter.updateClockOutTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                buttonLunchIn.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    timeClockAttendanceAdapter.updateLunchInTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                buttonLunchOut.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    timeClockAttendanceAdapter.updateLunchOutTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonSave.id -> {
                    val dialog = InfoWarningDialogFragment.newInstance(
                        getString(R.string.string_ask_to_save_attendance_details),
                        positiveButtonText = getString(R.string.string_yes),
                        negativeButtonText = getString(R.string.string_no),
                        onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
                            override fun onPositiveButtonClick() {
                                imageViewCancel.performClick()
                                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                                timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct())
                            }

                            override fun onNegativeButtonClick() {
                            }
                        })
                    dialog.show(childFragmentManager, InfoWarningDialogFragment::class.simpleName)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        text?.let {
            timeClockAttendanceAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeClockAttendancePresenter.onDestroy()
    }


    /*
    * Presenter Listeners
    */
    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>) {
        timeClockAttendanceAdapter.updateList(lumperAttendanceList)
        if (lumperAttendanceList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun showDataSavedMessage() {
        timeClockAttendancePresenter.fetchAttendanceList()
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
            bottomSheetBackground.setTag(R.id.attendancePosition, itemPosition)

            // Show Clock-In Time
            val clockInTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.morningPunchIn
            )
            buttonClockIn.text =
                if (clockInTime.isNotEmpty()) clockInTime else getString(R.string.clock_in)
            buttonClockIn.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                clockInTime.isNotEmpty(), "isMorningPunchIn", lumperAttendanceData.id!!
            )

            // Show Clock-Out Time
            val clockOutTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.eveningPunchOut
            )
            buttonClockOut.text =
                if (clockOutTime.isNotEmpty()) clockOutTime else getString(R.string.clock_out)
            buttonClockOut.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                clockOutTime.isNotEmpty(), "isEveningPunchOut", lumperAttendanceData.id!!
            )

            // Show Lunch-In Time
            val lunchInTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.lunchPunchIn
            )
            buttonLunchIn.text =
                if (lunchInTime.isNotEmpty()) lunchInTime else getString(R.string.out_to_lunch)
            buttonLunchIn.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                lunchInTime.isNotEmpty(), "isLunchPunchIn", lumperAttendanceData.id!!
            )

            // Show Lunch-Out Time
            val lunchOutTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.lunchPunchOut
            )
            buttonLunchOut.text =
                if (lunchOutTime.isNotEmpty()) lunchOutTime else getString(R.string.back_to_work)
            buttonLunchOut.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                lunchOutTime.isNotEmpty(), "isLunchPunchOut", lumperAttendanceData.id!!
            )
        } else {
            closeBottomSheet()
        }
    }

    override fun onAddNotes(updatedDataSize: Int) {
        buttonSave.isEnabled = updatedDataSize > 0
    }
}
