package com.quickhandslogistics.modified.views.lumpers

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import kotlinx.android.synthetic.main.fragment_lumper_personal_detail.*

class LumperPersonalDetailFragment : BaseFragment() {

    private var employeeData: EmployeeData? = null

    companion object {
        @JvmStatic
        fun newInstance(employeeData: EmployeeData) = LumperPersonalDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_LUMPER_DATA, employeeData)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumper_personal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let {
            textViewFirstName.text = if (!it.firstName.isNullOrEmpty()) it.firstName else "-"
            textViewLastName.text = if (!it.lastName.isNullOrEmpty()) it.lastName else "-"
            textViewEmployeeId.text = if (!it.employeeId.isNullOrEmpty()) it.employeeId else "-"
            textViewEmailAddress.text = if (!it.email.isNullOrEmpty()) it.email else "-"
            textViewPhoneNumber.text = if (!it.phone.isNullOrEmpty()) PhoneNumberUtils.formatNumber(it.phone, "US") else "-"
        }
    }
}