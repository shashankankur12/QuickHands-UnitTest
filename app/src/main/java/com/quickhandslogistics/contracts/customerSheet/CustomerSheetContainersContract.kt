package com.quickhandslogistics.contracts.customerSheet

import com.quickhandslogistics.data.schedule.WorkItemDetail

class CustomerSheetContainersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onBOItemClick(workItemDetail: WorkItemDetail, parameters: ArrayList<String>)
            fun onNotesItemClick(notesQHLCustomer: String?)
        }
    }
}