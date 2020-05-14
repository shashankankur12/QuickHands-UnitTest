package com.quickhandslogistics.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import de.hdodenhof.circleimageview.CircleImageView

object UIUtils {

    fun getEmployeeFullName(employeeData: EmployeeData?): String {
        var fullName = ""
        employeeData?.let {
            fullName = String.format(
                "%s %s",
                getDefaultOrValue(employeeData.firstName).capitalize(),
                getDefaultOrValue(employeeData.lastName).capitalize()
            )
        }
        return fullName
    }

    fun getDisplayEmployeeID(employeeData: EmployeeData?): String {
        var dsisplayEmployeeId = ""
        employeeData?.let {
            if (!StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                dsisplayEmployeeId = String.format("(Emp ID: %s)", employeeData.employeeId)
            }
        }
        return dsisplayEmployeeId
    }

    fun getDisplayEmployeeID(employeeId: String?): String {
        var dsisplayEmployeeId = ""
        if (!StringUtils.isNullOrEmpty(employeeId)) {
            dsisplayEmployeeId = String.format("(Emp ID: %s)", employeeId)
        }
        return dsisplayEmployeeId
    }

    fun showEmployeeProfileImage(context: Context, employeeData: EmployeeData?, imageView: CircleImageView) {
        employeeData?.let {
            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                Glide.with(context).load(employeeData.profileImageUrl).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(imageView)
            } else {
                Glide.with(context).clear(imageView)
            }
        }
    }

    fun showEmployeeProfileImage(context: Context, profileImageUrl: String?, imageView: CircleImageView) {
        if (!StringUtils.isNullOrEmpty(profileImageUrl)) {
            Glide.with(context).load(profileImageUrl).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(imageView)
        } else {
            Glide.with(context).clear(imageView)
        }
    }
}

