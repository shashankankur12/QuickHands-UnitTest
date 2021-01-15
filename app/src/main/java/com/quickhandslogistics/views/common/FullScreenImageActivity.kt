package com.quickhandslogistics.views.common

import android.os.Bundle
import android.view.View
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.UIUtils
import com.quickhandslogistics.views.BaseActivity
import kotlinx.android.synthetic.main.activity_full_screen_image.*

class FullScreenImageActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val ARG_IMAGE_URL = "ARG_IMAGE_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        intent.extras?.let { bundle ->
            if (bundle.containsKey(ARG_IMAGE_URL)) {
                val imageUrl = bundle.getString(ARG_IMAGE_URL)
                UIUtils.showEmployeeProfileImage(activity, imageUrl, imageViewProfileImage)
            }
        }

        imageViewBack.setOnClickListener(this)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewBack.id -> {
                    onBackPressed()
                }
            }
        }
    }
}
