package com.quickhandslogistics.modified.views.workSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkItemDetailLumpersAdapter
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.modified.views.BaseFragment
import kotlinx.android.synthetic.main.content_work_sheet_item_detail_lumpers.*

class WorkSheetItemDetailLumpersFragment : BaseFragment(), View.OnClickListener,
    WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener {

    private lateinit var workItemDetailLumpersAdapter: WorkItemDetailLumpersAdapter
    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? =
        null

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

        buttonAddLumpers.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAddLumpers.id -> {

                }
            }
        }
    }

    override fun onAddTimeClick(itemPosition: Int) {
        startIntent(AddLumperTimeWorkSheetItemActivity::class.java)
    }

    override fun onAddNotes(updatedDataSize: Int) {

    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkSheetItemDetailLumpersFragment()
    }
}