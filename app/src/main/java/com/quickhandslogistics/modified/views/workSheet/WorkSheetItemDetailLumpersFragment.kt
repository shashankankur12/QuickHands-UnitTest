package com.quickhandslogistics.modified.views.workSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetItemDetailLumpersAdapter
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.modified.views.schedule.AddWorkItemLumpersActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.content_work_sheet_item_detail_lumpers.*

class WorkSheetItemDetailLumpersFragment : BaseFragment(), View.OnClickListener,
    WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener {

    private var workItemDetail: WorkItemDetail? = null

    private lateinit var workSheetItemDetailLumpersAdapter: WorkSheetItemDetailLumpersAdapter
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
            workSheetItemDetailLumpersAdapter =
                WorkSheetItemDetailLumpersAdapter(this@WorkSheetItemDetailLumpersFragment)
            adapter = workSheetItemDetailLumpersAdapter
        }

        workSheetItemDetailLumpersAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (workSheetItemDetailLumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        buttonAddLumpers.setOnClickListener(this)
    }

    fun showLumpersData(workItemDetail: WorkItemDetail) {
        this.workItemDetail = workItemDetail
        workSheetItemDetailLumpersAdapter.updateList(workItemDetail.assignedLumpersList)

        if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
            buttonAddLumpers.text = getString(R.string.add_lumpers)
        } else {
            buttonAddLumpers.text = getString(R.string.update_lumpers)
        }

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                buttonAddLumpers.visibility = View.GONE
            } else {
                buttonAddLumpers.visibility = View.VISIBLE
            }
        }
    }

    fun showEmptyData() {
        workSheetItemDetailLumpersAdapter.updateList(ArrayList())
        buttonAddLumpers.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAddLumpers.id -> {
                    workItemDetail?.let { workItemDetail ->
                        val bundle = Bundle()
                        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
                        bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.workItemType)
                        if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
                            bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
                        } else {
                            bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                            bundle.putParcelableArrayList(
                                AddWorkItemLumpersActivity.ARG_ASSIGNED_LUMPERS_LIST,
                                workItemDetail.assignedLumpersList
                            )
                        }
                        startIntent(
                            AddWorkItemLumpersActivity::class.java, bundle = bundle,
                            requestCode = AppConstant.REQUEST_CODE_CHANGED
                        )
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            onFragmentInteractionListener?.fetchWorkItemDetail(changeResultCode = true)
        }
    }

    override fun onAddTimeClick(employeeData: EmployeeData) {
        val bundle = Bundle()
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(
            AddLumperTimeWorkSheetItemActivity::class.java,
            bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkSheetItemDetailLumpersFragment()
    }
}