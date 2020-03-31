package com.quickhandslogistics.modified.data.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.io.Serializable

class LoginUserData : EmployeeData(), Serializable {
    @SerializedName("token")
    @Expose
    var token: String? = null

}