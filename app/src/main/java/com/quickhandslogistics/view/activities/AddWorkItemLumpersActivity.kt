package com.quickhandslogistics.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.adapter.AddLumperAdapter
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(),View.OnClickListener, TextWatcher {

    private var position: Int = 0
    private lateinit var addLumperAdapter: AddLumperAdapter
    private var addedLumpers: ArrayList<LumperModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_item_lumpers)
        setupToolbar(getString(R.string.add_lumpers))

        position = intent.getIntExtra("position", 0)

        recycler_lumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            addLumperAdapter = AddLumperAdapter(activity)
            adapter = addLumperAdapter
        }

        button_submit.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                button_submit.id -> {
                    addedLumpers = addLumperAdapter.getSelectedLumper()
                    if (addedLumpers.size > 0) {
                        val intent = Intent()
                        intent.putExtra("position", position)
                        intent.putExtra("count", addedLumpers.size)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
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

}
