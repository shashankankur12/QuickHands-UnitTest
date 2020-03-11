package com.quickhandslogistics.view.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_lumper_details.*
import kotlinx.android.synthetic.main.layout_header.*

class LumperDetailsActivity : AppCompatActivity() {
    private var mLumperData: LumperData? = null

    companion object {
        const val ARG_LUMPER_DATA = "ARG_LUMPER_DATA"
    }

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
        if (intent.extras?.containsKey(ARG_LUMPER_DATA)!!) {
            mLumperData = intent.extras?.getSerializable(ARG_LUMPER_DATA) as LumperData

            if (mLumperData != null)
                setLumperDetail(mLumperData!!)
        }

    }

    private fun setLumperDetail(lumperdata: LumperData) {

        if (!TextUtils.isEmpty(lumperdata.firstName) && !TextUtils.isEmpty(lumperdata.lastName))
            text_name.setText(lumperdata.firstName?.capitalize() + " " + lumperdata.lastName?.capitalize())
        if (!TextUtils.isEmpty(lumperdata.email))
            edit_email.setText(lumperdata.email)
        if (!TextUtils.isEmpty(lumperdata.phone))
            edit_phone.setText(lumperdata.phone)
        if (!TextUtils.isEmpty(lumperdata.role))
            edit_role.setText(lumperdata.role.capitalize())
        if (!TextUtils.isEmpty(lumperdata.created_at))
            edit_Created_at.setText(Utils.convertLocalToUTC(lumperdata.created_at))
        if (!TextUtils.isEmpty(lumperdata.updated_at))
            edit_updated_at.setText(Utils.convertLocalToUTC(lumperdata.updated_at))
        if (!TextUtils.isEmpty(lumperdata.firstName) && !TextUtils.isEmpty(lumperdata.lastName))
            edit_sign.setText(lumperdata.firstName?.capitalize() + " " + lumperdata.lastName?.capitalize())
    }
}
