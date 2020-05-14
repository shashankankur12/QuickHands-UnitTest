package com.quickhandslogistics.modified.views.lumpers

import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.lumpers.LumperPagerAdapter
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.content_lumper_detail.*

class LumperDetailActivity : BaseActivity() {

    private var employeeData: EmployeeData? = null
    private lateinit var lumperPagerAdapter: LumperPagerAdapter

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
        const val ARG_LUMPER_TIMING_DATA = "ARG_LUMPER_TIMING_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_detail)
        setupToolbar()

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
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)

            lumperPagerAdapter = LumperPagerAdapter(supportFragmentManager, resources, employeeData)
            viewPagerLumperDetail.adapter = lumperPagerAdapter
            tabLayoutLumperDetail.setupWithViewPager(viewPagerLumperDetail)
        }
    }
}