package com.quickhandslogistics.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.models.workSheet.WorkSheetModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class WorkSheetPresenter(private var workSheetView: WorkSheetContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    WorkSheetContract.Presenter, WorkSheetContract.Model.OnFinishedListener {

    private val workSheetModel = WorkSheetModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        workSheetView = null
    }

    override fun fetchWorkSheetList() {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetModel.fetchHeaderInfo(this)
        workSheetModel.fetchWorkSheetList(this)
    }

    override fun saveGroupNoteData(containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetModel.saveGroupNoteData(this, containerIds, containerType, customerNote, qhlNote)
    }

    override fun updateGroupNoteData(groupNoteId: String, containerIds: ArrayList<String>, containerType: String, customerNote: String, qhlNote: String) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetModel.updateGroupNoteData(this, containerIds, containerType, customerNote, qhlNote, groupNoteId)
    }

    override fun removeNote (id: String) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetModel.removeNote(this, id)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        workSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            workSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        workSheetView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            workSheetView?.showLoginScreen()
        }
    }

    override fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse) {
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


            workSheetView?.showWorkSheets(data)
        }
    }

    override fun onSuccessGetHeaderInfo(companyName: String, date: String, shift: String, dept: String) {
        workSheetView?.showHeaderInfo(companyName, date, shift, dept)
    }

    override fun onSuccessSaveGroupNoteWorkSheet(message: String) {
        workSheetView?.hideProgressDialog()
        workSheetView?.successGroupNoteSave(message)

    }

    override fun onSuccessRemoveNote(message: String) {
        workSheetView?.hideProgressDialog()
        workSheetView?.successGroupNoteSave(message)
    }
}