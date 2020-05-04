package com.quickhandslogistics.modified.presenters.customerSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.modified.models.customerSheet.CustomerSheetModel
import com.quickhandslogistics.utils.SharedPref
import java.util.*
import kotlin.Comparator

class CustomerSheetPresenter(
    private var workSheetView: CustomerSheetContract.View?,
    private val resources: Resources, sharedPref: SharedPref
) : CustomerSheetContract.Presenter, CustomerSheetContract.Model.OnFinishedListener {

    private val customerSheetModel = CustomerSheetModel(sharedPref)

    override fun getCustomerSheetByDate(date: Date) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        customerSheetModel.fetchHeaderInfo(date, this)
        customerSheetModel.fetchCustomerSheetList(date, this)
    }

    override fun onDestroy() {
        workSheetView = null
    }

    override fun onFailure(message: String) {
        workSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            workSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchCustomerSheet(workSheetListAPIResponse: WorkSheetListAPIResponse) {
        workSheetView?.hideProgressDialog()
        workSheetListAPIResponse.data?.let { data ->

            // Sort all the work Items by their Start Time
            data.inProgress?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.onHold?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.scheduled?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.cancelled?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })
            data.completed?.sortWith(Comparator { workItem1, workItem2 ->
                workItem1.startTime!!.compareTo(workItem2.startTime!!)
            })

            workSheetView?.showCustomerSheets(data)
        }
    }

    override fun onSuccessGetHeaderInfo(buildingName: String, date: String) {
        workSheetView?.showHeaderInfo(buildingName, date)
    }
}