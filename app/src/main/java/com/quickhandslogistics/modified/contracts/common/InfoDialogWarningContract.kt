package com.quickhandslogistics.modified.contracts.common

class InfoDialogWarningContract {
    interface View {
        interface OnClickListener {
            fun onPositiveButtonClick()
            fun onNegativeButtonClick()
        }
    }
}