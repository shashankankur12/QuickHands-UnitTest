package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_lumper_work_detail.*

class LumperWorkDetailFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_lumper_work_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let { it ->
            textViewShift.text = if (!StringUtils.isNullOrEmpty(it.shift)) it.shift else "-"
            textViewShiftHours.text =
                if (!StringUtils.isNullOrEmpty(it.shiftHours)) it.shiftHours else "-"
            it.fullTime?.also { fullTime ->
                textViewAvailability.text =
                    if (fullTime) getString(R.string.full_time) else getString(R.string.part_time)
            } ?: run {
                textViewAvailability.text = "-"
            }
            it.abilityToTravelBetweenBuildings?.also { abilityToTravelBetweenBuildings ->
                textViewAbilityTravel.text =
                    if (abilityToTravelBetweenBuildings) getString(R.string.string_yes) else getString(
                        R.string.string_no
                    )
            } ?: run {
                textViewAbilityTravel.text = "-"
            }
            textViewPrimaryBuilding.text =
                if (!StringUtils.isNullOrEmpty(it.primaryBuilding)) it.primaryBuilding else "-"
            textViewMilesRadius.text =
                if (!StringUtils.isNullOrEmpty(it.milesRadiusFromPrimaryBuilding)) String.format(
                    "%s Miles",
                    it.milesRadiusFromPrimaryBuilding
                ) else "-"
        }
    }

    companion object {
        private const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"

        @JvmStatic
        fun newInstance(employeeData: EmployeeData) =
            LumperWorkDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LUMPER_DATA, employeeData)
                }
            }
    }
}
