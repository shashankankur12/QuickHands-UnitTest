package com.quickhandslogistics.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.layout_header.*

class CompleteLumperJobHistoryDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_lumper_job_history_details)

        text_title.text = getString(R.string.string_lumper_details)

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }
    }
}
