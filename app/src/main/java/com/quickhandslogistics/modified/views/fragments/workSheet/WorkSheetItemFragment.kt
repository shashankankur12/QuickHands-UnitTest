package com.quickhandslogistics.modified.views.fragments.workSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.workSheet.WorkSheetItemAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_work_sheet_item.*
import java.util.*

class WorkSheetItemFragment : BaseFragment(), ScheduleContract.View,
    ScheduleContract.View.OnAdapterItemClickListener {

    private var workItemType: String = ""

    //    private lateinit var schedulePresenter: SchedulePresenter
    private lateinit var workSheetItemAdapter: WorkSheetItemAdapter
    private var onScheduleFragmentInteractionListener: ScheduleMainContract.View.OnScheduleFragmentInteractionListener? =
        null

    /* override fun onAttach(context: Context) {
         super.onAttach(context)
         if (parentFragment is ScheduleMainContract.View.OnScheduleFragmentInteractionListener) {
             onScheduleFragmentInteractionListener =
                 parentFragment as ScheduleMainContract.View.OnScheduleFragmentInteractionListener
         }
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")
        }
        //schedulePresenter = SchedulePresenter(this, resources, sharedPref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewWorkSheet.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            workSheetItemAdapter = WorkSheetItemAdapter(workItemType, resources, this@WorkSheetItemFragment)
            adapter = workSheetItemAdapter
        }
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onScheduleItemClick(scheduleDetail: ScheduleDetail) {
        /*val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, isCurrentDate)
        bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleDetail.scheduleIdentity)
        bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(
            ScheduleDetailActivity::class.java, bundle = bundle,
            requestCode = AppConstant.REQUEST_CODE_CHANGED
        )*/
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        /*val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)*/
    }

    override fun showDateString(dateString: String) {

    }

    /*
    * Presenter Listeners
    */
    override fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetail>) {
//        scheduleAdapter.updateList(workItemsList)

        textViewEmptyData.visibility = View.GONE
        recyclerViewWorkSheet.visibility = View.VISIBLE
    }

    override fun hideProgressDialog() {
        onScheduleFragmentInteractionListener?.hideProgressDialog()
    }

    override fun showProgressDialog(message: String) {
        onScheduleFragmentInteractionListener?.showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewWorkSheet.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun fetchUnsScheduledWorkItems() {
        onScheduleFragmentInteractionListener?.fetchUnScheduledWorkItems()
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewWorkSheet.visibility = View.GONE
    }

    fun fetchScheduledWorkItems() {

    }

    companion object {
        private const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"

        @JvmStatic
        fun newInstance(workItemType: String) =
            WorkSheetItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_WORK_ITEM_TYPE, workItemType)
                }
            }
    }
}