package com.quickhandslogistics.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.model.lumper.LumperData
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_lumper_details.*
import kotlinx.android.synthetic.main.layout_header.*

class LumperDetailsActivity : AppCompatActivity() {
    private var mLumperData: LumperData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_details)

       // image_profile.visibility = View.VISIBLE
        text_title.text = getString(R.string.string_lumper_details)
        button_submit.visibility = View.GONE

        image_back.setOnClickListener {
            Utils.finishActivity(this)
        }
        getIntentValue()

        /*image_profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
        }*/
    }

    private fun getIntentValue() {
        if (intent.hasExtra("lumperData")) {
            mLumperData = intent.getSerializableExtra("lumperData") as LumperData

            if(mLumperData != null)
            setLumperDetail(mLumperData!!)
        }

    }

    private fun setLumperDetail(lumperdata : LumperData){

        text_name.setText(lumperdata.firstName.capitalize() + " " +lumperdata.lastName.capitalize())
        edit_email.setText(lumperdata.email)
        edit_phone.setText(lumperdata.phone)
        edit_role.setText(lumperdata.role.capitalize())
        edit_Created_at.setText(Utils.convertLocalToUTC(lumperdata.created_at))
        edit_updated_at.setText(Utils.convertLocalToUTC(lumperdata.updated_at))
        edit_sign.setText(lumperdata.firstName.capitalize() + " " +lumperdata.lastName.capitalize())
    }
}
