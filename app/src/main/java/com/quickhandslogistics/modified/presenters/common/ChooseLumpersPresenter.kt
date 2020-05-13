package com.quickhandslogistics.modified.presenters.common

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.common.ChooseLumpersContract
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.modified.models.common.ChooseLumpersModel
import com.quickhandslogistics.utils.ValueUtils

class ChooseLumpersPresenter(
    private var chooseLumpersView: ChooseLumpersContract.View?, private val resources: Resources
) : ChooseLumpersContract.Presenter, ChooseLumpersContract.Model.OnFinishedListener {

    private val chooseLumpersModel: ChooseLumpersModel = ChooseLumpersModel()

    override fun fetchLumpersList(pageIndex: Int) {
        chooseLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        chooseLumpersModel.fetchLumpersList(pageIndex, this)
    }

    override fun onDestroy() {
        chooseLumpersView = null
    }

    override fun onFailure(message: String) {
        chooseLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            chooseLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            chooseLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse, currentPageIndex: Int) {
        chooseLumpersView?.hideProgressDialog()

        val totalPagesCount = ValueUtils.getDefaultOrValue(response.data?.pageCount)
        val nextPageIndex = ValueUtils.getDefaultOrValue(response.data?.next)

        chooseLumpersView?.showLumpersData(response.data?.employeeDataList!!, totalPagesCount, nextPageIndex, currentPageIndex)
    }
}