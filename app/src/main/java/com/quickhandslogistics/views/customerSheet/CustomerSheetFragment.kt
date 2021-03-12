package com.quickhandslogistics.views.customerSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.WorkItemAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.customerSheet.CustomerSheetScheduleDetails
import com.quickhandslogistics.data.customerSheet.LocalCustomerSheetData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.customerSheet.CustomerSheetPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_DATE_DISPLAY_CUSTOMER_SHEET
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_DATE_DISPLAY_SHEET
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.common.AddSignatureActivity
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.*
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.buttonSubmit
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.editTextCustomerName
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.editTextCustomerNotes
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.imageViewSignature
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.layoutSignature
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.textViewAddSignature
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.textViewSignature
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.customer_sheet_content.*
import kotlinx.android.synthetic.main.customer_sheet_content.textViewBuildingName
import kotlinx.android.synthetic.main.customer_sheet_contner.*
import kotlinx.android.synthetic.main.fragment_customer_sheet.bottomSheetBackground
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.customer_sheet_contner.textViewWorkItemsDate as textViewWorkItemsDate1


class CustomerSheetFragment : BaseFragment(), CustomerSheetContract.View,
    CustomerSheetContract.View.OnFragmentInteractionListener, CustomerSheetContract.View.fragmentDataListener,
    View.OnClickListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? =
        null

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>
    private var customerSheetScheduleDetails: CustomerSheetScheduleDetails? = null
    private var customerSheetData: CustomerSheetData? = null
    private var selectedDate: Date = Date()
    private var companyName: String = ""
    private var date: String = ""
    private var signatureFilePath = ""
    private var localCustomerSheetData: LocalCustomerSheetData? = null

    private var isSavedState: Boolean = false
    private var isSaveddata: Boolean = true
    private var selectedDatePosition: Int = 0
    private var inCompleteWorkItemsCount: Int = 0
    private var workItemsCount: Int = 0


    private lateinit var customerSheetPresenter: CustomerSheetPresenter
//    private lateinit var adapter: CustomerSheetPagerAdapter
    private lateinit var workItemAdapter: WorkItemAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    companion object {
        const val DATE = "DATE"
        const val CUSTOMER_SHEET = "CUSTOMER_SHEET"
        const val SCHEDULE_DETAIL = "SCHEDULE_DETAIL"
        const val DATE_STRING = "DATE_STRING"
        const val COMPANY_NAME = "COMPANY_NAME"
        const val SELECTED_DATE_POSITION = "SELECTED_DATE_POSITION"
        const val LOCAL_CUSTOMER_SHEET_DATA = "LOCAL_CUSTOMER_SHEET_DATA"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
            isSaveddata=true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerSheetPresenter = CustomerSheetPresenter(this, resources, sharedPref)

        // Setup Calendar Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewWorkItemsDate1.text= DateUtils.getDateString(PATTERN_NORMAL, Date(selectedTime))
        initializeViewPager()

        savedInstanceState?.also {
            isSavedState = true
            if (savedInstanceState.containsKey(SELECTED_DATE_POSITION)) {
                selectedDatePosition = savedInstanceState.getInt(SELECTED_DATE_POSITION)
            }

            if (savedInstanceState.containsKey(COMPANY_NAME) && savedInstanceState.containsKey(
                    DATE_STRING
                )
            ) {
                companyName = savedInstanceState.getString(COMPANY_NAME)!!
                date = savedInstanceState.getString(DATE_STRING)!!

                showHeaderInfo(companyName, date)
            }

            if (savedInstanceState.containsKey(LOCAL_CUSTOMER_SHEET_DATA)) {
                localCustomerSheetData = savedInstanceState.getParcelable(LOCAL_CUSTOMER_SHEET_DATA)
            }

            if (savedInstanceState.containsKey(SCHEDULE_DETAIL) && savedInstanceState.containsKey(
                    DATE
                ) && savedInstanceState.containsKey(CUSTOMER_SHEET)
            ) {
                customerSheetScheduleDetails = savedInstanceState.getParcelable(SCHEDULE_DETAIL)
                selectedDate = savedInstanceState.getSerializable(DATE) as Date
                customerSheetData = savedInstanceState.getParcelable(CUSTOMER_SHEET)

                selectedTime = selectedDate.time
                textViewWorkItemsDate1.text= DateUtils.getDateString(PATTERN_NORMAL, Date(selectedTime))
                showCustomerSheets(customerSheetScheduleDetails!!,customerSheetData,selectedDate)

            }
        } ?: run {
            customerSheetPresenter.getCustomerSheetByDate(Date(selectedTime))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customerSheetPresenter.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        if (selectedDate != null && customerSheetScheduleDetails != null) {
            outState.putSerializable(DATE, selectedDate)
            outState.putParcelable(CUSTOMER_SHEET, customerSheetData)
            outState.putParcelable(SCHEDULE_DETAIL, customerSheetScheduleDetails)
        }
        outState.putSerializable(COMPANY_NAME, companyName)
        outState.putSerializable(DATE_STRING, date)
        outState.putInt(SELECTED_DATE_POSITION, selectedDatePosition)
        outState.putParcelable(LOCAL_CUSTOMER_SHEET_DATA, localCustomerSheetData)

        super.onSaveInstanceState(outState)
    }

    private fun initializeViewPager(
        allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null,
        customerSheetData: CustomerSheetData? = null,
        selectedTime: Long? = null,
        localCustomerSheetData: LocalCustomerSheetData? = null
    ) {

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetCustomerDetails)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        buttonSignature.setOnClickListener (this)
        bottomSheetBackground.setOnClickListener (this)
        buttonCancel.setOnClickListener (this)
        textViewAddSignature.setOnClickListener (this)
        buttonSubmit.setOnClickListener (this)
        textViewWorkItemsDate1.setOnClickListener (this)
        workItemAdapter= WorkItemAdapter(resources, this)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycelrViewItem.layoutManager=layoutManager
        recycelrViewItem.adapter=workItemAdapter



//        adapter = if (allWorkItemLists != null) {
//            CustomerSheetPagerAdapter(
//                childFragmentManager,
//                resources,
//                allWorkItemLists,
//                customerSheetData,
//                selectedTime,
//                localCustomerSheetData
//            )
//        } else {
//            CustomerSheetPagerAdapter(childFragmentManager, resources)
//        }
//        viewPagerCustomerSheet.offscreenPageLimit = adapter.count
//        viewPagerCustomerSheet.adapter = adapter
//        tabLayoutCustomerSheet.setupWithViewPager(viewPagerCustomerSheet)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.let {
                val signatureFilePath = data.getStringExtra(AddSignatureActivity.ARG_SIGNATURE_FILE_PATH)
                showLocalSignatureOnUI(signatureFilePath)
            }
        }
    }

    private fun createDifferentListData(scheduleDetails: CustomerSheetScheduleDetails): Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>> {
        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(scheduleDetails.inProgress!!)
        onGoingWorkItems.addAll(scheduleDetails.onHold!!)
        onGoingWorkItems.addAll(scheduleDetails.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(scheduleDetails.cancelled!!)
        allWorkItems.addAll(scheduleDetails.completed!!)
        textViewTotalCount.text =
            String.format(getString(R.string.total_containers_s), allWorkItems.size)

        return Triple(onGoingWorkItems, scheduleDetails.cancelled!!, scheduleDetails.completed!!)
    }

    override fun showAPIErrorMessage(message: String) {
        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, fragmentActivity!!)
        } else SnackBarFactory.createSnackBar(fragmentActivity!!, frameLayoutMain, message)

        // Reset Whole Screen Data
        textViewCompanyName.text = ""
//        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
        textViewBuildingName.text=""
        buttonSignature.isEnabled=false
    }

    override fun showCustomerSheets(scheduleDetails: CustomerSheetScheduleDetails, customerSheet: CustomerSheetData?, selectedDate: Date) {
        this.selectedDate = selectedDate
        this.customerSheetScheduleDetails = scheduleDetails
        this.customerSheetData = customerSheet
        this.localCustomerSheetData = null

        selectedTime = selectedDate.time

        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(scheduleDetails.inProgress!!)
        onGoingWorkItems.addAll(scheduleDetails.onHold!!)
        onGoingWorkItems.addAll(scheduleDetails.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(scheduleDetails.cancelled!!)
        allWorkItems.addAll(scheduleDetails.completed!!)
        textViewTotalCount.text =
            String.format(getString(R.string.total_containers_s), allWorkItems.size)


//        adapter.updateCustomerSheetList(
//            onGoingWorkItems,
//            scheduleDetails.cancelled!!,
//            scheduleDetails.completed!!,
//            customerSheet,
//            selectedTime
//        )

        workItemsCount= allWorkItems.size

        inCompleteWorkItemsCount=onGoingWorkItems.size
        buildingDetails(scheduleDetails,customerSheet)
    }


    private fun buildingDetails(scheduleDetails: CustomerSheetScheduleDetails, customerSheet: CustomerSheetData?) {

        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        leadProfile?.buildingDetailData?.let {
            textViewBuildingName.text=  it.buildingName!!.capitalize()+ " \n"+it.buildingAddress!!.capitalize() +", \n"+it.buildingCity+", "+it.buildingState +" "+it .buildingZipcode
        }
        textViewHeaderBar.text= UIUtils.getSpannableText(getString(R.string.date),DateUtils.getDateString(PATTERN_DATE_DISPLAY_CUSTOMER_SHEET, selectedDate))
        textViewShiftName.text= UIUtils.getSpannableText(getString(R.string.bar_header_shift), leadProfile?.shift!!.capitalize())
        textViewDepartmentName.text= UIUtils.getSpannableText(getString(R.string.bar_header_dept), leadProfile?.department!!.capitalize())
        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(scheduleDetails.inProgress!!)
        onGoingWorkItems.addAll(scheduleDetails.onHold!!)
        onGoingWorkItems.addAll(scheduleDetails.scheduled!!)

        val workItemDetailList =ArrayList<ArrayList<WorkItemDetail>>()
        workItemDetailList.add(scheduleDetails.completed!!)
        workItemDetailList.add(scheduleDetails.cancelled!!)
        workItemDetailList.add(onGoingWorkItems)

        workItemAdapter.update(workItemDetailList)

        customerSheet?.also {
            layoutBarCustomerDetail.visibility=View.VISIBLE

            textViewCustomerName.text = customerSheet.customerRepresentativeName
            if (!customerSheet.note.isNullOrEmpty()&& !customerSheet.note.equals("NA")){
                textViewCustomerNote.text = customerSheet.note
                layoutBarCustomerNote.visibility=View.VISIBLE
            }
            else{
                layoutBarCustomerNote.visibility=View.GONE
            }
            if (customerSheet.isSigned!!){
                Glide.with(fragmentActivity!!).load(customerSheet.signatureUrl).into(imageViewCustomerSignature)
                imageViewCustomerSignature.visibility=View.VISIBLE
                buttonSignature.background = resources.getDrawable(R.drawable.round_button_red_disabled)
                buttonSignature.text=getString(R.string.signed)
            }else{
                imageViewCustomerSignature.visibility=View.GONE
                buttonSignature.background = resources.getDrawable(R.drawable.round_button_red)
                buttonSignature.text=getString(R.string.signature)
            }

        } ?: run {
            layoutBarCustomerDetail.visibility=View.GONE
            buttonSignature.text=getString(R.string.signature)
            buttonSignature.background = resources.getDrawable(R.drawable.round_button_red)
        }

    }


    override fun showHeaderInfo(companyName: String, date: String) {
        this.companyName = companyName
        this.date = date

        textViewCompanyName.text = companyName.capitalize()
//        textViewWorkItemsDate.text = UIUtils.getSpannedText(date)
    }

    override fun customerSavedSuccessfully() {
        LocalNotificationUtils.showTimeClockNotification(fragmentActivity!!)

        CustomProgressBar.getInstance()
            .showSuccessDialog(getString(R.string.customer_sheet_success_message),
                fragmentActivity!!, object : CustomDialogListener {
                    override fun onConfirmClick() {
                        customerSheetPresenter.getCustomerSheetByDate(Date(selectedTime))
                    }
                })
    }

    override fun showLoginScreen() {
        startIntent(
            LoginActivity::class.java,
            isFinish = true,
            flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    /** Fragment Interaction Listeners */
    override fun saveCustomerSheet(
        customerName: String,
        notesCustomer: String,
        signatureFilePath: String,
        customerId: String
    ) {
        customerSheetPresenter.saveCustomerSheet(
            customerName,
            notesCustomer,
            signatureFilePath,
            customerId
        )
    }

    override fun saveSateCustomerSheet(
        customerName: String,
        notesCustomer: String,
        signatureFilePath: String
    ) {
        if (localCustomerSheetData == null) {
            localCustomerSheetData = LocalCustomerSheetData()
        }
        localCustomerSheetData?.customerRepresentativeName = customerName
        localCustomerSheetData?.note = notesCustomer
        localCustomerSheetData?.signatureFilePath = signatureFilePath
    }

    override fun isDataSave(isDataSave: Boolean) {
        isSaveddata = isDataSave
    }

    override fun onDataChanges(): Boolean {
        var isDataChanged = false
        if (customerSheetData != null && customerSheetData!!.isSigned!!.equals(true)) {
            isDataChanged = false
        } else {
            if ( !isSaveddata) {
                isDataChanged = true
            } else false
        }

        return isDataChanged
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSignature.id -> {
                    showBottomSheetWithData(customerSheetData)
                }
                bottomSheetBackground.id-> closeBottomSheet()
                buttonCancel.id-> closeBottomSheet()
                textViewAddSignature.id -> startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                buttonSubmit.id->submitCustomerSheet()
                textViewWorkItemsDate1.id->{
                    showStartDatePicker()
                }
            }
        }

    }

    private fun showStartDatePicker() {
        val selectedEndDate=availableDates.get(0)
        ReportUtils.showDatePicker(Date(selectedTime),selectedEndDate, fragmentActivity!!, object : ReportUtils.OnDateSetListener {
            override fun onDateSet(selected: Date) {
                updateSelectedDateText(selected.time)
            }
        })
    }

    private fun updateSelectedDateText(selectedTime: Long) {
        selectedTime?.also { date ->
            textViewWorkItemsDate1.text= DateUtils.getDateString(PATTERN_NORMAL, Date(selectedTime))
                customerSheetPresenter.getCustomerSheetByDate(Date(selectedTime))

        }

    }


    private fun showBottomSheetWithData(customerSheet: CustomerSheetData?) {
        constraintLayoutBottomSheetCustomerDetails.visibility=View.VISIBLE
        val isCurrentDate = DateUtils.isCurrentDate(selectedTime)
        customerSheet?.also {
            editTextCustomerName.setText(customerSheet.customerRepresentativeName)
            editTextCustomerNotes.setText(customerSheet.note)
            buttonSubmit.setTag(R.id.requestLumperId, customerSheet.id)
            updateUIVisibility(ValueUtils.getDefaultOrValue(customerSheet.isSigned), isCurrentDate, inCompleteWorkItemsCount, customerSheet.signatureUrl)
        } ?: run {
            editTextCustomerName.setText("")
            editTextCustomerNotes.setText("")
            signatureFilePath=""
            buttonSubmit.setTag(R.id.requestLumperId, "")
            updateUIVisibility(false, isCurrentDate, inCompleteWorkItemsCount)
        }

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    private fun closeBottomSheet() {
        AppUtils.hideSoftKeyboard(activity!!)
        constraintLayoutBottomSheetCustomerDetails.visibility=View.GONE
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
    }


    private fun updateUIVisibility(signed: Boolean, currentDate: Boolean, inCompleteWorkItemsCount: Int, signatureUrl: String? = "") {
        buttonSubmit.visibility = if (currentDate) View.VISIBLE else View.GONE
        textViewSignature.visibility = View.GONE

        if (signed) {
            imageViewSignature.visibility = View.VISIBLE
            Glide.with(fragmentActivity!!).load(signatureUrl).into(imageViewSignature)
        } else {
            imageViewSignature.visibility = View.GONE
            Glide.with(fragmentActivity!!).clear(imageViewSignature)
        }

        if (!currentDate || signed /*|| inCompleteWorkItemsCount > 0*/) {
            editTextCustomerName.isEnabled = false
            editTextCustomerNotes.isEnabled = false
            buttonSubmit.isEnabled = false
            if (signed) {
                buttonSubmit.background = resources.getDrawable(R.drawable.round_button_blue)
                buttonSubmit.text = getText(R.string.submitted)
            } else {
                buttonSubmit.text = getText(R.string.submit)
            }
        } else {
            editTextCustomerName.isEnabled = true
            editTextCustomerNotes.isEnabled = true
            buttonSubmit.isEnabled = true
        }

        if (!signed && currentDate && inCompleteWorkItemsCount == 0) {
            textViewAddSignature.visibility = View.VISIBLE
        } else {
            textViewAddSignature.visibility = View.GONE
        }

        if (signed || (currentDate && inCompleteWorkItemsCount == 0 && workItemsCount != 0)) {
            layoutSignature.visibility = View.VISIBLE
        } else {
            layoutSignature.visibility = View.GONE
        }
    }

    private fun showLocalSignatureOnUI(signatureFilePath: String?) {
        if (!signatureFilePath.isNullOrEmpty()) {
            this.signatureFilePath = signatureFilePath
            Glide.with(fragmentActivity!!).load(File(signatureFilePath)).into(imageViewSignature)
            imageViewSignature.visibility = View.VISIBLE
            textViewAddSignature.visibility = View.GONE
        } else {
            this.signatureFilePath = ""
            imageViewSignature.visibility = View.GONE
            textViewAddSignature.visibility = View.VISIBLE
        }
    }

    private fun submitCustomerSheet() {
        val customerName = editTextCustomerName.text.toString()
        val notesCustomer = editTextCustomerNotes.text.toString()
        if (customerName.isNotEmpty()  && (signatureFilePath.isNotEmpty()|| inCompleteWorkItemsCount > 0)) {
            var message = getString(R.string.submit_customer_sheet_alert_message)
            if (notesCustomer.isEmpty()) {
                message = getString(R.string.submit_customer_sheet_permanently_alert_message)
            }
            showConfirmationDialog(message, customerName, notesCustomer)
        } else {
            CustomProgressBar.getInstance().showErrorDialog(getString(R.string.customer_sheet_warning_message), fragmentActivity!!)
        }
    }

    private fun showConfirmationDialog(message: String, customerName: String, notesCustomer: String) {
//        CustomProgressBar.getInstance().showWarningDialog(message, fragmentActivity!!, object : CustomDialogWarningListener {
//            override fun onConfirmClick() {
                closeBottomSheet()
                val customerId = buttonSubmit.getTag(R.id.requestLumperId) as String?
                saveCustomerSheet(customerName, notesCustomer, signatureFilePath, customerId!!)
//            }
//
//            override fun onCancelClick() {
//            }
//        })
    }

    private fun disableSignature(){
        textViewBuildingName.text=""
    }
}
