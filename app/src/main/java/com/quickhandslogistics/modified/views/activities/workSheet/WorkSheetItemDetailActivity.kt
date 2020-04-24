package com.quickhandslogistics.modified.views.activities.workSheet

import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.workSheet.WorkSheetItemDetailPagerAdapter
import kotlinx.android.synthetic.main.activity_work_sheet_item_detail.*

class WorkSheetItemDetailActivity : BaseActivity() {

    private lateinit var adapter: WorkSheetItemDetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_sheet_item_detail)
        setupToolbar(getString(R.string.work_sheet_detail))

        adapter = WorkSheetItemDetailPagerAdapter(supportFragmentManager, resources)
        viewPagerWorkSheetDetail.adapter = adapter
        tabLayoutWorkSheetDetail.setupWithViewPager(viewPagerWorkSheetDetail)
    }
}
