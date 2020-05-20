package com.quickhandslogistics.utils

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue

object UIUtils {

    fun getEmployeeFullName(employeeData: EmployeeData?): String {
        var fullName = ""
        employeeData?.let {
            fullName = String.format(
                "%s %s",
                getDefaultOrValue(employeeData.firstName).trim().capitalize(),
                getDefaultOrValue(employeeData.lastName).trim().capitalize()
            )
        }
        return fullName
    }

    fun getDisplayEmployeeID(employeeData: EmployeeData?): String {
        var displayEmployeeId = ""
        employeeData?.let {
            displayEmployeeId = getDisplayEmployeeID(employeeData.employeeId)
        }
        return displayEmployeeId
    }

    fun getDisplayEmployeeID(employeeId: String?): String {
        var displayEmployeeId = ""
        if (!employeeId.isNullOrEmpty()) {
            displayEmployeeId = String.format("(Emp ID: %s)", employeeId)
        }
        return displayEmployeeId
    }

    fun getDisplayShiftHours(employeeData: EmployeeData?): String {
        var displayShiftHours = ""
        employeeData?.let {
            if (!employeeData.shiftHours.isNullOrEmpty()) {
                displayShiftHours = String.format("(Shift Hours: %s)", employeeData.shiftHours)
            }
        }
        return displayShiftHours
    }

    fun showEmployeeProfileImage(context: Context, employeeData: EmployeeData?, imageView: ImageView) {
        employeeData?.let {
            showEmployeeProfileImage(context, employeeData.profileImageUrl, imageView)
        }
    }

    fun showEmployeeProfileImage(context: Context, profileImageUrl: String?, imageView: ImageView) {
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(context).load(profileImageUrl).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(imageView)
        } else {
            Glide.with(context).clear(imageView)
        }
    }

    fun getDisplayPhoneNumber(employeeData: EmployeeData?): String {
        var phoneNumber = ""
        employeeData?.let {
            phoneNumber = String.format("+1 %s", PhoneNumberUtils.formatNumber(it.phone, "US"))
        }
        return phoneNumber
    }
}

