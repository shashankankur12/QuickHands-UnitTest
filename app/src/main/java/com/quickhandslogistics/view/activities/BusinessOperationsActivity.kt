package com.quickhandslogistics.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.activity_business_operations.*
import kotlinx.android.synthetic.main.layout_header.*

class BusinessOperationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_operations)

        text_title.text = getString(R.string.building_operations)

        image_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        button_submit.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }
    }
}
