package com.quickhandslogistics.views.customerSheet

import android.content.Context
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
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.customerSheet.CustomerSheetPresenter
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.utils.CalendarUtils
import com.quickhandslogistics.utils.CustomDialogListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_customer_sheet.*
import java.util.*

class CustomerSheetFragment : BaseFragment(), CustomerSheetContract.View, CustomerSheetContract.View.OnFragmentInteractionListener, CalendarUtils.CalendarSelectionListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>

    private lateinit var customerSheetPresenter: CustomerSheetPresenter
    private lateinit var adapter: CustomerSheetPagerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerSheetPresenter = CustomerSheetPresenter(this, resources, sharedPref)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CustomerSheetPagerAdapter(childFragmentManager, resources)
        viewPagerCustomerSheet.offscreenPageLimit = adapter.count
        viewPagerCustomerSheet.adapter = adapter
        tabLayoutCustomerSheet.setupWithViewPager(viewPagerCustomerSheet)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarCustomerSheet, availableDates, this)
        singleRowCalendarCustomerSheet.select(availableDates.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        customerSheetPresenter.onDestroy()
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        // Reset Whole Screen Data
        textViewCompanyName.text = ""
        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
    }

    override fun showCustomerSheets(scheduleDetails: CustomerSheetScheduleDetails, customerSheet: CustomerSheetData?, selectedDate: Date) {
        selectedTime = selectedDate.time

        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(scheduleDetails.inProgress!!)
        onGoingWorkItems.addAll(scheduleDetails.onHold!!)
        onGoingWorkItems.addAll(scheduleDetails.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(scheduleDetails.cancelled!!)
        allWorkItems.addAll(scheduleDetails.completed!!)
        textViewTotalCount.text = String.format(getString(R.string.total_containers_s), allWorkItems.size)

        adapter.updateCustomerSheetList(onGoingWorkItems, scheduleDetails.cancelled!!, scheduleDetails.completed!!, customerSheet, selectedTime)
    }

    override fun showHeaderInfo(companyName: String, date: String) {
        textViewCompanyName.text = companyName.capitalize()
        textViewWorkItemsDate.text = date
    }

    override fun customerSavedSuccessfully() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.customer_sheet_success_message),
            fragmentActivity!!, object : CustomDialogListener {
                override fun onConfirmClick() {
                    customerSheetPresenter.getCustomerSheetByDate(Date(selectedTime))
                }
            })
    }

    /** Fragment Interaction Listeners */
    override fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String) {
        customerSheetPresenter.saveCustomerSheet(customerName, notesCustomer, signatureFilePath)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date) {
        customerSheetPresenter.getCustomerSheetByDate(date)
    }
}
