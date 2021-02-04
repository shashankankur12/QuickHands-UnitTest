package com.quickhandslogistics.presenters.addContainer

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.addContainer.AddContainerContract
import com.quickhandslogistics.data.ErrorResponse

class AddContainerPresenter(private var addContainerContractView: AddContainerContract.View?, private val resources: Resources): AddContainerContract.Presenter, AddContainerContract.Model.OnFinishedListener {

//    private val allWorkScheduleCancelModel = AllWorkScheduleCancelModel()
    
    /** View Listeners */
    override fun addTodayWorkContainer(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String) {
   
    }

    override fun onDestroy() {
        addContainerContractView=null
    }

    /** Model Result Listeners */
    override fun onSuccessAddTodayWorkContainer() {
        
    }

    override fun onFailure(message: String) {
        addContainerContractView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addContainerContractView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            addContainerContractView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
     
    }
}