package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.login.UserData
import com.quickhandslogistics.modified.data.profile.LeadProfileData
import com.quickhandslogistics.modified.presenters.LeadProfilePresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_lead_profile.*
import kotlinx.android.synthetic.main.content_lead_profile.*
import kotlinx.android.synthetic.main.fragment_lumpers.*
import java.util.*
import kotlin.collections.ArrayList

class LeadProfileActivity : BaseActivity(), LeadProfileContract.View {

    private lateinit var leadProfilePresenter: LeadProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)
        setupToolbar(title = getString(R.string.my_profile))

        leadProfilePresenter = LeadProfilePresenter(this, sharedPref)
        leadProfilePresenter.loadLeadProfileData()
    }

    override fun onDestroy() {
        super.onDestroy()
        leadProfilePresenter.onDestroy()
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainRoot, message)
    }

    override fun showLumpersData(profileData: LeadProfileData) {
        if (!StringUtils.isNullOrEmpty(profileData.profileImageUrl))
            Picasso.get().load(profileData.profileImageUrl).placeholder(R.drawable.dummy)
                .error(R.drawable.dummy)
                .into(circleImageViewProfile)

        textViewLumperName.text = String.format(
            "%s %s",
            ValueUtils.getDefaultOrValue(profileData.firstName),
            ValueUtils.getDefaultOrValue(profileData.lastName)
        )

        textViewEmployeeId.text =
            if (!StringUtils.isNullOrEmpty(profileData.employeeId)) profileData.employeeId else "-"
        textViewRole.text =
            if (!StringUtils.isNullOrEmpty(profileData.role)) profileData.role!!.toUpperCase(
                Locale.getDefault()
            ) else "-"
        textViewEmailAddress.text =
            if (!StringUtils.isNullOrEmpty(profileData.email)) profileData.email else "-"
        textViewPhoneNumber.text =
            if (!StringUtils.isNullOrEmpty(profileData.phone)) PhoneNumberUtils.formatNumber(
                profileData.phone,
                "US"
            ) else "-"
        textViewShiftHours.text =
            if (!StringUtils.isNullOrEmpty(profileData.shiftHours)) profileData.shiftHours else "-"

        textViewBuildingName.text =
            if (!StringUtils.isNullOrEmpty(profileData.buildingAssignedAsLead?.buildingName))  profileData.buildingAssignedAsLead?.buildingName?.capitalize() else "-"
    }
}
