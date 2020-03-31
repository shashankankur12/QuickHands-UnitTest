package com.quickhandslogistics.modified.presenters.lumperSheet

import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.models.lumperSheet.LumperSheetModel

class LumperSheetPresenter(
    private val lumpersView: LumperSheetContract.View?
) : LumperSheetContract.Presenter, LumperSheetContract.Model.OnFinishedListener {

    private val lumperSheetModel: LumperSheetModel = LumperSheetModel()

    override fun onFailure(message: String) {

    }

    override fun onSuccess(
        lumperList: ArrayList<LumperModel>
    ) {
        lumpersView?.showLumperSheetData(lumperList)
    }

    override fun fetchLumpersList() {
        lumperSheetModel.fetchLumperSheetList(this)
    }
}