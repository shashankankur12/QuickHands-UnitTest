package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_lumper_personal_detail.*

class LumperPersonalDetailFragment : BaseFragment() {

    private var lumperData: LumperData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lumperData = it.getSerializable(ARG_LUMPER_DATA) as LumperData?
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

        lumperData?.let {
            textViewFirstName.text = it.firstName
            textViewLastName.text = it.lastName
            it.employeeId?.also { employeeId ->
                textViewEmployeeId.text = employeeId as CharSequence?
            } ?: run {
                textViewEmployeeId.text = "-"
            }
            textViewEmailAddress.text = it.email
            textViewPhoneNumber.text = it.phone
        }
    }

    companion object {
        private const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"

        @JvmStatic
        fun newInstance(lumperData: LumperData) =
            LumperPersonalDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LUMPER_DATA, lumperData)
                }
            }
    }
}
