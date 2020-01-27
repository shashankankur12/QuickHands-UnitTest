package com.quickhandslogistics.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.quickhandslogistics.R

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
            this.requireActivity().finish()
        }

        textNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog .show()
    }
}