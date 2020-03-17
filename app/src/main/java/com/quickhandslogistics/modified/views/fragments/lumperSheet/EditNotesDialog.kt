package com.quickhandslogistics.modified.views.fragments.lumperSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.fragment_dialog_edit_notes.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class EditNotesDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_edit_notes, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.title = getString(R.string.edit_notes)
        toolbar.setNavigationOnClickListener {
            dismiss()
        }

        buttonSave.setOnClickListener {
            dismiss()
        }
    }
}