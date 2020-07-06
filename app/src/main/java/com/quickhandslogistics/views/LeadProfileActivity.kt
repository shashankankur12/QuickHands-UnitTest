package com.quickhandslogistics.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.LeadProfileContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.LeadProfilePresenter
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.views.common.FullScreenImageActivity
import kotlinx.android.synthetic.main.content_lead_profile.*
import java.util.*


class LeadProfileActivity : BaseActivity(), LeadProfileContract.View, View.OnClickListener {

    private var employeeData: LeadProfileData? = null

    private lateinit var leadProfilePresenter: LeadProfilePresenter

    companion object {
        const val LEAD_DATA = "LEAD_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_profile)
        setupToolbar(title = getString(R.string.my_profile))

        layoutDMEmail.setOnClickListener(this)
        //  circleImageViewProfile.setOnClickListener(this)

        leadProfilePresenter = LeadProfilePresenter(this, resources, sharedPref)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LEAD_DATA)) {
                employeeData = savedInstanceState.getParcelable<LeadProfileData>(LEAD_DATA)!!
                loadLeadProfile(employeeData!!)
            }
        } ?: run {
            leadProfilePresenter.loadLeadProfileData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        leadProfilePresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeData != null)
            outState.putParcelable(LEAD_DATA, employeeData)
        super.onSaveInstanceState(outState)
    }

    private fun showEmailDialog() {
        val name = UIUtils.getEmployeeFullName(employeeData?.buildingDetailData?.districtManager)
        employeeData?.buildingDetailData?.districtManager?.email?.let { email ->
            CustomProgressBar.getInstance().showWarningDialog(String.format(getString(R.string.email_lumper_alert_message), name),
                activity, object : CustomDialogWarningListener {
                    override fun onConfirmClick() {
                        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                        startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    }

                    override fun onCancelClick() {
                    }
                })
        }
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
                layoutDMEmail.id -> {
                    showEmailDialog()
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
        textViewShift.text = if (!employeeData.shift.isNullOrEmpty()) employeeData.shift?.capitalize() else "-"
        textViewBuildingName.text = if (!employeeData.buildingDetailData?.buildingName.isNullOrEmpty()) employeeData.buildingDetailData?.buildingName!!.capitalize() else "-"
        textViewDepartment.text = if (!employeeData.department.isNullOrEmpty()) UIUtils.getDisplayEmployeeDepartment(employeeData) else "-"

        textViewDMName.text = UIUtils.getEmployeeFullName(employeeData.buildingDetailData?.districtManager)
        textViewDMEmail.text = if (!employeeData.buildingDetailData?.districtManager?.email.isNullOrEmpty()) employeeData.buildingDetailData?.districtManager?.email else "-"
    }
}