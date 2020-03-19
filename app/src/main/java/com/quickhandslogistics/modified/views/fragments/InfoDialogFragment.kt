package com.quickhandslogistics.modified.views.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogContract
import kotlinx.android.synthetic.main.fragment_dialog_info.*

class InfoDialogFragment(private val onClickListener: InfoDialogContract.View.OnClickListener) :
    DialogFragment(), View.OnClickListener {

    private var message: String = ""
    private var positiveButtonText: String = ""
    private var negativeButtonText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(ARG_MESSAGE, "")
            positiveButtonText = it.getString(ARG_POSITIVE_BUTTON_TEXT, "")
            negativeButtonText = it.getString(ARG_NEGATIVE_BUTTON_TEXT, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewMessage.text = message
        if (positiveButtonText.isNotEmpty()) {
            buttonPositive.text = positiveButtonText
        }
        if (negativeButtonText.isNotEmpty()) {
            buttonNegative.text = negativeButtonText
        }

        buttonPositive.setOnClickListener(this)
        buttonNegative.setOnClickListener(this)
    }

    companion object {
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_POSITIVE_BUTTON_TEXT = "ARG_POSITIVE_BUTTON_TEXT"
        private const val ARG_NEGATIVE_BUTTON_TEXT = "ARG_NEGATIVE_BUTTON_TEXT"

        @JvmStatic
        fun newInstance(
            message: String,
            positiveButtonText: String = "",
            negativeButtonText: String = "",
            onClickListener: InfoDialogContract.View.OnClickListener
        ) =
            InfoDialogFragment(onClickListener).apply {
                arguments = Bundle().apply {
                    putString(ARG_MESSAGE, message)
                    putString(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText)
                    putString(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText)
                }
            }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonPositive.id -> {
                    onClickListener.onPositiveButtonClick()
                    dismiss()
                }
                buttonNegative.id -> {
                    dismiss()
                }
            }
        }
    }
}