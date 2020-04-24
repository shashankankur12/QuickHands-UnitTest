package com.quickhandslogistics.modified.views.fragments.workSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment

class WorkSheetItemDetailNotesFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_notes, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkSheetItemDetailNotesFragment()
    }
}