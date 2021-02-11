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
import com.quickhandslogistics.presenters.addContainer.AddContainerPresenter
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_INBOUND
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_LIVE
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_OUTBOUND
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.CustomeDialog
import com.quickhandslogistics.utils.SnackBarFactory
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
            val dividerItemDecoration = DividerItemDecoration(
                    activity,
                    linearLayoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)
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
            val dividerItemDecoration = DividerItemDecoration(
                    activity,
                    linearLayoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)
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
            val dividerItemDecoration = DividerItemDecoration(
                    activity,
                    linearLayoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)
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
        if (!outBoundList.isNullOrEmpty() || !dropOffList.isNullOrEmpty() || !liveLoadList.isNullOrEmpty()) {
            buttonAdd.isEnabled = checkOutBound()&& checkLiveLoad()&& checkDropOff()
            isDataSave(false)
        } else {
            buttonAdd.isEnabled = false
            isDataSave(true)
        }
        textViewAddDrop.isEnabled = dropOffList.size <= 0
    }

    private fun checkDropOff(): Boolean {
        var isDropOffChecked=false
        if (dropOffList.size>0){
            dropOffList.forEach{
                isDropOffChecked = !(it.numberOfDrops.isNullOrEmpty() || it.startTime.isNullOrEmpty())
            }
        } else isDropOffChecked=true
        return isDropOffChecked
    }

    private fun checkLiveLoad(): Boolean {
        var liveLoadChecked=false
        if (liveLoadList.size>0){
            liveLoadList.forEach{
                liveLoadChecked = !(it.sequence.isNullOrEmpty() || it.startTime.isNullOrEmpty())
            }
        }else liveLoadChecked=true
        return liveLoadChecked
    }

    private fun checkOutBound(): Boolean {
        var outBoundChecked= false
        if (outBoundList.size>0){
            outBoundList.forEach{
                outBoundChecked = !(it.sequence.isNullOrEmpty() || it.startTime.isNullOrEmpty())
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
            CustomeDialog.showAddNoteDialog(activity, "ADD", outBoundList, liveLoadList, dropOffList, object : CustomeDialog.IDialogOnClick {
                override fun onSendRequest(dialog: Dialog) {
                    addContainerPresenter.addTodayWorkContainer(outBoundList, liveLoadList, dropOffList)
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
                    containerDetails.isDisabled = true
                    containerDetails.readonlypermission = true
                    containerDetails.workItemType = WORKSHEET_WORK_ITEM_OUTBOUND
                    outBoundList.add(containerDetails)
                    addOutBoundContainerAdapter.addContainerData(
                            WORKSHEET_WORK_ITEM_OUTBOUND, outBoundList
                    )
                    updateUiVisibility()
                }
                textViewAddLiveLode.id -> {

                    val containerDetails = ContainerDetails()
                    containerDetails.isDisabled = true
                    containerDetails.readonlypermission = true
                    containerDetails.workItemType = WORKSHEET_WORK_ITEM_LIVE
                    liveLoadList.add(containerDetails)
                    addLiveContainerAdapter.addContainerData(
                            WORKSHEET_WORK_ITEM_LIVE,
                            liveLoadList
                    )
                    updateUiVisibility()
                }
                textViewAddDrop.id -> {
                    val containerDetails = ContainerDetails()
                    containerDetails.isDisabled = true
                    containerDetails.readonlypermission = true
                    containerDetails.workItemType = WORKSHEET_WORK_ITEM_INBOUND
                    dropOffList.add(containerDetails)
                    addDropContainerAdapter.addContainerData(
                            WORKSHEET_WORK_ITEM_INBOUND,
                            dropOffList
                    )
                    updateUiVisibility()
                }
                headerBackImage.id->{super.onBackPressed()}
            }
        }

    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun addWorkScheduleFinished() {
        setResult(RESULT_OK)
        isDataSave(true)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */

    override fun removeItemFromList(position: Int, item: ContainerDetails) {
        item.workItemType.let {
            when (it){
                WORKSHEET_WORK_ITEM_OUTBOUND->{outBoundList.removeAt(position)}
                WORKSHEET_WORK_ITEM_LIVE->{liveLoadList.removeAt(position)}
                WORKSHEET_WORK_ITEM_INBOUND->{dropOffList.removeAt(position)}
                else -> {}
            }
        }

        updateUiVisibility()
    }

    override fun addQuantity(adapterPosition: Int, item: ContainerDetails, quantity: String) {
        item.workItemType.let {
            when (it){
                WORKSHEET_WORK_ITEM_OUTBOUND->{outBoundList [adapterPosition].sequence=quantity}
                WORKSHEET_WORK_ITEM_LIVE->{liveLoadList[adapterPosition].sequence=quantity}
                WORKSHEET_WORK_ITEM_INBOUND->{dropOffList[adapterPosition].numberOfDrops=quantity}
                else -> {}
            }
        }

        updateUiVisibility()
    }

    override fun addTimeInList(position: Int, item: ContainerDetails, timeInMilli1: String) {
        item.workItemType.let {
            when (it){
                WORKSHEET_WORK_ITEM_OUTBOUND->{outBoundList [position].startTime=timeInMilli1}
                WORKSHEET_WORK_ITEM_LIVE->{liveLoadList[position].startTime=timeInMilli1}
                WORKSHEET_WORK_ITEM_INBOUND->{dropOffList[position].startTime=timeInMilli1}
                else -> {}
            }
        }

        updateUiVisibility()
    }
}