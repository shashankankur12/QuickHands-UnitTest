package com.quickhandslogistics.modified.views

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.presenters.LeadProfilePresenter
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
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

    override fun loadLeadProfile(employeeData: LeadProfileData) {
        if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
            Glide.with(activity).load(employeeData.profileImageUrl)
                .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                .into(circleImageViewProfile)
        } else {
            Glide.with(activity).clear(circleImageViewProfile);
        }

        textViewLumperName.text = String.format(
            "%s %s",
            ValueUtils.getDefaultOrValue(employeeData.firstName).capitalize(),
            ValueUtils.getDefaultOrValue(employeeData.lastName).capitalize()
        )

        textViewEmployeeId.text =
            if (!StringUtils.isNullOrEmpty(employeeData.employeeId)) employeeData.employeeId else "-"
        textViewRole.text =
            if (!StringUtils.isNullOrEmpty(employeeData.role)) employeeData.role!!.toUpperCase(
                Locale.getDefault()
            ) else "-"
        textViewEmailAddress.text =
            if (!StringUtils.isNullOrEmpty(employeeData.email)) employeeData.email else "-"
        textViewPhoneNumber.text =
            if (!StringUtils.isNullOrEmpty(employeeData.phone)) PhoneNumberUtils.formatNumber(
                employeeData.phone,
                "US"
            ) else "-"
        textViewShiftHours.text =
            if (!StringUtils.isNullOrEmpty(employeeData.shiftHours)) employeeData.shiftHours else "-"

        textViewBuildingName.text =
            if (!StringUtils.isNullOrEmpty(employeeData.buildingDetailData?.buildingName)) employeeData.buildingDetailData?.buildingName else "-"
    }
}