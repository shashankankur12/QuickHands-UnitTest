package com.quickhandslogistics.views.lumpers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumpers.LumperDetailsContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.lumpers.LumpersDetailsPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.common.FullScreenImageActivity
import kotlinx.android.synthetic.main.content_lumper_detail.*


class LumperDetailActivity : BaseActivity(), View.OnClickListener, LumperDetailsContract.View {
    private var employeeData: EmployeeData? = null
    private lateinit var lumperDetailsPresenter: LumpersDetailsPresenter

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
        const val ARG_LUMPER_TIMING_DATA = "ARG_LUMPER_TIMING_DATA"
        const val ARG_LUMPER_PRESENT= "ARG_LUMPER_PRESENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_detail)
        setupToolbar(title = getString(R.string.lumper_profile))
        lumperDetailsPresenter = LumpersDetailsPresenter(this, resources, sharedPref)

        intent.extras?.let { it ->
            if (it.containsKey(ARG_LUMPER_DATA)) {
                employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
            }
        }

        employeeData?.let {
            if (it.isTemporaryAssigned)
                lumperDetailsPresenter.fetchBuildingInfo(it.originalBuildingId)
            else{
                val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
                val buildingDetailData: BuildingDetailData? = if(!employeeData?.isTemporaryAssigned!!) ScheduleUtils.getBuildingDetailData(leadProfile?.buildingDetailData) else ScheduleUtils.getBuildingDetailData(employeeData?.buildingAssignedAsLumper)
                initializeUI(buildingDetailData)
            }
        }
    }

    private fun initializeUI(buildingDetailData: BuildingDetailData?) {
        employeeData?.let { employeeData ->
            val phoneNumber =if (!employeeData.phone.isNullOrEmpty())UIUtils.formetMobileNumber(employeeData.phone!!)else "---"

            UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)
            UIUtils.updateProfileBorder(activity, employeeData.isTemporaryAssigned, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)

            if (!buildingDetailData?.buildingName.isNullOrEmpty() && !employeeData.role.isNullOrEmpty()) {
                textViewCompanyName.text = "${employeeData.role!!.capitalize()} at ${buildingDetailData?.buildingName!!.capitalize()}"
            } else textViewCompanyName.visibility = View.GONE

            textViewEmailAddress.text = if (!employeeData.email.isNullOrEmpty()) employeeData.email else "---"
            textViewPhoneNumber.text = phoneNumber
            textViewEmployeeId.text = if (!employeeData.employeeId.isNullOrEmpty()) employeeData.employeeId else "---"
            textViewRole.text = if (!employeeData.role.isNullOrEmpty()) employeeData.role?.capitalize() else "---"
            textViewDepartment.text = if (!employeeData.department.isNullOrEmpty()) UIUtils.getDisplayEmployeeDepartment(employeeData) else "---"
            textViewTitle.text = if (!employeeData.title.isNullOrEmpty()) employeeData.title?.capitalize() else "---"
            textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "---"
            textViewShift.text = if (!employeeData.shift.isNullOrEmpty()) employeeData.shift?.capitalize() else "---"
            textViewScheduleNote.text = if (!employeeData.scheduleNotes.isNullOrEmpty()) UIUtils.getSpannedText(getString(R.string.schedule_note) + employeeData.scheduleNotes) else UIUtils.getSpannedText(getString(R.string.schedule_note_lead))
            textViewAvailability.text = if (employeeData.fullTime == true) getString(R.string.full_time_ud) else getString(R.string.part_time_ud)
            viewAttendanceStatus.setBackgroundResource(if (employeeData.isPresent == true) R.drawable.online_dot else R.drawable.offline_dot)
            textViewBuildingName.text = if (!buildingDetailData?.buildingName.isNullOrEmpty()) buildingDetailData?.buildingName?.capitalize() else "---"
            textViewCustomerName.text = if (!buildingDetailData?.customerDetail?.name.isNullOrEmpty()) buildingDetailData?.customerDetail?.name?.capitalize() else "---"

            //circleImageViewProfile.setOnClickListener(this@LumperDetailActivity)
            textViewPhoneNumber.setOnClickListener(this@LumperDetailActivity)
            textViewEmailAddress.setOnClickListener(this@LumperDetailActivity)
        }
    }
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                circleImageViewProfile.id -> {
                    if (!ConnectionDetector.isNetworkConnected(this)) {
                        ConnectionDetector.createSnackBar(this)
                        return
                    }

                    if (!employeeData?.profileImageUrl.isNullOrEmpty()) {
                        val bundle = Bundle()
                        bundle.putString(FullScreenImageActivity.ARG_IMAGE_URL, employeeData?.profileImageUrl)
                        startZoomIntent(FullScreenImageActivity::class.java, bundle, circleImageViewProfile)
                    }
                }
                textViewPhoneNumber.id -> {
                    if (!employeeData?.phone.isNullOrEmpty()) {
                        if (!ConnectionDetector.isNetworkConnected(this)) {
                            ConnectionDetector.createSnackBar(this)
                            return
                        }

                        var phone=employeeData?.phone
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")) // Initiates the Intent
                        startActivity(intent)
                    }
                }
                textViewEmailAddress.id -> {
                    if (!ConnectionDetector.isNetworkConnected(this)) {
                        ConnectionDetector.createSnackBar(this)
                        return
                    }

                    if (!employeeData?.email.isNullOrEmpty()) {
                        var email=employeeData?.email
                        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
                        startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    }
                }
            }
        }
    }

    override fun showAPIErrorMessage(message: String) {
        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, this)
        } else SnackBarFactory.createSnackBar(this, mainConstraintLayout, message)
    }

    override fun showBuildingInfo(buildingDetailData: BuildingDetailData) {
        initializeUI(buildingDetailData)
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}