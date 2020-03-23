package com.quickhandslogistics.view.activities

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.view.adapter.AssignedLumperAdapter
import kotlinx.android.synthetic.main.activity_work_item_lumpers.*

class WorkItemLumpersActivity : BaseActivity() {

    companion object {
        const val ARG_CAN_REPLACE = "ARG_CAN_REPLACE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_item_lumpers)
        setupToolbar("Container : ICNU7869687")

        intent.extras?.let { it ->
            if (it.containsKey(ARG_CAN_REPLACE)) {
                val canReplace = it.getBoolean(ARG_CAN_REPLACE, true)

                recycler_lumpers.apply {
                    val linearLayoutManager = LinearLayoutManager(this@WorkItemLumpersActivity)
                    layoutManager = linearLayoutManager
                    val dividerItemDecoration =
                        DividerItemDecoration(this@WorkItemLumpersActivity, linearLayoutManager.orientation)
                    addItemDecoration(dividerItemDecoration)
                    adapter = AssignedLumperAdapter(this@WorkItemLumpersActivity, canReplace)
                }
            }
        }
    }
}
