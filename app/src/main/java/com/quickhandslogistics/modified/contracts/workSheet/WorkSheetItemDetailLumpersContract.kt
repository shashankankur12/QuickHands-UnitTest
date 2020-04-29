package com.quickhandslogistics.modified.contracts.workSheet

class WorkSheetItemDetailLumpersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onAddTimeClick(itemPosition: Int)
            fun onAddNotes(updatedDataSize: Int)
        }
    }
}