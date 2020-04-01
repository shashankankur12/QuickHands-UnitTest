package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.BuildingOperationsAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import kotlinx.android.synthetic.main.content_building_operations.*

class BuildingOperationsActivity : BaseActivity(), View.OnClickListener {

    private var allowUpdate: Boolean = true

    companion object {
        const val ARG_ALLOW_UPDATE = "ARG_ALLOW_UPDATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_operations)
        setupToolbar(getString(R.string.building_operations))

        intent.extras?.let { it ->
            if (it.containsKey(ARG_ALLOW_UPDATE)) {
                allowUpdate = it.getBoolean(ARG_ALLOW_UPDATE, true)
            }
        }

        buttonSubmit.visibility = if (allowUpdate) View.VISIBLE else View.GONE
        buttonSubmit.setOnClickListener(this)

        recyclerViewBuildingOperations.apply {
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(SpaceDividerItemDecorator(20, 20))
            adapter =
                BuildingOperationsAdapter(
                    allowUpdate
                )
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> {
                    onBackPressed()
                }
            }
        }
    }
}
