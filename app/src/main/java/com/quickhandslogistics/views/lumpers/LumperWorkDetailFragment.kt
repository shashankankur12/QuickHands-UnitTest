package com.quickhandslogistics.views.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
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
            textViewShift.text = if (!employeeData.shift.isNullOrEmpty()) employeeData.shift!!.capitalize(Locale.getDefault()) else "-"
            textViewShiftHours.text = if (!employeeData.shiftHours.isNullOrEmpty()) employeeData.shiftHours else "-"

            employeeData.fullTime?.also { fullTime ->
                textViewAvailability.text = if (fullTime) getString(R.string.full_time) else getString(R.string.part_time)
            } ?: run {
                textViewAvailability.text = "-"
            }

            employeeData.abilityToTravelBetweenBuildings?.also { abilityToTravelBetweenBuildings ->
                textViewAbilityTravel.text = if (abilityToTravelBetweenBuildings) getString(R.string.yes) else getString(
                    R.string.no
                )
            } ?: run {
                textViewAbilityTravel.text = "-"
            }

            textViewPrimaryBuilding.text = if (!employeeData.primaryBuilding.isNullOrEmpty()) employeeData.primaryBuilding else "-"
            textViewMilesRadius.text = if (!employeeData.milesRadiusFromPrimaryBuilding.isNullOrEmpty())
                String.format("%s Miles", employeeData.milesRadiusFromPrimaryBuilding) else "-"
        }
    }
}