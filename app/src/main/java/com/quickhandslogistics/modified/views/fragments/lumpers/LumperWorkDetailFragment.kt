package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment

class LumperWorkDetailFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumper_work_detail, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LumperWorkDetailFragment()
    }
}
