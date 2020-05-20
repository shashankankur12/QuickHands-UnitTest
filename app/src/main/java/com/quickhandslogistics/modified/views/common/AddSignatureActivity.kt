package com.quickhandslogistics.modified.views.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.gcacace.signaturepad.views.SignaturePad
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.ImageUtils
import kotlinx.android.synthetic.main.activity_add_signature.*

class AddSignatureActivity : BaseActivity(), View.OnClickListener, SignaturePad.OnSignedListener {

    companion object {
        const val ARG_SIGNATURE_FILE_PATH = "ARG_SIGNATURE_FILE_PATH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_add_signature)
        setupToolbar(getString(R.string.add_signature))

        signaturePad.setOnSignedListener(this)
        buttonSubmit.setOnClickListener(this)
        buttonClear.setOnClickListener(this)
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                saveSignature()
            }

            override fun onCancelClick() {
            }
        })
    }

    private fun saveSignature() {
        val signatureBitmap = signaturePad.signatureBitmap
        val file = ImageUtils.saveSignatureTemporary(signatureBitmap, 100, activity)
        file?.also {
            val intent = Intent()
            intent.putExtra(ARG_SIGNATURE_FILE_PATH, file.absolutePath)
            setResult(RESULT_OK, intent)
            onBackPressed()
        } ?: run {
            CustomProgressBar.getInstance().showErrorDialog(getString(R.string.something_went_wrong), activity)
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonClear.id -> signaturePad.clear()
                buttonSubmit.id -> showConfirmationDialog()
            }
        }
    }

    /** Signature Listeners */
    override fun onStartSigning() {
    }

    override fun onClear() {
        buttonSubmit.isEnabled = false
        buttonClear.isEnabled = false
    }

    override fun onSigned() {
        buttonSubmit.isEnabled = true
        buttonClear.isEnabled = true
    }
}
