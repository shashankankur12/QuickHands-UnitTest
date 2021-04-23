package com.quickhandslogistics.contracts.addContainer

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.addContainer.ContainerDetails

interface AddContainerContract {
    interface Model {
        fun addTodayWorkContainer(
            scheduleNote: String,
            uploadContainer: ArrayList<ContainerDetails>,
            liveLoadContainer: ArrayList<ContainerDetails>,
            dropOffContainer: ArrayList<ContainerDetails>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessAddTodayWorkContainer(message: BaseResponse?)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun addWorkScheduleFinished(message: String)
        fun showLoginScreen()
        interface OnAdapterItemClickListener {
            fun removeItemFromList(position: Int, item: ContainerDetails)
            fun addQuantity(adapterPosition: Int, item: ContainerDetails, quantity: String)
            fun addTimeInList(position: Int, timeInMilli: ContainerDetails, timeInMilli1: String)
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun addTodayWorkContainer(
            uploadContainer: ArrayList<ContainerDetails>,
            liveLoadContainer: ArrayList<ContainerDetails>,
            dropOffContainer: ArrayList<ContainerDetails>,
            scheduleNote: String
        )
    }
}