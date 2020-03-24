package com.quickhandslogistics.modified.contracts.schedule

class WorkItemDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onReplaceItemClick(position: Int)
        }
    }
}