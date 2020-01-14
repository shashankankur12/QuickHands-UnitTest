package com.quickhandslogistics.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.layout_header.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        text_title.text = getString(R.string.string_profile)

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }
    }
}
