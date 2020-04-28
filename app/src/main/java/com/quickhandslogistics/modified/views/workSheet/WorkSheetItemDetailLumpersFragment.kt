package com.quickhandslogistics.modified.views.workSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.adapters.workSheet.WorkItemDetailLumpersAdapter
import kotlinx.android.synthetic.main.bottom_sheet_add_work_item_time.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail_lumpers.*

class WorkSheetItemDetailLumpersFragment : BaseFragment(), View.OnClickListener,
    WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener {

    private lateinit var workItemDetailLumpersAdapter: WorkItemDetailLumpersAdapter
    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? =
        null

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener =
                activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_lumpers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            workItemDetailLumpersAdapter =
                WorkItemDetailLumpersAdapter(this@WorkSheetItemDetailLumpersFragment)
            adapter = workItemDetailLumpersAdapter
        }

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetWorkItem)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        buttonSave.setOnClickListener(this)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        onFragmentInteractionListener?.changeBottomSheetBackgroundVisibility(View.GONE)
        //bottomSheetBackgroundWorkItemTime.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSave.id -> {

                }
            }
        }
    }

    override fun onAddTimeClick(itemPosition: Int) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            onFragmentInteractionListener?.changeBottomSheetBackgroundVisibility(View.VISIBLE)
            //bottomSheetBackgroundWorkItemTime.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    override fun onAddNotes(updatedDataSize: Int) {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WorkSheetItemDetailLumpersFragment()
    }
}