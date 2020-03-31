package com.quickhandslogistics.modified.contracts.lumperSheet

import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.data.lumperSheet.StatusModel


class LumperSheetContract {
    interface Model {
        fun fetchLumperSheetList(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(lumperList: ArrayList<LumperModel>)
        }
    }

    interface View {
        fun showLumperSheetData(
            lumperList: ArrayList<LumperModel>
        )

        interface OnAdapterItemClickListener {
            fun onItemClick(lumperData: LumperModel)
        }
    }

    interface Presenter {
        fun fetchLumpersList()
    }
}