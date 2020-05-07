package com.quickhandslogistics.modified.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.lumperSheet.LumperWorkDetailAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperWorkDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.AddSignatureActivity
import com.quickhandslogistics.modified.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.activity_lumper_work_detail.*
import java.io.File
import java.util.*

class LumperWorkDetailActivity : BaseActivity(), View.OnClickListener, LumperWorkDetailContract.View,
    LumperWorkDetailContract.View.OnAdapterItemClickListener {

    private var signatureFilePath = ""
    private var selectedTime: Long = 0
    private var employeeData: EmployeeData? = null

    private lateinit var lumperWorkDetailPresenter: LumperWorkDetailPresenter
    private lateinit var lumperWorkDetailAdapter: LumperWorkDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )*/
        setContentView(R.layout.activity_lumper_work_detail)
        setupToolbar(getString(R.string.lumper_work_detail))

        intent.extras?.let { bundle ->
            if (bundle.containsKey(ARG_LUMPER_DATA)) {
                employeeData = bundle.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
                selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            }
        }

        initializeUI()

        lumperWorkDetailPresenter = LumperWorkDetailPresenter(this, resources)
        lumperWorkDetailPresenter.getLumperWorkDetails(employeeData?.id, Date(selectedTime))
    }

    private fun initializeUI() {
        employeeData?.let { employeeData ->
            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                Glide.with(activity).load(employeeData.profileImageUrl).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(circleImageViewProfile)
            } else {
                Glide.with(activity).clear(circleImageViewProfile);
            }

            textViewLumperName.text = String.format(
                "%s %s",
                ValueUtils.getDefaultOrValue(employeeData.firstName),
                ValueUtils.getDefaultOrValue(employeeData.lastName)
            )

            if (StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            }
        }

        recyclerViewLumperWork.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            lumperWorkDetailAdapter = LumperWorkDetailAdapter(resources, this@LumperWorkDetailActivity)
            adapter = lumperWorkDetailAdapter
        }

        textViewAddSignature.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> {
                    startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                }
                buttonSave.id -> {
                    CustomProgressBar.getInstance().showWarningDialog(
                        activityContext = activity, listener = object : CustomDialogWarningListener {
                            override fun onConfirmClick() {
                                lumperWorkDetailPresenter.saveLumperSignature(employeeData?.id!!, Date(selectedTime), signatureFilePath)
                            }

                            override fun onCancelClick() {
                            }
                        })
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.let {
                val signatureFilePath = data.getStringExtra(AddSignatureActivity.ARG_SIGNATURE_FILE_PATH)
                showLocalSignatureOnUI(signatureFilePath)
            }
        }
    }

    private fun showLocalSignatureOnUI(signatureFilePath: String?) {
        if (!signatureFilePath.isNullOrEmpty()) {
            this.signatureFilePath = signatureFilePath
            Glide.with(activity).load(File(signatureFilePath)).into(imageViewSignature)
            imageViewSignature.visibility = View.VISIBLE
            textViewAddSignature.visibility = View.GONE
            buttonSave.visibility = View.VISIBLE
        } else {
            this.signatureFilePath = ""
            imageViewSignature.visibility = View.GONE
            textViewAddSignature.visibility = View.VISIBLE
            buttonSave.visibility = View.GONE
        }
    }

    override fun onBOItemClick(buildingOps: HashMap<String, String>, parameters: ArrayList<String>) {
        val bundle = Bundle()
        bundle.putStringArrayList(ScheduleMainFragment.ARG_BUILDING_PARAMETERS, parameters)
        bundle.putSerializable(ScheduleMainFragment.ARG_BUILDING_PARAMETER_VALUES, buildingOps)
        startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
    }

    override fun onNotesItemClick(notes: String?) {
        notes?.let {
            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.string_note), notes, activity)
        }
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumperWorkDetails(employeeDataList: java.util.ArrayList<EmployeeData>) {
        buttonSave.visibility = View.GONE
    }
}
