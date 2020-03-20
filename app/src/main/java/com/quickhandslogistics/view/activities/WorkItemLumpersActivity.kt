package com.quickhandslogistics.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.AssignedLumperAdapter
import kotlinx.android.synthetic.main.activity_work_item_lumpers.*
import kotlinx.android.synthetic.main.layout_header.*


class WorkItemLumpersActivity : AppCompatActivity() {

    companion object {
        const val ARG_CAN_REPLACE = "ARG_CAN_REPLACE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_item_lumpers)

        text_title.text = getString(R.string.string_lumper)

        image_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        intent.extras?.let { it ->
            if (it.containsKey(ARG_CAN_REPLACE)) {
                val canReplace = it.getBoolean(ARG_CAN_REPLACE, true)

                recycler_lumpers.layoutManager = LinearLayoutManager(this)
                recycler_lumpers.adapter = AssignedLumperAdapter(this, canReplace)
            }
        }
    }
}
