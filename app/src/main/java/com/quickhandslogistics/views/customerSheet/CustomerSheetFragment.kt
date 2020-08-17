package com.quickhandslogistics.views.customerSheet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.CustomerSheetPagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.customerSheet.CustomerSheetScheduleDetails
import com.quickhandslogistics.data.customerSheet.LocalCustomerSheetData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.customerSheet.CustomerSheetPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.fragment_customer_sheet.*
import java.util.*
import kotlin.collections.ArrayList


class CustomerSheetFragment : BaseFragment(), CustomerSheetContract.View,
    CustomerSheetContract.View.OnFragmentInteractionListener,
    CalendarUtils.CalendarSelectionListener, CustomerSheetContract.View.fragmentDataListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? =
        null

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>
    private var customerSheetScheduleDetails: CustomerSheetScheduleDetails? = null
    private var customerSheetData: CustomerSheetData? = null
    private var selectedDate: Date = Date()
    private var companyName: String = ""
    private var date: String = ""
    private var localCustomerSheetData: LocalCustomerSheetData? = null

    private var isSavedState: Boolean = false
    private var isSaveddata: Boolean = true
    private var selectedDatePosition: Int = 0

    private lateinit var customerSheetPresenter: CustomerSheetPresenter
    private lateinit var adapter: CustomerSheetPagerAdapter

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

        CalendarUtils.initializeCalendarView(
            fragmentActivity!!,
            singleRowCalendarCustomerSheet,
            availableDates,
            this
        )
        savedInstanceState?.also {
            isSavedState = true
            if (savedInstanceState.containsKey(SELECTED_DATE_POSITION)) {
                selectedDatePosition = savedInstanceState.getInt(SELECTED_DATE_POSITION)
                singleRowCalendarCustomerSheet.select(selectedDatePosition)
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

                val allWorkItemLists = createDifferentListData(customerSheetScheduleDetails!!)
                initializeViewPager(
                    allWorkItemLists,
                    customerSheetData,
                    selectedTime,
                    localCustomerSheetData
                )
            }
        } ?: run {
            initializeViewPager()
            singleRowCalendarCustomerSheet.select(availableDates.size - 1)
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
        adapter = if (allWorkItemLists != null) {
            CustomerSheetPagerAdapter(
                childFragmentManager,
                resources,
                allWorkItemLists,
                customerSheetData,
                selectedTime,
                localCustomerSheetData
            )
        } else {
            CustomerSheetPagerAdapter(childFragmentManager, resources)
        }
        viewPagerCustomerSheet.offscreenPageLimit = adapter.count
        viewPagerCustomerSheet.adapter = adapter
        tabLayoutCustomerSheet.setupWithViewPager(viewPagerCustomerSheet)
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
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        // Reset Whole Screen Data
        textViewCompanyName.text = ""
        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
    }

    override fun showCustomerSheets(
        scheduleDetails: CustomerSheetScheduleDetails,
        customerSheet: CustomerSheetData?,
        selectedDate: Date
    ) {
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

        adapter.updateCustomerSheetList(
            onGoingWorkItems,
            scheduleDetails.cancelled!!,
            scheduleDetails.completed!!,
            customerSheet,
            selectedTime
        )
    }

    override fun showHeaderInfo(companyName: String, date: String) {
        this.companyName = companyName
        this.date = date

        textViewCompanyName.text = companyName.capitalize()
        textViewWorkItemsDate.text = UIUtils.getSpannedText(date)
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

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date, selected: Boolean, position: Int) {
        if (!isSavedState)
            customerSheetPresenter.getCustomerSheetByDate(date)
        isSavedState = false
        selectedDatePosition = position
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
}
