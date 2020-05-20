package com.quickhandslogistics.modified.presenters.customerSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.models.customerSheet.CustomerSheetModel
import com.quickhandslogistics.utils.SharedPref
import java.util.*
import kotlin.Comparator

class CustomerSheetPresenter(private var workSheetView: CustomerSheetContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    CustomerSheetContract.Presenter, CustomerSheetContract.Model.OnFinishedListener {

    private val customerSheetModel = CustomerSheetModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        workSheetView = null
    }

    override fun getCustomerSheetByDate(date: Date) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        customerSheetModel.fetchHeaderInfo(date, this)
        customerSheetModel.fetchCustomerSheetList(date, this)
    }

    override fun saveCustomerSheet(customerName: String, notesCustomer: String, signatureFilePath: String) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        customerSheetModel.saveCustomerSheet(customerName, notesCustomer, signatureFilePath, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        workSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            workSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchCustomerSheet(customerSheetListAPIResponse: CustomerSheetListAPIResponse, selectedDate: Date) {
        workSheetView?.hideProgressDialog()
        customerSheetListAPIResponse.data?.scheduleDetails?.let { scheduleDetails ->

            // Sort all the work Items by their Start Time
            scheduleDetails.inProgress?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            scheduleDetails.onHold?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            scheduleDetails.scheduled?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            scheduleDetails.cancelled?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            scheduleDetails.completed?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })

            workSheetView?.showCustomerSheets(scheduleDetails, customerSheetListAPIResponse.data?.customerSheet, selectedDate)
        }
    }

    override fun onSuccessGetHeaderInfo(companyName: String, date: String) {
        workSheetView?.showHeaderInfo(companyName, date)
    }

    override fun onSuccessSaveCustomerSheet() {
        workSheetView?.hideProgressDialog()
        workSheetView?.customerSavedSuccessfully()
    }
}