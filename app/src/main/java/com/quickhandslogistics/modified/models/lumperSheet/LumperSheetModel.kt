package com.quickhandslogistics.modified.models.lumperSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import java.util.*

class LumperSheetModel : LumperSheetContract.Model {

    override fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        DataManager.getAllLumpersData(object : ResponseListener<AllLumpersResponse> {
            override fun onSuccess(response: AllLumpersResponse) {
                if (response.success) {
                    onFinishedListener.onSuccess(response, selectedDate)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(LumperSheetModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }
}