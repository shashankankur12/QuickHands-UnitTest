package com.quickhandslogistics.views

import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.LeadProfileContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.LeadProfilePresenter
import com.quickhandslogistics.views.common.FullScreenImageActivity
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.content_lead_profile.*
import java.util.*


class LeadProfileActivity : BaseActivity(), LeadProfileContract.View, View.OnClickListener {

    private var employeeData: LeadProfileData? = null

    private lateinit var leadProfilePresenter: LeadProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)
        setupToolbar(title = getString(R.string.my_profile))

      //  circleImageViewProfile.setOnClickListener(this)

        leadProfilePresenter = LeadProfilePresenter(this, resources, sharedPref)
        leadProfilePresenter.loadLeadProfileData()
    }

    override fun onDestroy() {
        super.onDestroy()
        leadProfilePresenter.onDestroy()
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                circleImageViewProfile.id -> {
                    if (!employeeData?.profileImageUrl.isNullOrEmpty()) {
                        val bundle = Bundle()
                        bundle.putString(FullScreenImageActivity.ARG_IMAGE_URL, employeeData?.profileImageUrl)
                        startZoomIntent(FullScreenImageActivity::class.java, bundle, circleImageViewProfile)
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun loadLeadProfile(employeeData: LeadProfileData) {
        this.employeeData = employeeData
        UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)

        textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
        textViewEmployeeId.text = if (!employeeData.employeeId.isNullOrEmpty()) employeeData.employeeId else "-"
        textViewRole.text = if (!employeeData.role.isNullOrEmpty()) employeeData.role!!.toUpperCase(Locale.getDefault()) else "-"
        textViewEmailAddress.text = if (!employeeData.email.isNullOrEmpty()) employeeData.email else "-"

        val phoneNumber = UIUtils.getDisplayPhoneNumber(employeeData)
        textViewPhoneNumber.text = if (phoneNumber.isNotEmpty()) phoneNumber else "-"

        textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "-"
        textViewBuildingName.text = if (!employeeData.buildingDetailData?.buildingName.isNullOrEmpty()) employeeData.buildingDetailData?.buildingName!!.capitalize() else "-"
    }
}