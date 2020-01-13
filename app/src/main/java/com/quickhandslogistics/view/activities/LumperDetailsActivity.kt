package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.layout_header.*

class LumperDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_details)
        
        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }

        text_title.text = getString(R.string.string_lumper_details)
    }
}
