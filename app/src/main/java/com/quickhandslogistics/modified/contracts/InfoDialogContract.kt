package com.quickhandslogistics.modified.contracts

class InfoDialogContract {
    interface View {
        interface OnClickListener {
            fun onPositiveButtonClick()
            fun onNegativeButtonClick()
        }
    }
}