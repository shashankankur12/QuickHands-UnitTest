package com.quickhandslogistics.modified.views.fragments.lumpers

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.presenters.lumpers.LumpersPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.LumpersAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.view.activities.LumperDetailsActivity
import kotlinx.android.synthetic.main.fragment_lumpers.*

class LumpersFragment : BaseFragment(), LumpersContract.View, TextWatcher, View.OnClickListener,
    LumpersContract.View.OnAdapterItemClickListener {

    private lateinit var lumpersAdapter: LumpersAdapter
    private lateinit var lumpersPresenter: LumpersPresenter
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumpersPresenter = LumpersPresenter(this, resources)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumpers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpers.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            lumpersAdapter = LumpersAdapter(this@LumpersFragment)
            adapter = lumpersAdapter
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        lumpersPresenter.fetchLumpersList()
    }

    /*
    * Presenter Listeners
    */
    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(lumperDataList: ArrayList<LumperData>) {
        lumpersAdapter.updateLumpersData(lumperDataList)
        if (lumperDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    /*
    * Native Views Listeners
    */
    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumpersAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
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

    /*
    * Adapter Item Click Listeners
    */
    override fun onItemClick(lumperData: LumperData) {
        val bundle = Bundle()
        bundle.putSerializable(LumperDetailsActivity.ARG_LUMPER_DATA, lumperData)
        startIntent(LumperDetailsActivity::class.java, bundle = bundle)
//        when (lumperJobDetails) {
//            context.getString(R.string.string_lumper) -> context.startActivity(
//                Intent(
//                    context,
//                    LumperJobHistoryActivity::class.java
//                )
//            )
//            context.getString(R.string.string_lumper_sheet) -> context.startActivity(
//                Intent(
//                    context,
//                    LumperSheetDetailActivity::class.java
//                )
//            )
//            else -> {
//                val intent = Intent(context, LumperDetailsActivity::class.java)
//                intent.putExtra("lumperData", lumperData as Serializable)
//                context.startActivity(intent)
//            }
//        }
    }

    override fun onPhoneViewClick(phone: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
    }
}
