package com.quickhandslogistics.modified.contracts.workSheet

class WorkSheetItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemId: String, workItemTypeDisplayName: String)
        }
    }
}