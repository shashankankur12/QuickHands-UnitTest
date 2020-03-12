package com.quickhandslogistics.view.activities

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.CustomToolBarHelper

open class BaseActivity : AppCompatActivity() {

//    protected fun setToolBar(activity: Activity, title: String, backPress: Boolean) {
//        val customToolBarHelper = CustomToolBarHelper(activity)
//        customToolBarHelper.setTitle(title)
//        if (backPress) customToolBarHelper.enableBackPress()
//    }
//
//    protected fun setToolBar(activity: Activity, title: String, backPress: Boolean, changeicon: Boolean) {
//        val customToolBarHelper = CustomToolBarHelper(activity)
//        customToolBarHelper.setTitle(title)
//
//        if (backPress) customToolBarHelper.enableBackPress()
//
//        if (changeicon)
//        customToolBarHelper.setLogoIcon(R.drawable.ic_cancel_black_24dp)
//    }

    protected fun setGroupToolbar(activity: Activity?, title: String, subtitle: String) {
        if (activity == null) return
        val textTitle = activity.findViewById<TextView>(R.id.text_title)
        //val textSubtitle = activity.findViewById<TextView>(R.id.text_subtitle)
        val imageBack = activity.findViewById<ImageView>(R.id.image_back)
        imageBack.setOnClickListener {v ->
            activity.onBackPressed()
        }

        textTitle.text = title
      //  textSubtitle.text = subtitle
       // textSubtitle.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
    }

    fun setToolbarReset(activity: Activity?, title: String, visible: Boolean) {
        if (activity == null) return
        val textTitle = activity.findViewById<TextView>(R.id.text_title)
        val imageBack = activity.findViewById<ImageView>(R.id.image_back)
        val linearLayout = activity.findViewById<LinearLayout>(R.id.view_toolbar)

        imageBack.setOnClickListener { v ->
            activity.onBackPressed()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }
        textTitle.setText(title)

        if(!visible)  linearLayout.visibility = View.INVISIBLE
    }

     fun startNewActivity(activity: AppCompatActivity, givenclas: Class<*>) {
        val intent = Intent(activity, givenclas)
        startActivity(intent)
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
    }

    fun startActivityWithFlags(activity: AppCompatActivity, givenclas: Class<*>) {
        val intent = Intent(activity, givenclas)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
    }
}
