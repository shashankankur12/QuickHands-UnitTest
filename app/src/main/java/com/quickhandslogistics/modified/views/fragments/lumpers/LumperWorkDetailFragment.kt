package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_lumper_work_detail.*

class LumperWorkDetailFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_lumper_work_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lumperData?.let { it ->
            textViewShift.text = it.shift
            it.shiftHours?.also { shiftHours ->
                textViewShiftHours.text = shiftHours as CharSequence?
            } ?: run {
                textViewShiftHours.text = "-"
            }
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
            it.primaryBuilding?.also { primaryBuilding ->
                textViewPrimaryBuilding.text = primaryBuilding as CharSequence?
            } ?: run {
                textViewPrimaryBuilding.text = "-"
            }
            textViewMilesRadius.text = String.format("%s Miles", it.milesRadiusFromPrimaryBuilding)
        }
    }

    companion object {
        private const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"

        @JvmStatic
        fun newInstance(lumperData: LumperData) =
            LumperWorkDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LUMPER_DATA, lumperData)
                }
            }
    }
}
