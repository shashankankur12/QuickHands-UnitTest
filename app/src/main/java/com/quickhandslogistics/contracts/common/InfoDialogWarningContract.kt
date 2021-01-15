package com.quickhandslogistics.contracts.common

class InfoDialogWarningContract {
    interface View {
        interface OnClickListener {
            fun onPositiveButtonClick()
            fun onNegativeButtonClick()
        }
    }
}