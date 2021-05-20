package com.quickhandslogistics.views.workSheet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetPagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.controls.Quintuple
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.ContainerGroupNote
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.presenters.workSheet.WorkSheetPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ScheduleUtils.getGroupNoteList
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.bottom_work_sheet_item.*
import kotlinx.android.synthetic.main.content_work_sheet.*
import kotlinx.android.synthetic.main.fragment_work_sheet.*

class WorkSheetFragment : BaseFragment(), WorkSheetContract.View, WorkSheetContract.View.OnFragmentInteractionListener,
    View.OnClickListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetPresenter: WorkSheetPresenter
    private var adapter: WorkSheetPagerAdapter? = null
    private var data: WorkSheetListAPIResponse.Data = WorkSheetListAPIResponse.Data()
    private lateinit var date: String
    private lateinit var shift: String
    private lateinit var dept: String
    private lateinit var companyName: String
    private lateinit var customerGroupNote:Triple<Pair<ArrayList<String>,ArrayList<String>>, ArrayList<String>, ArrayList<String>>
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var containerIds: ArrayList<String> = ArrayList()
    private var containerType= ""

    companion object {
        const val WORKSHEET_DETAIL = "WORKSHEET_DETAIL"
        const val WORKSHEET_DATE_SELECTED_HEADER = "WORKSHEET_DATE_SELECTED_HEADER"
        const val WORKSHEET_COMPANY_NAME = "WORKSHEET_COMPANY_NAME"
        const val WORKSHEET_SHIFT = "WORKSHEET_SHIFT"
        const val WORKSHEET_DEPT = "WORKSHEET_DEPT"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workSheetPresenter = WorkSheetPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutWorkSheetItem)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        textViewGroupNote.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
        buttonCancelGroupNote.setOnClickListener(this)
        buttonSaveGroupNote.setOnClickListener(this)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(WORKSHEET_DATE_SELECTED_HEADER)) {
                date = savedInstanceState.getString(WORKSHEET_DATE_SELECTED_HEADER)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_SHIFT)) {
                shift = savedInstanceState.getString(WORKSHEET_SHIFT)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_DEPT)) {
                dept = savedInstanceState.getString(WORKSHEET_DEPT)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_COMPANY_NAME)) {
                companyName = savedInstanceState.getString(WORKSHEET_COMPANY_NAME)!!
                showHeaderInfo(companyName, date, shift, dept)
            }
            if (savedInstanceState.containsKey(WORKSHEET_DETAIL)) {
                data = savedInstanceState.getParcelable<WorkSheetListAPIResponse.Data>(WORKSHEET_DETAIL) as WorkSheetListAPIResponse.Data
                showWorkSheets(data)

                val allWorkItemLists = createDifferentListData(data)
                initializeViewPager(allWorkItemLists)
            }
        } ?: run {
            initializeViewPager()
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            workSheetPresenter.fetchWorkSheetList()
        }
        refreshData()
        closeBottomSheet()
    }

    private fun refreshData() {

        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        swipe_pull_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            initializeViewPager()

            workSheetPresenter.fetchWorkSheetList()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        workSheetPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (data != null)
            outState.putParcelable(WORKSHEET_DETAIL, data)
        if (!date.isNullOrEmpty())
            outState.putString(WORKSHEET_DATE_SELECTED_HEADER, date)
        if (!shift.isNullOrEmpty())
            outState.putString(WORKSHEET_SHIFT, shift)
        if (!dept.isNullOrEmpty())
            outState.putString(WORKSHEET_DEPT, dept)
        outState.putSerializable(WORKSHEET_COMPANY_NAME, companyName)
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentInteractionListener = null
    }

    private fun initializeViewPager(allWorkItemLists: Quintuple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null) {
        adapter = if (allWorkItemLists != null) {
            WorkSheetPagerAdapter(childFragmentManager, resources, allWorkItemLists, data.containerGroupNote, data.unfinishedNotes, data.notOpenNotes)
        } else {
            WorkSheetPagerAdapter(childFragmentManager, resources)
        }
        viewPagerWorkSheet.offscreenPageLimit = adapter?.count!!
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)

    }

    private fun createDifferentListData(data: WorkSheetListAPIResponse.Data): Quintuple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>> {
        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(data.inProgress!!)
        onGoingWorkItems.addAll(data.onHold!!)
        onGoingWorkItems.addAll(data.scheduled!!)

        return Quintuple(getSortList(onGoingWorkItems), getSortList(data.cancelled!!), getSortList(data.completed!!), getSortList(data.unfinished!!), getSortList(data.notOpen!!))
    }

    private fun resetUI() {
        // Reset Whole Screen Data
        textViewCompanyName.text = ""
        textViewWorkItemsDate.text = ""
        textViewWorkItemShift.text = ""
        textViewWorkItemDept.text = ""
        textViewTotalCount.text = ""
        textViewLiveLoadsCount.text = ""
        textViewDropsCount.text = ""
        textViewOutBoundsCount.text = ""
        textViewUnfinishedCount.text = ""
        textViewGroupNote.isEnabled=false
        adapter?.updateWorkItemsList(
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList(),
            null,
            null,
            null
        )
    }

    private fun closeBottomSheet() {
        AppUtils.hideSoftKeyboard(activity!!)
        bottomSheetBackground.visibility=View.GONE
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        constraintLayoutWorkSheetItem.visibility = View.GONE
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, fragmentActivity!!)
        } else SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        swipe_pull_refresh?.isRefreshing = false
        resetUI()
        onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
    }

    override fun showWorkSheets(data: WorkSheetListAPIResponse.Data) {
        this.data = data
        swipe_pull_refresh?.isRefreshing = false

        customerGroupNote=getGroupNoteList(data)
        textViewGroupNote.isEnabled = (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0||customerGroupNote.first.second.size>0 ))


        // Change the visibility of Cancel All Schedule Option
        if (data.inProgress.isNullOrEmpty() && data.onHold.isNullOrEmpty() && data.cancelled.isNullOrEmpty() && data.completed.isNullOrEmpty()&& data.unfinished.isNullOrEmpty()&& data.notOpen.isNullOrEmpty() && !data.scheduled.isNullOrEmpty()) {
            onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(true)
        } else {
            onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
        }

        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(data.inProgress!!)
        onGoingWorkItems.addAll(data.onHold!!)
        onGoingWorkItems.addAll(data.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(data.cancelled!!)
        allWorkItems.addAll(data.completed!!)
        allWorkItems.addAll(data.notOpen!!)
        val workItemTypeCounts = ScheduleUtils.getWorkItemTypeCounts(allWorkItems)

        if (workItemTypeCounts.first > 0) {
            textViewLiveLoadsCount.visibility = View.VISIBLE
            textViewLiveLoadsCount.text =
                String.format(getString(R.string.live_load_s), workItemTypeCounts.first)
        } else textViewLiveLoadsCount.visibility = View.GONE

        if (workItemTypeCounts.second > 0) {
            textViewDropsCount.visibility = View.VISIBLE
            textViewDropsCount.text =
                String.format(getString(R.string.drops_value), workItemTypeCounts.second)
        } else textViewDropsCount.visibility = View.GONE

        if (workItemTypeCounts.third > 0) {
            textViewOutBoundsCount.visibility = View.VISIBLE
            textViewOutBoundsCount.text =
                String.format(getString(R.string.out_bound_s), workItemTypeCounts.third)
        } else textViewOutBoundsCount.visibility = View.GONE

        if (data.unfinished?.size!! > 0) {
            textViewUnfinishedCount.visibility = View.VISIBLE
            textViewUnfinishedCount.text =
                String.format(getString(R.string.unfinished_s), data.unfinished?.size)
        } else textViewUnfinishedCount.visibility = View.GONE

        allWorkItems.addAll(data.unfinished!!)
        textViewTotalCount.text = UIUtils.getSpannableText(getString(R.string.total_container_bold), allWorkItems.size.toString())

        adapter?.updateWorkItemsList(getSortList(onGoingWorkItems), getSortList(data.cancelled!!), getSortList(data.completed!!), getSortList(data.unfinished!!),getSortList(data.notOpen!!), data.containerGroupNote, data.unfinishedNotes, data.notOpenNotes)
    }

    private fun getSortList(workItemsList: ArrayList<WorkItemDetail>): ArrayList<WorkItemDetail> {
        val inboundList: ArrayList<WorkItemDetail> = ArrayList()
        val outBoundList: ArrayList<WorkItemDetail> = ArrayList()
        val liveList: ArrayList<WorkItemDetail> = ArrayList()
        var sortedList: ArrayList<WorkItemDetail> = ArrayList()

        workItemsList.forEach {
            when {
                it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> {
                    liveList.add(it)
                }
                it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> {
                    inboundList.add(it)
                }
                it.type.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> {
                    outBoundList.add(it)
                }
            }
        }
        sortedList.addAll(outBoundList)
        sortedList.addAll(liveList)
        sortedList.addAll(inboundList)
        return sortedList
    }

    override fun showHeaderInfo(companyName: String, date: String, shift: String, dept: String) {
        this.companyName = companyName
        this.date = date
        this.shift=shift
        this.dept=dept

        textViewCompanyName.text = companyName.capitalize()
        textViewWorkItemsDate.text = UIUtils.getSpannedText(date)
        textViewWorkItemShift.text = UIUtils.getSpannedText(shift)
        textViewWorkItemDept.text = UIUtils.getSpannedText(dept)
    }

    override fun successGroupNoteSave(message: String) {
        CustomProgressBar.getInstance().showSuccessDialog(message, fragmentActivity!!, object : CustomDialogListener {
            override fun onConfirmClick() {
                workSheetPresenter.fetchWorkSheetList()
            }
        })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Child Fragment Interaction Listeners */
    override fun fetchWorkSheetList() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        workSheetPresenter.fetchWorkSheetList()
    }

    override fun showBottomSheetGroupNote(groupNote: ContainerGroupNote?, containerIds: ArrayList<String>, containerType: String) {
        constraintLayoutWorkSheetItem.visibility=View.VISIBLE
        this.containerIds.clear()
        this.containerType=""
        groupNote?.also {
            buttonSaveGroupNote.setText(R.string.update)
            textViewTitle.setText(R.string.update_group_notes)
            textViewTitle.setTag(R.id.note, groupNote.id)
            editTextQHLCustomerNotes.setText(groupNote.noteForCustomer)
            editTextQHLNotes.setText(groupNote.noteForQHL)
        }?:run {
            buttonSaveGroupNote.setText(R.string.save)
            textViewTitle.setText(R.string.group_note)
            textViewTitle.setTag(R.id.note, "")
            editTextQHLCustomerNotes.setText("")
            editTextQHLNotes.setText("")
        }

        this.containerType=containerType
        this.containerIds=containerIds

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    override fun showGroupNote(noteForCustomer: String, noteForQHL: String) {
        CustomerDialog.showLeadNoteDialog(activity, "Group Notes ",noteForCustomer , noteForQHL, resources.getString(R.string.notes_for_customer), resources.getString(R.string.notes_for_qhl))
    }

    override fun removeGroupNote(id: String?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.warning_alert_message_group_note), fragmentActivity!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                id?.let { workSheetPresenter.removeNote(it) }
            }

            override fun onCancelClick() {
            }
        })

    }

    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        when(view!!.id){
            textViewGroupNote.id->{
                if (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0|| customerGroupNote.first.second.size>0))
                CustomerDialog.showGroupNoteDialog(activity, "Customer Notes :", customerGroupNote)

            }
            bottomSheetBackground.id->{closeBottomSheet()}
            buttonCancelGroupNote.id->{closeBottomSheet()}
            buttonSaveGroupNote.id->{
               getWorkContainerIds()
            }
        }
    }

    private fun getWorkContainerIds() {
        val containerIds :ArrayList<String> = ArrayList()
        when (containerType) {
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                data.cancelled?.forEach {
                    it.id?.let { it1 -> containerIds.add(it1) }
                }
            }
            AppConstant.WORK_ITEM_STATUS_UNFINISHED -> {
                data.unfinished?.forEach {
                    it.id?.let { it1 -> containerIds.add(it1) }
                }
            }
            AppConstant.WORK_ITEM_STATUS_NOT_OPEN -> {
                data.notOpen?.forEach {
                    it.id?.let { it1 -> containerIds.add(it1) }
                }
            }
        }
        if (!containerIds.isNullOrEmpty()) {
            saveGroupNote(containerIds)
        }
    }

    private fun saveGroupNote(containerIds: ArrayList<String>) {
        val customerNote= editTextQHLCustomerNotes.text.toString()
        val qhlNote= editTextQHLNotes.text.toString()
        val groupNoteId = textViewTitle.getTag(R.id.note) as String?

        when {
            customerNote.isNullOrEmpty() -> {
                CustomProgressBar.getInstance().showValidationErrorDialog(resources.getString(R.string.group_customer_note_error_message), activity!!)
            }
            qhlNote.isNullOrEmpty() -> {
                CustomProgressBar.getInstance().showValidationErrorDialog(resources.getString(R.string.group_qhl_note_error_message), activity!!)
            }
            else -> {
                closeBottomSheet()
                if (groupNoteId.isNullOrEmpty())
                    workSheetPresenter.saveGroupNoteData(
                        containerIds,
                        containerType,
                        customerNote,
                        qhlNote
                    )
                else workSheetPresenter.updateGroupNoteData(
                    groupNoteId,
                    containerIds,
                    containerType,
                    customerNote,
                    qhlNote
                )
            }
        }
    }
}
