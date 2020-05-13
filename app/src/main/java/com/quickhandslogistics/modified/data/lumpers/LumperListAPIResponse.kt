package com.quickhandslogistics.modified.data.lumpers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.PaginationResponse
import com.quickhandslogistics.utils.StringUtils

class LumperListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : PaginationResponse() {
        @SerializedName("records")
        @Expose
        var employeeDataList: ArrayList<EmployeeData>? = null
            get() = if (!field.isNullOrEmpty()) {
                field?.sortWith(Comparator { lumper1, lumper2 ->
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
}