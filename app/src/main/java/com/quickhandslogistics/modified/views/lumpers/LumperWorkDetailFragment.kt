package com.quickhandslogistics.modified.views.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import com.quickhandslogistics.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_lumper_work_detail.*
import java.util.*

class LumperWorkDetailFragment : BaseFragment() {

    private var employeeData: EmployeeData? = null

    companion object {
        @JvmStatic
        fun newInstance(employeeData: EmployeeData) = LumperWorkDetailFragment().apply {
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
        return inflater.inflate(R.layout.fragment_lumper_work_detail, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employeeData?.let { employeeData ->
            textViewShift.text = if (!StringUtils.isNullOrEmpty(employeeData.shift)) employeeData.shift!!.capitalize(Locale.getDefault()) else "-"
            textViewShiftHours.text = if (!StringUtils.isNullOrEmpty(employeeData.shiftHours)) employeeData.shiftHours else "-"

            employeeData.fullTime?.also { fullTime ->
                textViewAvailability.text = if (fullTime) getString(R.string.full_time) else getString(R.string.part_time)
            } ?: run {
                textViewAvailability.text = "-"
            }

            employeeData.abilityToTravelBetweenBuildings?.also { abilityToTravelBetweenBuildings ->
                textViewAbilityTravel.text = if (abilityToTravelBetweenBuildings) getString(R.string.string_yes) else getString(
                    R.string.string_no
                )
            } ?: run {
                textViewAbilityTravel.text = "-"
            }

            textViewPrimaryBuilding.text = if (!StringUtils.isNullOrEmpty(employeeData.primaryBuilding)) employeeData.primaryBuilding else "-"
            textViewMilesRadius.text = if (!StringUtils.isNullOrEmpty(employeeData.milesRadiusFromPrimaryBuilding))
                String.format("%s Miles", employeeData.milesRadiusFromPrimaryBuilding) else "-"
        }
    }
}