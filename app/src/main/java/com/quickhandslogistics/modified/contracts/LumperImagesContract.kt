package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class LumperImagesContract {
    interface OnItemClickListener {
        fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>)
    }
}
