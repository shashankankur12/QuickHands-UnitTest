package com.quickhandslogistics.modified.contracts.schedule

class AddWorkItemLumpersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onSelectLumper(totalSelectedCount: Int)
        }
    }
}