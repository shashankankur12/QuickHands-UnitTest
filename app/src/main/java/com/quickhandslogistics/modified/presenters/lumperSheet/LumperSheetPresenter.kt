package com.quickhandslogistics.modified.presenters.lumperSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.models.lumperSheet.LumperSheetModel
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class LumperSheetPresenter(
    private var lumperSheetView: LumperSheetContract.View?,
    private val resources: Resources
) : LumperSheetContract.Presenter, LumperSheetContract.Model.OnFinishedListener {

    private val lumperSheetModel: LumperSheetModel = LumperSheetModel()

    override fun getLumpersSheetByDate(date: Date) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, date)
        lumperSheetView?.showDateString(dateString)

        lumperSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        lumperSheetModel.fetchLumperSheetList(this)
    }

    override fun onDestroy() {
        lumperSheetView = null
    }

    override fun onFailure(message: String) {
        lumperSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            lumperSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: AllLumpersResponse) {
        lumperSheetView?.hideProgressDialog()
        lumperSheetView?.showLumperSheetData(response.data!!)
    }
}