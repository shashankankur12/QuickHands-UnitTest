package com.quickhandslogistics.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.view.adapter.AddLumperAdapter
import kotlinx.android.synthetic.main.activity_add_work_item_lumpers.*

class AddWorkItemLumpersActivity : BaseActivity(),View.OnClickListener {

    private var position: Int = 0
    private lateinit var addLumperAdapter: AddLumperAdapter
    private var addedLumpers: ArrayList<String> = ArrayList()

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
    }

    override fun onClick(view: View?) {
        view?.let{
            when(view.id){
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
            }
        }
    }

}
