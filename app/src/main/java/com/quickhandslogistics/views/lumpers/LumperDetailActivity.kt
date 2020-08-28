package com.quickhandslogistics.views.lumpers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumpers.LumperPagerAdapter
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.common.FullScreenImageActivity
import kotlinx.android.synthetic.main.content_lead_profile.*
import kotlinx.android.synthetic.main.content_lumper_detail.*
import kotlinx.android.synthetic.main.content_lumper_detail.circleImageViewProfile
import kotlinx.android.synthetic.main.content_lumper_detail.textViewBuildingName
import kotlinx.android.synthetic.main.content_lumper_detail.textViewCompanyName
import kotlinx.android.synthetic.main.content_lumper_detail.textViewDepartment
import kotlinx.android.synthetic.main.content_lumper_detail.textViewEmailAddress
import kotlinx.android.synthetic.main.content_lumper_detail.textViewEmployeeId
import kotlinx.android.synthetic.main.content_lumper_detail.textViewLumperName
import kotlinx.android.synthetic.main.content_lumper_detail.textViewPhoneNumber
import kotlinx.android.synthetic.main.content_lumper_detail.textViewRole
import kotlinx.android.synthetic.main.content_lumper_detail.textViewShift
import kotlinx.android.synthetic.main.content_lumper_detail.textViewShiftHours
import kotlinx.android.synthetic.main.content_lead_profile.textViewScheduleNote as textViewScheduleNote1
import kotlinx.android.synthetic.main.content_lead_profile.textViewTitle as textViewTitle1
import kotlinx.android.synthetic.main.content_lumper_detail.textViewAvailability as textViewAvailability1
import kotlinx.android.synthetic.main.content_lumper_detail.textViewCustomerName as textViewCustomerName1

class LumperDetailActivity : BaseActivity(), View.OnClickListener {

    private var employeeData: EmployeeData? = null
    private lateinit var lumperPagerAdapter: LumperPagerAdapter

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
        const val ARG_LUMPER_TIMING_DATA = "ARG_LUMPER_TIMING_DATA"
        const val ARG_LUMPER_PRESENT= "ARG_LUMPER_PRESENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_detail)
        setupToolbar(title = getString(R.string.lumper_profile))

        intent.extras?.let { it ->
            if (it.containsKey(ARG_LUMPER_DATA)) {
                employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
            }
        }

        initializeUI()
    }

    private fun initializeUI() {
        employeeData?.let { employeeData ->
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            var buildingDetailData: BuildingDetailData

            buildingDetailData = if(!employeeData.isTemporaryAssigned) leadProfile?.buildingDetailData!! else employeeData.buildingAssignedAsLumper!!

            UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)
            UIUtils.updateProfileBorder(activity, employeeData.isTemporaryAssigned, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)

            if (!buildingDetailData.buildingName.isNullOrEmpty() && !employeeData.role.isNullOrEmpty()) {
                textViewCompanyName.text =
                    employeeData.role!!.capitalize() + " at " + buildingDetailData?.buildingName!!.capitalize()
            } else textViewCompanyName.visibility = View.GONE

            textViewEmailAddress.text = if (!employeeData.email.isNullOrEmpty()) employeeData.email else "---"
            val phoneNumber = UIUtils.getDisplayPhoneNumber(employeeData)
            textViewPhoneNumber.text = if (phoneNumber.isNotEmpty()) phoneNumber else "---"

            textViewEmployeeId.text = if (!employeeData.employeeId.isNullOrEmpty()) employeeData.employeeId else "---"
            textViewRole.text = if (!employeeData.role.isNullOrEmpty()) employeeData.role!!.capitalize() else "---"
            textViewDepartment.text = if (!employeeData.department.isNullOrEmpty()) UIUtils.getDisplayEmployeeDepartment(employeeData) else "---"
            textViewTitle.text = if (!employeeData.title.isNullOrEmpty()) employeeData.title!!.capitalize() else "---"

            textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "---"
            textViewShift.text = if (!employeeData.shift.isNullOrEmpty()) employeeData.shift?.capitalize() else "---"
            textViewScheduleNote.text = if (!employeeData.scheduleNotes.isNullOrEmpty()) UIUtils.getSpannedText(getString(R.string.schedule_note) + employeeData.scheduleNotes) else UIUtils.getSpannedText(getString(R.string.schedule_note_lead))
            textViewAvailability.text = if (employeeData.fullTime!!) getString(R.string.full_time_ud) else getString(R.string.part_time_ud)
            viewAttendanceStatus.setBackgroundResource(if (employeeData.isPresent!!) R.drawable.online_dot else R.drawable.offline_dot)

            textViewBuildingName.text = if (!buildingDetailData?.buildingName.isNullOrEmpty()) buildingDetailData?.buildingName!!.capitalize() else "---"
            textViewCustomerName.text = if (!buildingDetailData?.customerDetail?.companyAdminName.isNullOrEmpty()) buildingDetailData?.customerDetail?.companyAdminName!!.capitalize() else "---"


            //circleImageViewProfile.setOnClickListener(this@LumperDetailActivity)
            textViewPhoneNumber.setOnClickListener(this@LumperDetailActivity)
            textViewEmailAddress.setOnClickListener(this@LumperDetailActivity)
        }
    }
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
                textViewPhoneNumber.id -> {
                    if (!employeeData?.phone.isNullOrEmpty()) {
                        var phone=employeeData?.phone
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")) // Initiates the Intent
                        startActivity(intent)
                    }
                }
                textViewEmailAddress.id -> {
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
}