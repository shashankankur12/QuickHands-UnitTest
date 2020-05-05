package com.quickhandslogistics.modified.contracts.lumperSheet

class LumperWorkDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onBOItemClick(
                buildingOps: HashMap<String, String>, parameters: ArrayList<String>
            )
            fun onNotesItemClick(notes: String?)
        }
    }
}