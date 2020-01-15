
package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.layout_header.*

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }
    }
}


