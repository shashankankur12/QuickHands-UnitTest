package com.quickhandslogistics.modified.views

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.presenters.LeadProfilePresenter
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.content_lead_profile.*
import java.util.*

class LeadProfileActivity : BaseActivity(), LeadProfileContract.View {

    private lateinit var leadProfilePresenter: LeadProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)
        setupToolbar(title = getString(R.string.my_profile))

        leadProfilePresenter = LeadProfilePresenter(this, resources, sharedPref)
        leadProfilePresenter.loadLeadProfileData()
    }

    override fun onDestroy() {
        super.onDestroy()
        leadProfilePresenter.onDestroy()
    }

    /** Presenter Listeners */
    override fun loadLeadProfile(employeeData: LeadProfileData) {
        UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)

        textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
        textViewEmployeeId.text = if (!employeeData.employeeId.isNullOrEmpty()) employeeData.employeeId else "-"
        textViewRole.text = if (!employeeData.role.isNullOrEmpty()) employeeData.role!!.toUpperCase(Locale.getDefault()) else "-"
        textViewEmailAddress.text = if (!employeeData.email.isNullOrEmpty()) employeeData.email else "-"
        textViewPhoneNumber.text = if (!employeeData.phone.isNullOrEmpty()) PhoneNumberUtils.formatNumber(employeeData.phone, "US") else "-"
        textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "-"
        textViewBuildingName.text = if (!employeeData.buildingDetailData?.buildingName.isNullOrEmpty()) employeeData.buildingDetailData?.buildingName else "-"
    }
}