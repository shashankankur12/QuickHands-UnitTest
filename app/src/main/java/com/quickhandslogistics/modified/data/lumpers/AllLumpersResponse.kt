package com.quickhandslogistics.modified.data.lumpers

import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.utils.StringUtils

class AllLumpersResponse : BaseResponse() {
    val data: ArrayList<EmployeeData>? = null
        get() = if (!field.isNullOrEmpty()) {
            field.sortWith(Comparator { lumper1, lumper2 ->
                if (!StringUtils.isNullOrEmpty(lumper1.firstName)
                    && !StringUtils.isNullOrEmpty(lumper2.firstName)
                ) {
                    lumper1.firstName?.toLowerCase()!!.compareTo(lumper2.firstName?.toLowerCase()!!)
                } else {
                    0
                }
            })
            field
        } else ArrayList()
}