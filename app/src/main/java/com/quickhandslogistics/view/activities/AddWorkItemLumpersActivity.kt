package com.quickhandslogistics.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.AddLumperAdapter
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*
import kotlinx.android.synthetic.main.layout_header.*


class AddWorkItemLumpersActivity : AppCompatActivity() {

    private var position: Int = 0
    private lateinit var adapter: AddLumperAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_item_lumpers)

        position = intent.getIntExtra("position", 0)

        text_title.text = getString(R.string.add_lumpers)

        image_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        recycler_lumpers.layoutManager = LinearLayoutManager(this)
        adapter = AddLumperAdapter(this)
        recycler_lumpers.adapter = adapter

        button_submit.setOnClickListener {
            val addedLumpers = adapter.getSelectedLumper()
            if (addedLumpers.size > 0) {
                val intent = Intent()
                intent.putExtra("position", position)
                intent.putExtra("count", addedLumpers.size)
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
            }
        }
    }
}
