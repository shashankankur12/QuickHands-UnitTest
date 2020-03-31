package com.quickhandslogistics.modified.views.fragments.lumperSheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.ChooseLumperActivity
import com.quickhandslogistics.modified.views.adapters.LumperSheetAdapter
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.activities.LumperSheetDetailActivity
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*


class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher,
    View.OnClickListener,
    LumperSheetContract.View.OnAdapterItemClickListener, SpeedDialView.OnActionSelectedListener {

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

        recyclerViewLumpersSheet.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperSheetAdapter =
                LumperSheetAdapter(this@LumperSheetFragment)
            adapter = lumperSheetAdapter
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        speedDialView.inflate(R.menu.menu_lumper_sheet)
        speedDialView.setOnActionSelectedListener(this)

        lumperSheetPresenter.fetchLumpersList()
    }

    override fun showLumperSheetData(
        lumperList: ArrayList<LumperModel>
    ) {
        lumperSheetAdapter.updateLumpersData(lumperList)
        if (lumperList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpersSheet.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersSheet.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperSheetAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
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
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }
            }
        }
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem?.let {
            return when (actionItem.id) {
                R.id.actionAddLumper -> {
                    speedDialView.close(true)
                    startIntent(ChooseLumperActivity::class.java)
                    true
                }
                R.id.actionEditNote -> {
                    speedDialView.close(true)
                    val dialog = EditNotesDialog();
                    dialog.show(
                        activity!!.supportFragmentManager,
                        EditNotesDialog::class.simpleName
                    )
                    true
                }
                else -> false
            }
        }
        return false
    }
}
