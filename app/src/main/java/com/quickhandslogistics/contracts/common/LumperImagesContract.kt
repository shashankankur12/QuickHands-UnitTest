package com.quickhandslogistics.contracts.common

import com.quickhandslogistics.data.lumpers.EmployeeData

class LumperImagesContract {
    interface OnItemClickListener {
        fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>)
    }
}
