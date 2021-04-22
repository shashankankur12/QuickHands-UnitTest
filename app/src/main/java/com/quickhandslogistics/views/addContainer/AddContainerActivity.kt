package com.quickhandslogistics.views.addContainer


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.addContainer.AddContainerAdapter
import com.quickhandslogistics.contracts.addContainer.AddContainerContract
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.addContainer.AddContainerPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_INBOUND
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_LIVE
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_OUTBOUND
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.activity_add_container.*
import kotlinx.android.synthetic.main.custome_add_container_header.*



class AddContainerActivity : BaseActivity(), View.OnClickListener, AddContainerContract.View.OnAdapterItemClickListener, AddContainerContract.View {
    private lateinit var addContainerPresenter: AddContainerPresenter
    private lateinit var addOutBoundContainerAdapter: AddContainerAdapter
    private lateinit var addLiveContainerAdapter: AddContainerAdapter
    private lateinit var addDropContainerAdapter: AddContainerAdapter

    private var outBoundList: ArrayList<ContainerDetails> = ArrayList()
    private var liveLoadList: ArrayList<ContainerDetails> = ArrayList()
    private var dropOffList: ArrayList<ContainerDetails> = ArrayList()

    companion object {
        const val ADD_CONTAINER_OUTBOUND_LIST = "ADD_CONTAINER_OUTBOUND_LIST"
        const val ADD_CONTAINER_LIVE_LOAD_LIST = "ADD_CONTAINER_LIVE_LOAD_LIST"
        const val ADD_CONTAINER_DROP_OFF_LIST = "ADD_CONTAINER_DROP_OFF_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_container)
        setUpCustomToolbar()

        initializeUI()
        addContainerPresenter = AddContainerPresenter(this, resources)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(ADD_CONTAINER_OUTBOUND_LIST)) {
                outBoundList = savedInstanceState.getParcelableArrayList(ADD_CONTAINER_OUTBOUND_LIST)!!

            }
            if (savedInstanceState.containsKey(ADD_CONTAINER_LIVE_LOAD_LIST)) {
                liveLoadList = savedInstanceState.getParcelableArrayList(ADD_CONTAINER_LIVE_LOAD_LIST)!!

            }
            if (savedInstanceState.containsKey(ADD_CONTAINER_DROP_OFF_LIST)) {
                dropOffList = savedInstanceState.getParcelableArrayList(ADD_CONTAINER_DROP_OFF_LIST)!!

            }
            saveInstanceStateDataSet()
        } ?: run {

        }

    }

    private fun setUpCustomToolbar() {
        textViewToolbar.text= getString(R.string.add_container)
        textSubHeader.text= getString(R.string.add_container_header_message)
        headerBackImage.setOnClickListener(this)
    }

    fun initializeUI() {
        recyclerViewOutBound.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            addOutBoundContainerAdapter = AddContainerAdapter(
                    this@AddContainerActivity,
                    resources,
                    context
            )
            adapter = addOutBoundContainerAdapter
        }

        recyclerViewLiveLode.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            addLiveContainerAdapter = AddContainerAdapter(
                    this@AddContainerActivity,
                    resources,
                    context
            )
            adapter = addLiveContainerAdapter
        }

        recyclerViewDrop.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            addDropContainerAdapter = AddContainerAdapter(
                    this@AddContainerActivity,
                    resources,
                    context
            )
            adapter = addDropContainerAdapter
        }

        updateUiVisibility()
        buttonAdd.setOnClickListener(this)
        textViewAddOutBound.setOnClickListener(this)
        textViewAddLiveLode.setOnClickListener(this)
        textViewAddDrop.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (!outBoundList.isNullOrEmpty())
            outState.putParcelableArrayList(ADD_CONTAINER_OUTBOUND_LIST, outBoundList)
        if (!liveLoadList.isNullOrEmpty())
            outState.putParcelableArrayList(ADD_CONTAINER_LIVE_LOAD_LIST, liveLoadList)
        if (!dropOffList.isNullOrEmpty())
            outState.putParcelableArrayList(ADD_CONTAINER_DROP_OFF_LIST, dropOffList)
        super.onSaveInstanceState(outState)
    }

    private fun saveInstanceStateDataSet() {
        addOutBoundContainerAdapter.addContainerData(WORKSHEET_WORK_ITEM_OUTBOUND, outBoundList)
        addLiveContainerAdapter.addContainerData(WORKSHEET_WORK_ITEM_LIVE, liveLoadList)
        addDropContainerAdapter.addContainerData(WORKSHEET_WORK_ITEM_INBOUND, dropOffList)
        updateUiVisibility()
    }

    private fun validateOutBoundData(): Boolean {
        var outboundValid = true
        if (outBoundList.size > 0) {
            outBoundList.forEach {
                if (it.startTime.isNullOrEmpty() || it.sequence.isNullOrEmpty())
                    outboundValid = false
            }
        }

        return outboundValid
    }

    private fun validateLiveLoadData(): Boolean {
        var liveLoadValid = true
        if (liveLoadList.size > 0) {
            liveLoadList.forEach {
                if (it.startTime.isNullOrEmpty() || it.sequence.isNullOrEmpty())
                    liveLoadValid = false
            }
        }

        return liveLoadValid
    }

    private fun validateDropData(): Boolean {
        var dropValid = true
        if (dropOffList.size > 0) {
            dropOffList.forEach {
                if (it.startTime.isNullOrEmpty() || it.numberOfDrops.isNullOrEmpty())
                    dropValid = false
            }
        }

        return dropValid
    }

    private fun updateUiVisibility() {
        val leadProfile = DateUtils.sharedPref.getClassObject(
            AppConstant.PREFERENCE_LEAD_PROFILE,
            LeadProfileData::class.java
        ) as LeadProfileData?
        leadProfile?.department?.let {
            when (it) {
                AppConstant.EMPLOYEE_DEPARTMENT_INBOUND -> {
                    layoutOutBound.visibility = View.GONE
                    recyclerViewOutBound.visibility = View.GONE
                    if (!dropOffList.isNullOrEmpty() || !liveLoadList.isNullOrEmpty()) {
                        buttonAdd.isEnabled = checkLiveLoad() && checkDropOff()
                        isDataSave(false)
                    } else {
                        buttonAdd.isEnabled = false
                        isDataSave(true)
                    }
                    textViewAddDrop.isEnabled = dropOffList.size <= 0
                }
                AppConstant.EMPLOYEE_DEPARTMENT_OUTBOUND -> {
                    layoutLiveLode.visibility = View.GONE
                    recyclerViewLiveLode.visibility = View.GONE
                    layoutDrop.visibility = View.GONE
                    recyclerViewDrop.visibility = View.GONE
                    if (!outBoundList.isNullOrEmpty()) {
                        buttonAdd.isEnabled = checkOutBound()
                        isDataSave(false)
                    } else {
                        buttonAdd.isEnabled = false
                        isDataSave(true)
                    }
                    textViewAddDrop.isEnabled = dropOffList.size <= 0
                }
                AppConstant.EMPLOYEE_DEPARTMENT_BOTH -> {
                    if (!outBoundList.isNullOrEmpty() || !dropOffList.isNullOrEmpty() || !liveLoadList.isNullOrEmpty()) {
                        buttonAdd.isEnabled = checkOutBound() && checkLiveLoad() && checkDropOff()
                        isDataSave(false)
                    } else {
                        buttonAdd.isEnabled = false
                        isDataSave(true)
                    }
                    textViewAddDrop.isEnabled = dropOffList.size <= 0
                }
            }
        }

    }

    private fun checkDropOff(): Boolean {
        var isDropOffChecked=false
        if (dropOffList.size>0){
//            dropOffList.forEach{
//                if (it.numberOfDrops.isNullOrEmpty() || it.startTime.isNullOrEmpty()){
//                    isDropOffChecked= false
//                    return@forEach
//
//                }else isDropOffChecked=true
////                isDropOffChecked = !(it.numberOfDrops.isNullOrEmpty() || it.startTime.isNullOrEmpty())
//            }
            for (dropOffData in dropOffList) {
                if (dropOffData.numberOfDrops.isNullOrEmpty() || dropOffData.startTime.isNullOrEmpty()){
                    isDropOffChecked= false
                    break

                }else isDropOffChecked=true
            }
        } else isDropOffChecked=true
        return isDropOffChecked
    }

    private fun checkLiveLoad(): Boolean {
        var liveLoadChecked=false
        if (liveLoadList.size>0){
//            liveLoadList.forEach{
//
//                if (it.numberOfDrops.isNullOrEmpty() || it.startTime.isNullOrEmpty()){
//                    liveLoadChecked= false
//                    return@forEach
//
//                }else liveLoadChecked=true
////                liveLoadChecked = !(it.sequence.isNullOrEmpty() || it.startTime.isNullOrEmpty())
//            }

            for (liveLoad in liveLoadList) {
                if (liveLoad.sequence.isNullOrEmpty() || liveLoad.startTime.isNullOrEmpty()){
                    liveLoadChecked= false
                    break

                }else liveLoadChecked=true
            }
        }else liveLoadChecked=true
        return liveLoadChecked
    }

    private fun checkOutBound(): Boolean {
        var outBoundChecked= false
        if (outBoundList.size>0){
//            outBoundList.forEach{
//
//                if (it.numberOfDrops.isNullOrEmpty() || it.startTime.isNullOrEmpty()){
//                    outBoundChecked= false
//                    return@forEach
//
//                }else outBoundChecked=true
//
////                outBoundChecked = !(it.sequence.isNullOrEmpty() || it.startTime.isNullOrEmpty())
//            }

            for (outBoundData in outBoundList) {
                if (outBoundData.sequence.isNullOrEmpty() || outBoundData.startTime.isNullOrEmpty()){
                    outBoundChecked= false
                    break

                }else outBoundChecked=true
            }
        }else outBoundChecked= true

      return outBoundChecked
    }

    private fun addContainer() {
        if (validateOutBoundData() && validateLiveLoadData() && validateDropData()) {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }
            val scheduleNote=editTextScheduleNote.text.toString()
            CustomerDialog.showAddNoteDialog(activity, "ADD", outBoundList, liveLoadList, dropOffList, object : CustomerDialog.IDialogOnClick {
                override fun onSendRequest(dialog: Dialog) {
                    addContainerPresenter.addTodayWorkContainer(outBoundList, liveLoadList, dropOffList,scheduleNote)
                    dialog.dismiss()
                }
            })
        } else {
            CustomProgressBar.getInstance().showErrorWarningDialog(getString(R.string.add_container_error_message), activity)
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                buttonAdd.id -> {
                    addContainer()
                }
                textViewAddOutBound.id -> {
                    val containerDetails = ContainerDetails()
                    containerDetails.workItemType = WORKSHEET_WORK_ITEM_OUTBOUND
                    containerDetails.sequence = 1.toString()
                    outBoundList.add(containerDetails)
                    addOutBoundContainerAdapter.addContainerData(
                            WORKSHEET_WORK_ITEM_OUTBOUND, outBoundList
                    )
                    updateUiVisibility()
                }
                textViewAddLiveLode.id -> {

                    val containerDetails = ContainerDetails()
                    containerDetails.workItemType = WORKSHEET_WORK_ITEM_LIVE
                    containerDetails.sequence = 1.toString()
                    liveLoadList.add(containerDetails)
                    addLiveContainerAdapter.addContainerData(
                            WORKSHEET_WORK_ITEM_LIVE,
                            liveLoadList
                    )
                    updateUiVisibility()
                }
                textViewAddDrop.id -> {
                    val containerDetails = ContainerDetails()
                    containerDetails.workItemType = WORKSHEET_WORK_ITEM_INBOUND
                    dropOffList.add(containerDetails)
                    addDropContainerAdapter.addContainerData(
                            WORKSHEET_WORK_ITEM_INBOUND,
                            dropOffList
                    )
                    updateUiVisibility()
                }
                headerBackImage.id -> {
                    super.onBackPressed()
                }
            }
        }

    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun addWorkScheduleFinished(message: String) {
        CustomProgressBar.getInstance().showSuccessDialog(message, activity, object :
            CustomDialogListener {
            override fun onConfirmClick() {
                setResult(RESULT_OK)
                isDataSave(true)
                onBackPressed()
            }
        })

    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */

    override fun removeItemFromList(position: Int, item: ContainerDetails) {
        item.workItemType.let {
            when (it){
                WORKSHEET_WORK_ITEM_OUTBOUND -> {
                    outBoundList.removeAt(position)
                }
                WORKSHEET_WORK_ITEM_LIVE -> {
                    liveLoadList.removeAt(position)
                }
                WORKSHEET_WORK_ITEM_INBOUND -> {
                    dropOffList.removeAt(position)
                }
                else -> {}
            }
        }

        updateUiVisibility()
    }

    override fun addQuantity(adapterPosition: Int, item: ContainerDetails, quantity: String) {
        item.workItemType.let {
            when (it){
                WORKSHEET_WORK_ITEM_OUTBOUND -> {
                    outBoundList[adapterPosition].sequence = quantity
                }
                WORKSHEET_WORK_ITEM_LIVE -> {
                    liveLoadList[adapterPosition].sequence = quantity
                }
                WORKSHEET_WORK_ITEM_INBOUND -> {
                    dropOffList[adapterPosition].numberOfDrops = quantity
                }
                else -> {}
            }
        }

        updateUiVisibility()
    }

    override fun addTimeInList(position: Int, item: ContainerDetails, timeInMilli1: String) {
        item.workItemType.let {
            when (it){
                WORKSHEET_WORK_ITEM_OUTBOUND -> {
                    outBoundList[position].startTime = timeInMilli1
                }
                WORKSHEET_WORK_ITEM_LIVE -> {
                    liveLoadList[position].startTime = timeInMilli1
                }
                WORKSHEET_WORK_ITEM_INBOUND -> {
                    dropOffList[position].startTime = timeInMilli1
                }
                else -> {}
            }
        }

        updateUiVisibility()
    }
}