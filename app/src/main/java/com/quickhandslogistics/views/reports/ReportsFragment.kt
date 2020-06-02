package com.quickhandslogistics.views.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.reports.ReportsAdapter
import com.quickhandslogistics.contracts.reports.ReportsContract
import com.quickhandslogistics.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_reports.*

class ReportsFragment : BaseFragment(), ReportsContract.View.OnAdapterItemClickListener {

    private lateinit var reportsAdapter: ReportsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewReports.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            reportsAdapter = ReportsAdapter(resources, this@ReportsFragment)
            adapter = reportsAdapter
        }
    }

    /** Adapter Listeners */
    override fun showLumperJobReport() {
        startIntent(LumperJobReportActivity::class.java)
    }

    override fun showTimeClockReport() {
        startIntent(TimeClockReportActivity::class.java)
    }

    override fun showCustomerSheetReport() {
        startIntent(CustomerReportActivity::class.java)
    }
}