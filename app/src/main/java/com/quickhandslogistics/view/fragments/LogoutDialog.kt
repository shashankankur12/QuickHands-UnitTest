package com.quickhandslogistics.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.activities.LoginActivity
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_IS_ACTIVE
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_AUTH_TOKEN
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.layout_dialog.*

class LogoutDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        text_yes.setOnClickListener {
            SharedPref.getInstance().setString(PREFERENCE_AUTH_TOKEN, "")
            SharedPref.getInstance().setBoolean(PREFERENCE_IS_ACTIVE, false)

            Toast.makeText(
                this.requireContext(),
                "you have successfully logged out",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this.requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dismiss()
        }

        text_no.setOnClickListener {
            dismiss()
        }
    }
}