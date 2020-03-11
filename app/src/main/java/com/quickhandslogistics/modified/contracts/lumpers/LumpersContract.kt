package com.quickhandslogistics.modified.contracts.lumpers

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.LumperData

class LumpersContract {
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
            fun onItemClick(lumperData: LumperData)
            fun onPhoneViewClick(phone: String)
        }
    }

    interface Presenter {
        fun fetchLumpersList()

    }
}