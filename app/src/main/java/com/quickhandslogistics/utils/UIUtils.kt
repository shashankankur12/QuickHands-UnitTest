package com.quickhandslogistics.utils

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import de.hdodenhof.circleimageview.CircleImageView

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

    fun updateProfileBorder(context: Context, isTemporaryAssigned: Boolean?, circleImageView: CircleImageView) {
        if (getDefaultOrValue(isTemporaryAssigned)) {
            circleImageView.borderColor = ContextCompat.getColor(context, android.R.color.holo_orange_light)
            circleImageView.borderWidth = ((context.resources.getDimension(R.dimen.circleTemporaryBorder) / context.resources.displayMetrics.density).toInt())
        } else {
            circleImageView.borderColor = ContextCompat.getColor(context, R.color.imageBorder)
            circleImageView.borderWidth = ((context.resources.getDimension(R.dimen.circleNormalBorder) / context.resources.displayMetrics.density).toInt())
        }
    }

    fun getDisplayPhoneNumber(employeeData: EmployeeData?): String {
        var phoneNumber = ""
        employeeData?.let {
            phoneNumber = PhoneNumberUtils.formatNumber(it.phone, "US")
        }
        return phoneNumber
    }
}
