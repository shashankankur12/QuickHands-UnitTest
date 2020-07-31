package com.quickhandslogistics.views.lumpers

import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumpers.LumperPagerAdapter
import com.quickhandslogistics.data.lumpers.EmployeeData
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
import java.util.*
import kotlinx.android.synthetic.main.content_lead_profile.textViewAvailability as textViewAvailability1
import kotlinx.android.synthetic.main.content_lead_profile.textViewScheduleNote as textViewScheduleNote1
import kotlinx.android.synthetic.main.content_lead_profile.textViewTitle as textViewTitle1

class LumperDetailActivity : BaseActivity(), View.OnClickListener {

    private var employeeData: EmployeeData? = null
    private lateinit var lumperPagerAdapter: LumperPagerAdapter

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
        const val ARG_LUMPER_TIMING_DATA = "ARG_LUMPER_TIMING_DATA"
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
            UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)
            UIUtils.updateProfileBorder(activity, employeeData.isTemporaryAssigned, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)

//            if (!employeeData.buildingDetailData?.buildingName.isNullOrEmpty() && !employeeData.role.isNullOrEmpty()) {
//                textViewCompanyName.text =
//                    employeeData.role!!.capitalize() + " at " + employeeData.buildingDetailData?.buildingName!!.capitalize()
//            } else textViewCompanyName.visibility = View.GONE

            textViewEmailAddress.text = if (!employeeData.email.isNullOrEmpty()) employeeData.email else "-"
            val phoneNumber = UIUtils.getDisplayPhoneNumber(employeeData)
            textViewPhoneNumber.text = if (phoneNumber.isNotEmpty()) phoneNumber else "-"

            textViewEmployeeId.text = if (!employeeData.employeeId.isNullOrEmpty()) employeeData.employeeId else "-"
            textViewRole.text = if (!employeeData.role.isNullOrEmpty()) employeeData.role!!.capitalize() else "-"
            textViewDepartment.text = if (!employeeData.department.isNullOrEmpty()) employeeData.department!!.capitalize() else "-"
            textViewTitle.text = if (!employeeData.title.isNullOrEmpty()) employeeData.title!!.capitalize() else "---"

            textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "-"
            textViewShift.text = if (!employeeData.shift.isNullOrEmpty()) employeeData.shift?.capitalize() else "-"
            textViewScheduleNote.text = if (!employeeData.scheduleNotes.isNullOrEmpty()) getString(R.string.schedule_note) + employeeData.scheduleNotes else getString(R.string.schedule_note_lead)
            textViewAvailability.text = if (employeeData.fullTime!!) getString(R.string.full_time) else getString(R.string.part_time)

//            textViewBuildingName.text = if (!employeeData.buildingDetailData?.buildingName.isNullOrEmpty()) employeeData.buildingDetailData?.buildingName!!.capitalize() else "-"
//            textViewCustomerName.text = if (!employeeData.buildingDetailData?.customerDetail?.companyAdminName.isNullOrEmpty()) employeeData.buildingDetailData?.customerDetail?.companyAdminName!!.capitalize() else "-"


            //circleImageViewProfile.setOnClickListener(this@LumperDetailActivity)
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
            }
        }
    }
}