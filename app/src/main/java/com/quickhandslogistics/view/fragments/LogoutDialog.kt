package com.quickhandslogistics.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.LoginActivity

class LogoutDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_logout, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = Dialog(this.requireContext())
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.layout_dialog)

        var textYes = dialog.findViewById<TextView>(R.id.text_yes)
        var textNo = dialog.findViewById<TextView>(R.id.text_no)

        textYes.setOnClickListener {
            dialog.dismiss()

          Toast.makeText(this.requireContext(),"you have successfully logged out",Toast.LENGTH_SHORT).show()

            val intent = Intent(this.requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        textNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog .show()
    }
}