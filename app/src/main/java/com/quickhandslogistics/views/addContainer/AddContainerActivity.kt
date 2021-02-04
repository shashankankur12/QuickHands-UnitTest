package com.quickhandslogistics.views.addContainer


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.addContainer.AddContainerAdapter
import com.quickhandslogistics.contracts.addContainer.AddContainerContract
import com.quickhandslogistics.presenters.addContainer.AddContainerPresenter
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_INBOUND
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_LIVE
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_OUTBOUND
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.activity_add_container.*
import kotlinx.android.synthetic.main.activity_add_container.mainConstraintLayout


class AddContainerActivity : BaseActivity(), View.OnClickListener, AddContainerContract.View.OnAdapterItemClickListener, AddContainerContract.View {
    private lateinit var addContainerPresenter:AddContainerPresenter
    private lateinit var addOutBoundContainerAdapter: AddContainerAdapter
    private lateinit var addLiveContainerAdapter: AddContainerAdapter
    private lateinit var addDropContainerAdapter: AddContainerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_container)
        setupToolbar(getString(R.string.add_container))

        initializeUI()
        addContainerPresenter= AddContainerPresenter(this, resources)

    }
    fun initializeUI() {
        recyclerViewOutBound.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addOutBoundContainerAdapter = AddContainerAdapter(this@AddContainerActivity, resources,context)
            adapter = addOutBoundContainerAdapter
        }

        recyclerViewLiveLode.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
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
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addDropContainerAdapter = AddContainerAdapter(
                this@AddContainerActivity,
                resources,
                context
            )
            adapter = addDropContainerAdapter
        }

        addOutBoundContainerAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })


        buttonAdd.setOnClickListener(this)
        textViewAddOutBound.setOnClickListener(this)
        textViewAddLiveLode.setOnClickListener(this)
        textViewAddDrop.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (addOutBoundContainerAdapter.getContainerList().size>0) {
            if ( addOutBoundContainerAdapter.getContainerStartTime())
                buttonAdd.isEnabled=false
            else
                buttonAdd.isEnabled=true
//            isDataSave(false)
        }else{
            buttonAdd.isEnabled=false
//            isDataSave(true)

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
                buttonAdd.id -> {  }
                textViewAddOutBound.id -> {addOutBoundContainerAdapter.addContainerData(WORKSHEET_WORK_ITEM_OUTBOUND) }
                textViewAddLiveLode.id -> {addLiveContainerAdapter.addContainerData(WORKSHEET_WORK_ITEM_LIVE)  }
                textViewAddDrop.id->{ addDropContainerAdapter.addContainerData(WORKSHEET_WORK_ITEM_INBOUND) }
            }
        }

    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun cancellingWorkScheduleFinished() {

    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onLumperSelectionChanged() {

    }
}