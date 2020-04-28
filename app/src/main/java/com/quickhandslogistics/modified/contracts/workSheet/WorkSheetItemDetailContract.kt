package com.quickhandslogistics.modified.contracts.workSheet

class WorkSheetItemDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onSelectStatus(status: String)
        }

        interface OnFragmentInteractionListener {
        }
    }
}