package com.quickhandslogistics.modified.contracts.schedule

class UnScheduleDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onLumperImagesClick()
            fun onWorkItemClick()
            fun onAddLumpersItemClick()
        }
    }
}