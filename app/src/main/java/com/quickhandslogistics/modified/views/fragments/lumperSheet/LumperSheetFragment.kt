package com.quickhandslogistics.modified.views.fragments.lumperSheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.LumperSheetAdapter
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.activities.LumperListActivity
import com.quickhandslogistics.view.activities.LumperSheetDetailActivity
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*


class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher,
    View.OnClickListener,
    LumperSheetContract.View.OnAdapterItemClickListener {

    val lumperList: ArrayList<LumperModel> = ArrayList()
    private lateinit var lumperSheetAdapter: LumperSheetAdapter
    private lateinit var lumperSheetPresenter: LumperSheetPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumperSheetPresenter = LumperSheetPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumper_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_lumper_sheet.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            lumperSheetAdapter =
                LumperSheetAdapter(this@LumperSheetFragment)
            adapter = lumperSheetAdapter
        }

        edit_search_lumper.addTextChangedListener(this)
        image_cancel.setOnClickListener(this)
        fab_show_lumper.setOnClickListener(this)
        image_filter.setOnClickListener(this)

        lumperSheetPresenter.fetchLumpersList()
    }

    override fun showLumperSheetData(
        lumperList: ArrayList<LumperModel>
    ) {
        lumperSheetAdapter.updateLumpersData(lumperList)
        if (lumperList.size > 0) {
            text_no_record_found.visibility = View.GONE
            recycler_lumper_sheet.visibility = View.VISIBLE
        } else {
            recycler_lumper_sheet.visibility = View.GONE
            text_no_record_found.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperSheetAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            image_cancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onItemClick(lumperData: LumperModel) {
        val bundle = Bundle()
        bundle.putSerializable(LumperSheetDetailActivity.ARG_LUMPER_SHEET_DATA, lumperData)
        startIntent(LumperSheetDetailActivity::class.java, bundle = bundle)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                image_cancel.id -> {
                    edit_search_lumper.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }

                image_filter.id -> {
                    val popupMenu = PopupMenu(context!!, image_filter)
                    popupMenu.menuInflater.inflate(R.menu.filtermenu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.menu_complete ->
                                lumperSheetAdapter.setStatusEnabled(item.title.toString())
                            R.id.mnu_prgrs ->
                                lumperSheetAdapter.setStatusEnabled(item.title.toString())
                            R.id.menu_all ->
                                lumperSheetAdapter.setStatusEnabled("")
                        }
                        true
                    }
                    popupMenu.show()
                }

                fab_show_lumper.id -> {
                    val bundle = Bundle()
                    bundle.putSerializable(LumperListActivity.ARG_LUMPER_LIST_SHEET, lumperList)
                    startIntent(LumperListActivity::class.java, bundle = bundle)

                }
            }
        }
    }
}
