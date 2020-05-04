package com.quickhandslogistics.modified.adapters.common

import android.os.Bundle
import android.view.View
import com.github.gcacace.signaturepad.views.SignaturePad
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import kotlinx.android.synthetic.main.activity_add_signature.*

class AddSignatureActivity : BaseActivity(), View.OnClickListener, SignaturePad.OnSignedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_signature)
        setupToolbar(getString(R.string.add_signature))

        signaturePad.setOnSignedListener(this)
        buttonSubmit.setOnClickListener(this)
        buttonClear.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonClear.id -> {
                    signaturePad.clear();
                }
                buttonSubmit.id -> {
                    var signatureBitmap = signaturePad.signatureBitmap
                    onBackPressed()
                }
            }
        }
    }

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
