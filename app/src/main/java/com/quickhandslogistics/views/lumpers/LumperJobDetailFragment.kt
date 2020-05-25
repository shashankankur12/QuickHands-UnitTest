package com.quickhandslogistics.views.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import kotlinx.android.synthetic.main.fragment_lumper_job_detail.*
import java.util.*

class LumperJobDetailFragment : BaseFragment() {

    private var employeeData: EmployeeData? = null

    companion object {
        @JvmStatic
        fun newInstance(employeeData: EmployeeData) = LumperJobDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_LUMPER_DATA, employeeData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            employeeData = bundle.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumper_job_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let {
            textViewTitle.text = if (!it.title.isNullOrEmpty()) it.title!!.toUpperCase(Locale.getDefault()) else "-"
            textViewHiringDate.text = if (!it.hiringDate.isNullOrEmpty()) it.hiringDate else "-"
            textViewWorkSchedule.text = if (!it.workSchedule.isNullOrEmpty()) it.workSchedule else "-"
            textViewLastDayWorked.text = if (!it.lastDayWorked.isNullOrEmpty()) it.lastDayWorked else "-"
            textViewJobDescription.text = if (!it.jobDescription.isNullOrEmpty()) it.jobDescription else "-"
        }
    }
}