package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.adapter.AddLumperAdapter
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AddWorkItemLumpersContract.View.OnAdapterItemClickListener {

    private var position: Int = 0
    private lateinit var addLumperAdapter: AddLumperAdapter
    private var addedLumpers: ArrayList<LumperModel> = ArrayList()

    companion object {
        const val ARG_IS_ADD_LUMPER = "ARG_IS_ADD_LUMPER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_item_lumpers)

        intent.extras?.let { it ->
            if (it.containsKey(ARG_IS_ADD_LUMPER)) {
                val isAddLumper = it.getBoolean(ARG_IS_ADD_LUMPER, true)

                if (isAddLumper) {
                    setupToolbar(getString(R.string.add_lumpers))
                } else {
                    setupToolbar(getString(R.string.update_lumpers))
                }
            }
        }

        position = intent.getIntExtra("position", 0)

        recycler_lumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addLumperAdapter = AddLumperAdapter(this@AddWorkItemLumpersActivity)
            adapter = addLumperAdapter
        }

        buttonAdd.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAdd.id -> {
                    addedLumpers = addLumperAdapter.getSelectedLumper()
                    if (addedLumpers.size > 0) {
                        val intent = Intent()
                        intent.putExtra("position", position)
                        intent.putExtra("count", addedLumpers.size)
                        setResult(Activity.RESULT_OK, intent)
                        onBackPressed()
                    }
                }

                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            addLumperAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSelectLumper(totalSelectedCount: Int) {
        if (totalSelectedCount > 0) {
            buttonAdd.isEnabled = true
            buttonAdd.setBackgroundResource(R.drawable.round_button_red_new)
        } else {
            buttonAdd.isEnabled = false
            buttonAdd.setBackgroundResource(R.drawable.round_button_disabled)
        }
    }
}
