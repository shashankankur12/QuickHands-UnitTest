package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_lumper_job_detail.*
import java.util.*

class LumperJobDetailFragment : BaseFragment() {

    private var employeeData: EmployeeData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            employeeData = it.getSerializable(ARG_LUMPER_DATA) as EmployeeData?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumper_job_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let {
            textViewTitle.text =
                if (!StringUtils.isNullOrEmpty(it.title)) it.title!!.toUpperCase(Locale.getDefault()) else "-"
            textViewHiringDate.text =
                if (!StringUtils.isNullOrEmpty(it.hiringDate)) it.hiringDate else "-"
            textViewWorkSchedule.text =
                if (!StringUtils.isNullOrEmpty(it.workSchedule)) it.workSchedule else "-"
            textViewLastDayWorked.text =
                if (!StringUtils.isNullOrEmpty(it.lastDayWorked)) it.lastDayWorked else "-"
            textViewJobDescription.text =
                if (!StringUtils.isNullOrEmpty(it.jobDescription)) it.jobDescription else "-"
        }
    }

    companion object {
        private const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"

        @JvmStatic
        fun newInstance(employeeData: EmployeeData) =
            LumperJobDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LUMPER_DATA, employeeData)
                }
            }
    }
}
