package com.quickhandslogistics.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.text.Html
import android.text.Spanned
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.data.attendance.LumperAttendanceData
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

    fun getPresentLumperFullName(employeeData: LumperAttendanceData?): String {
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

    fun getDisplayPresentLumperID(employeeData: LumperAttendanceData?): String {
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
            circleImageView.borderColor = ContextCompat.getColor(context, R.color.temp_lumper_background)
            circleImageView.borderWidth = ((context.resources.getDimension(R.dimen.circleTemporaryBorder) / context.resources.displayMetrics.density).toInt())
        } else {
            circleImageView.borderColor = ContextCompat.getColor(context, R.color.imageBorder)
            circleImageView.borderWidth = ((context.resources.getDimension(R.dimen.circleTemporaryBorder) / context.resources.displayMetrics.density).toInt())
        }
    }

    fun getDisplayPhoneNumber(employeeData: EmployeeData?): String {
        var phoneNumber = ""
        employeeData?.let {
            phoneNumber = PhoneNumberUtils.formatNumber(it.phone, "US")
        }
        return phoneNumber
    }

    fun getDisplayEmployeeDepartment(employeeData: EmployeeData?): String {
        var displayDepartment = ""
        employeeData?.let {
            if (!employeeData.department.isNullOrEmpty()) {
                displayDepartment = when (employeeData.department) {
                    AppConstant.EMPLOYEE_DEPARTMENT_BOTH -> "Operations "
                    AppConstant.EMPLOYEE_DEPARTMENT_INBOUND -> "Receiving "
                    AppConstant.EMPLOYEE_DEPARTMENT_OUTBOUND -> "Shipping"
                    else -> employeeData.department!!
                }
            }
        }
        return displayDepartment
    }

    fun getSpannedText(text: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

    fun getSpannableText(sourseString: String, valueString: String): Spanned? {
        var stringFormet=String.format(sourseString, valueString)
        return getSpannedText(stringFormet)
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun formetMobileNumber(original: String): String? {
        val isbnStr = StringBuilder()
        for (i in original.indices) {
            when (i) {
                0 -> { isbnStr.append("(") }
                3 -> { isbnStr.append(") ") }
                6 -> { isbnStr.append(" - ") }
            }
            isbnStr.append(original[i])
        }
        return isbnStr.toString()
    }
}

