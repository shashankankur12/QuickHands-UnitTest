package com.quickhandslogistics.views.customerSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.customerSheet.CustomerSheetPagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.customerSheet.CustomerSheetScheduleDetails
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.customerSheet.CustomerSheetPresenter
import com.quickhandslogistics.utils.CalendarUtils
import com.quickhandslogistics.utils.CustomDialogListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_customer_sheet.*
import java.util.*


class CustomerSheetFragment : BaseFragment(), CustomerSheetContract.View, CustomerSheetContract.View.OnFragmentInteractionListener, CalendarUtils.CalendarSelectionListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>
    private lateinit var scheduleDetails: CustomerSheetScheduleDetails
    private  var customerSheet: CustomerSheetData = CustomerSheetData()
    private lateinit var selectedDate: Date
    private lateinit var companyName: String
    private lateinit var date: String

    private lateinit var customerSheetPresenter: CustomerSheetPresenter
    private lateinit var adapter: CustomerSheetPagerAdapter

    companion object {
        const val DATE = "DATE"
        const val CUSTOMER_SHEET= "CUSTOMER_SHEET"
        const val SCHEDULE_DETAIL = "SCHEDULE_DETAIL"
        const val DATE_STRING = "DATE_STRING"
        const val NAME = "NAME"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerSheetPresenter = CustomerSheetPresenter(this, resources, sharedPref)

        // Setup Calendar Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.fragments.size>0&&childFragmentManager!=null){
            var fragment: Fragment = childFragmentManager.fragments[0]
            var fragment2: Fragment = childFragmentManager.fragments[1]
            childFragmentManager.beginTransaction().remove(fragment).commit()
            childFragmentManager.beginTransaction().remove(fragment2).commit()
        }
        adapter = CustomerSheetPagerAdapter(childFragmentManager, resources)
        viewPagerCustomerSheet.offscreenPageLimit = adapter.count
        viewPagerCustomerSheet.adapter = adapter
        tabLayoutCustomerSheet.setupWithViewPager(viewPagerCustomerSheet)



        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarCustomerSheet, availableDates, this)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(NAME)) {
                companyName = savedInstanceState.getString(NAME)!!
            }
            if (savedInstanceState.containsKey(DATE_STRING)) {
                date = savedInstanceState.getString(DATE_STRING)!!
                showHeaderInfo(companyName, date)
            }
            if (savedInstanceState.containsKey(SCHEDULE_DETAIL)) {
                scheduleDetails = savedInstanceState.getSerializable(SCHEDULE_DETAIL)!!as CustomerSheetScheduleDetails
            }
            if(savedInstanceState.containsKey(DATE)) {
                selectedDate = savedInstanceState.getSerializable(DATE) as Date
            }
            if (savedInstanceState.containsKey(CUSTOMER_SHEET)) {
                customerSheet = savedInstanceState.getSerializable(CUSTOMER_SHEET) as CustomerSheetData
                showCustomerSheets(scheduleDetails, customerSheet, selectedDate)
            }
        } ?: run {
            singleRowCalendarCustomerSheet.select(availableDates.size - 1)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        customerSheetPresenter.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        if (selectedDate!=null)
        outState.putSerializable(DATE, selectedDate)
        if (customerSheet!=null)
        outState.putSerializable(CUSTOMER_SHEET, customerSheet)
        if (scheduleDetails!=null)
        outState.putSerializable(SCHEDULE_DETAIL, scheduleDetails)
        if (selectedDate!=null)
        outState.putSerializable(DATE, selectedDate)
        if (companyName!=null)
        outState.putSerializable(NAME, companyName)
        if (date!=null)
        outState.putSerializable(DATE_STRING, date)
    }

       override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        // Reset Whole Screen Data
        textViewCompanyName.text = ""
        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
    }

    override fun showCustomerSheets(scheduleDetails: CustomerSheetScheduleDetails, customerSheet: CustomerSheetData?, selectedDate: Date) {
        this.selectedDate=selectedDate
        this.scheduleDetails=scheduleDetails
        if (customerSheet != null) {
            this.customerSheet= customerSheet
        }


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
        this.companyName=companyName
        this.date=date

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
    override fun onSelectCalendarDate(
        date: Date,
        selected: Boolean,
        position: Int
    ) {
        customerSheetPresenter.getCustomerSheetByDate(date)
    }
}
