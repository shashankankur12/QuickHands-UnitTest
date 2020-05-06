package com.quickhandslogistics.modified.views.lumperSheet

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.common.AddSignatureActivity
import com.quickhandslogistics.modified.adapters.lumperSheet.LumperWorkDetailAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import kotlinx.android.synthetic.main.activity_lumper_work_detail.*

class LumperWorkDetailActivity : BaseActivity(), View.OnClickListener,
    LumperWorkDetailContract.View.OnAdapterItemClickListener {

    private var employeeData: EmployeeData? = null

    private lateinit var lumperWorkDetailAdapter: LumperWorkDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )*/
        setContentView(R.layout.activity_lumper_work_detail)
        setupToolbar(getString(R.string.lumper_work_detail))

        intent.extras?.let { it ->
            if (it.containsKey(ARG_LUMPER_DATA)) {
                employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
            }
        }

        initializeUI()
    }

    private fun initializeUI() {
        employeeData?.let { employeeData ->
            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                Glide.with(activity).load(employeeData.profileImageUrl)
                    .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                    .into(circleImageViewProfile)
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
            lumperWorkDetailAdapter =
                LumperWorkDetailAdapter(resources, this@LumperWorkDetailActivity)
            adapter = lumperWorkDetailAdapter
        }

        textViewAddSignature.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> {
                    startIntent(AddSignatureActivity::class.java)
                }
            }
        }
    }

    override fun onBOItemClick(
        buildingOps: HashMap<String, String>, parameters: ArrayList<String>
    ) {
        val bundle = Bundle()
        bundle.putStringArrayList(ScheduleMainFragment.ARG_BUILDING_PARAMETERS, parameters)
        bundle.putSerializable(ScheduleMainFragment.ARG_BUILDING_PARAMETER_VALUES, buildingOps)
        startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
    }

    override fun onNotesItemClick(notes: String?) {
        notes?.let {
            CustomProgressBar.getInstance().showInfoDialog(
                getString(R.string.string_note), notes, activity
            )
        }
    }
}
