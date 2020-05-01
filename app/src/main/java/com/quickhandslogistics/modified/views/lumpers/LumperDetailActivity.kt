package com.quickhandslogistics.modified.views.lumpers

import android.os.Bundle
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.adapters.lumpers.LumperPagerAdapter
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
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

        displayLumperDetails()
    }

    private fun displayLumperDetails() {
        intent.extras?.let { it ->
            if (it.containsKey(ARG_LUMPER_DATA)) {
                employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
                employeeData?.let { lumperData ->

                    if (!StringUtils.isNullOrEmpty(lumperData.profileImageUrl)) {
                        Glide.with(activity).load(lumperData.profileImageUrl)
                            .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                            .into(circleImageViewProfile)
                    } else {
                        Glide.with(activity).clear(circleImageViewProfile);
                    }

                    textViewLumperName.text = String.format(
                        "%s %s",
                        ValueUtils.getDefaultOrValue(lumperData.firstName),
                        ValueUtils.getDefaultOrValue(lumperData.lastName)
                    )

                    initializeUI(lumperData)
                }
            }
        }
    }

    private fun initializeUI(employeeData: EmployeeData) {
        lumperPagerAdapter =
            LumperPagerAdapter(
                supportFragmentManager,
                resources,
                employeeData
            )
        viewPagerLumperDetail.adapter = lumperPagerAdapter
        tabLayoutLumperDetail.setupWithViewPager(viewPagerLumperDetail)
    }
}
