package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_lumper_personal_detail.*

class LumperPersonalDetailFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_lumper_personal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let {
            textViewFirstName.text =
                if (!StringUtils.isNullOrEmpty(it.firstName)) it.firstName else "-"
            textViewLastName.text =
                if (!StringUtils.isNullOrEmpty(it.lastName)) it.lastName else "-"
            textViewEmployeeId.text =
                if (!StringUtils.isNullOrEmpty(it.employeeId)) it.employeeId else "-"
            textViewEmailAddress.text = if (!StringUtils.isNullOrEmpty(it.email)) it.email else "-"
            textViewPhoneNumber.text =
                if (!StringUtils.isNullOrEmpty(it.phone)) PhoneNumberUtils.formatNumber(
                    it.phone,
                    "US"
                ) else "-"
        }
    }

    companion object {
        private const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"

        @JvmStatic
        fun newInstance(employeeData: EmployeeData) =
            LumperPersonalDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LUMPER_DATA, employeeData)
                }
            }
    }
}
