package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.LumperData

class ChooseLumperContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(allLumpersResponse: AllLumpersResponse)
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(lumperDataList: ArrayList<LumperData>)

        interface OnAdapterItemClickListener {
            fun onClickLumperDetail(lumperData: LumperData)
            fun onSelectLumper(lumperData: LumperData)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
        fun onDestroy()
    }
}