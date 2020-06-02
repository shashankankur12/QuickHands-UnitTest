package com.quickhandslogistics.contracts.reports

class ReportsContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun showLumperJobReport()
            fun showTimeClockReport()
            fun showCustomerSheetReport()
        }
    }
}