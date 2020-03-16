package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.MenuItem
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.LumperPagerAdapter
import kotlinx.android.synthetic.main.content_lumper_detail.*

class LumperDetailActivity : BaseActivity() {

    private var lumperData: LumperData? = null
    private lateinit var lumperPagerAdapter: LumperPagerAdapter

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_detail)
        setupToolbar()

        displayLumperDetails()

        lumperPagerAdapter = LumperPagerAdapter(supportFragmentManager, resources)
        viewPagerLumperDetail.adapter = lumperPagerAdapter
        tabLayoutLumperDetail.setupWithViewPager(viewPagerLumperDetail)
    }

    private fun displayLumperDetails() {
        intent.extras?.let {
            if (it.containsKey(ARG_LUMPER_DATA)) {
                lumperData = it.getSerializable(ARG_LUMPER_DATA) as LumperData

                lumperData?.let {
//                    textViewLumperName.text = String.format(
//                        "%s %s",
//                        lumperData?.firstName,
//                        lumperData?.lastName
//                    )
//                    textViewEmail.text = lumperData?.email
//                    textViewPhone.text = lumperData?.phone
//                    textViewRole.text = lumperData?.role
                }
            }
        }
    }
}
