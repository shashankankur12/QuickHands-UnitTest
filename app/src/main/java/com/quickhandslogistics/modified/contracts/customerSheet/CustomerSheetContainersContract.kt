package com.quickhandslogistics.modified.contracts.customerSheet

import com.quickhandslogistics.modified.data.schedule.WorkItemDetail

class CustomerSheetContainersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemDetail: WorkItemDetail)
        }
    }
}