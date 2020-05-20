package com.quickhandslogistics.modified.views.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.common.InfoDialogContract
import kotlinx.android.synthetic.main.dialog_fragment_info.*

class InfoDialogFragment(private val onClickListener: InfoDialogContract.View.OnClickListener) : DialogFragment(), View.OnClickListener {

    private var message: String = ""
    private var positiveButtonText: String = ""
    private var showInfoIcon: Boolean = true

    companion object {
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_POSITIVE_BUTTON_TEXT = "ARG_POSITIVE_BUTTON_TEXT"
        private const val ARG_SHOW_INFO_ICON = "ARG_SHOW_INFO_ICON"

        @JvmStatic
        fun newInstance(
            message: String, positiveButtonText: String = "", showInfoIcon: Boolean = true,
            onClickListener: InfoDialogContract.View.OnClickListener
        ) = InfoDialogFragment(onClickListener).apply {
            arguments = Bundle().apply {
                putString(ARG_MESSAGE, message)
                putString(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText)
                putBoolean(ARG_SHOW_INFO_ICON, showInfoIcon)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(ARG_MESSAGE, "")
            positiveButtonText = it.getString(ARG_POSITIVE_BUTTON_TEXT, "")
            showInfoIcon = it.getBoolean(ARG_SHOW_INFO_ICON)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.setCancelable(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewInfo.visibility = if (showInfoIcon) View.VISIBLE else View.GONE

        textViewMessage.text = message
        if (positiveButtonText.isNotEmpty()) {
            buttonPositive.text = positiveButtonText
        }

        buttonPositive.setOnClickListener(this)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonPositive.id -> {
                    onClickListener.onPositiveButtonClick()
                    dismiss()
                }
            }
        }
    }
}