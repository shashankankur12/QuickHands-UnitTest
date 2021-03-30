package com.quickhandslogistics.contracts.workSheet

class WorkSheetItemDetailNoteImageContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onImageClick(imageUrl: String)
        }
    }
}