package com.quickhandslogistics.modified.contracts

class InfoDialogWarningContract {
    interface View {
        interface OnClickListener {
            fun onPositiveButtonClick()
            fun onNegativeButtonClick()
        }
    }
}