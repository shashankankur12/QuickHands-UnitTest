package com.quickhandslogistics.views.workSheet

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetDetailsNoteImageAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailNoteImageContract
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_work_sheet_item_detail_notes.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WorkSheetItemDetailNotesFragment : BaseFragment(), View.OnClickListener, TextWatcher, WorkSheetItemDetailNoteImageContract.View.OnAdapterItemClickListener {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null
    private lateinit var workSheetItemDetailNotesImageAdapter: WorkSheetDetailsNoteImageAdapter
    private var imageStringArray = ArrayList<String>()
    private var workItemDetail: WorkItemContainerDetails? = null
    private var isDataChanged: Boolean =false
    private var isCompleteOrCancel: Boolean =false
    private var timeStamp = ""
    private var imageFileName = ""
    private val myBitmap: Bitmap? = null
    private var mPictureImagePath = ""
    private var mImgFile: File? = null
    private val MY_PERMISSIONS_REQUEST_CAMERA = 100

    companion object {
        private const val NOTE_WORK_DETALS = "NOTE_WORK_DETALS"
        private const val SCHEDULE_CUSTOMER_NOTE = "SCHEDULE_CUSTOMER_NOTE"
        private const val SCHEDULE_QHL_NOTE = "SCHEDULE_QHL_NOTE"
        private const val SCHEDULE_ATTACHMENT_LIST = "SCHEDULE_ATTACHMENT_LIST"
        @JvmStatic
        fun newInstance(allWorkItem: WorkItemContainerDetails?) = WorkSheetItemDetailNotesFragment()
            .apply {
                arguments = Bundle().apply {
                    if(allWorkItem!=null){
                        putParcelable(NOTE_WORK_DETALS, allWorkItem)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workItemDetail = it.getParcelable<WorkItemContainerDetails>(NOTE_WORK_DETALS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesImageRecycler.apply {
            val linearLayoutManager = GridLayoutManager(fragmentActivity!!, 3)
            layoutManager = linearLayoutManager
//            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
//            addItemDecoration(dividerItemDecoration)
            workSheetItemDetailNotesImageAdapter = WorkSheetDetailsNoteImageAdapter(this@WorkSheetItemDetailNotesFragment)
            adapter = workSheetItemDetailNotesImageAdapter
        }
        addNotesTouchListener(editTextQHLCustomerNotes)
        addNotesTouchListener(editTextQHLNotes)

        buttonSubmit.setOnClickListener(this)
        addImageButton.setOnClickListener(this)
        editTextQHLCustomerNotes.addTextChangedListener(this)
        editTextQHLNotes.addTextChangedListener(this)

        workItemDetail?.let { showNotesData(it) }
    }


    fun showNotesData(workItemDetail: WorkItemContainerDetails) {
        this.workItemDetail = workItemDetail

        if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() && workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
            editTextQHLCustomerNotes.setText(workItemDetail.notesQHLCustomer)
        }

        if (!workItemDetail.notesQHL.isNullOrEmpty() && workItemDetail.notesQHL != AppConstant.NOTES_NOT_AVAILABLE) {
            editTextQHLNotes.setText(workItemDetail.notesQHL)
        }

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                editTextQHLCustomerNotes.isEnabled = false
                editTextQHLNotes.isEnabled = false
                buttonSubmit.visibility = View.GONE
                addImageButton.visibility = View.GONE
                isCompleteOrCancel=true
            } else {
                editTextQHLCustomerNotes.isEnabled = true
                editTextQHLNotes.isEnabled = true
                buttonSubmit.visibility = View.VISIBLE
                addImageButton.visibility = View.VISIBLE
                isCompleteOrCancel=false
            }
        }

        workItemDetail.attachmentsList?.let {
            imageStringArray= it as ArrayList<String>
            workSheetItemDetailNotesImageAdapter.updateList(imageStringArray, isCompleteOrCancel)
        }
    }

    private fun saveWorkItemNotes() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

//        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.save_notes_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
//            override fun onConfirmClick() {
                workItemDetail?.let {
                    val notesQHLCustomer = editTextQHLCustomerNotes.text.toString()
                    val notesQHL = editTextQHLNotes.text.toString()
                    val noteImageArrayList=workSheetItemDetailNotesImageAdapter.getImageArrayList()

                    onFragmentInteractionListener?.updateWorkItemNotes(notesQHLCustomer, notesQHL, noteImageArrayList)
                }
//            }
//
//            override fun onCancelClick() {
//            }
//        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                buttonSubmit.id -> saveWorkItemNotes()
                addImageButton.id -> {
                    if (PermissionUtil.checkCameraStorage(activity)) {
                        imageCapture()
                    } else {
                        PermissionUtil.requestCameraStorage(activity)
                    }
                }
            }
        }
    }

    private fun imageCapture() {
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = timeStamp + ".jpg"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        mPictureImagePath = storageDir.absolutePath + "/" + imageFileName
        val file: File = File(mPictureImagePath)
        val outputFileUri = Uri.fromFile(file)
        mImgFile = null

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        startActivityForResult(cameraIntent, MY_PERMISSIONS_REQUEST_CAMERA)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                MY_PERMISSIONS_REQUEST_CAMERA -> {
                    mImgFile = File(mPictureImagePath)
                    if (mImgFile!!.exists()) {
                        imageFileName = "$timeStamp.jpg"
                        val newImage: String? = ImageUtils.resizeAndCompressImageBeforeSend(fragmentActivity!!, mImgFile!!.absolutePath, imageFileName)
                        val optimizedFile = File(newImage)
                        val body = DataManager.createMultiPartBodyImage(optimizedFile.absoluteFile, AppConstant.IMAGE_PARAM)
                        onFragmentInteractionListener?.uploadNoteImage(body)
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.PERMISSION_REQUEST_CODE -> {
                if (PermissionUtil.granted(grantResults)) {
                    imageCapture()
                }
            }
        }
    }

    override fun afterTextChanged(text: Editable?) {
        if (text === editTextQHLCustomerNotes.editableText) {
            var notesQHLCustomerBefore = ""
            var notesQHLBefore = ""
            if (!workItemDetail?.notesQHLCustomer.isNullOrEmpty() && workItemDetail!!.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE) {
                notesQHLCustomerBefore = workItemDetail!!.notesQHLCustomer!!
            }
            if (!workItemDetail?.notesQHL.isNullOrEmpty() && workItemDetail!!.notesQHL != AppConstant.NOTES_NOT_AVAILABLE)
                notesQHLBefore=workItemDetail!!.notesQHL!!
            if (!notesQHLCustomerBefore.equals(text.toString()) || !notesQHLBefore.equals(editTextQHLNotes.text.toString()))
                onFragmentInteractionListener!!.dataChanged(false)
            else onFragmentInteractionListener!!.dataChanged(false)

        } else if (text === editTextQHLNotes.editableText) {
            var notesQHLCustomerBefore = ""
            var notesQHLBefore = ""
            if (!workItemDetail?.notesQHL.isNullOrEmpty() && workItemDetail!!.notesQHL != AppConstant.NOTES_NOT_AVAILABLE) {
                notesQHLBefore =workItemDetail!!.notesQHL!!
            }
            if (!workItemDetail?.notesQHLCustomer.isNullOrEmpty() && workItemDetail!!.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE)
                notesQHLCustomerBefore = workItemDetail!!.notesQHLCustomer!!
            if (!notesQHLBefore.equals(text.toString()) || !notesQHLCustomerBefore.equals(editTextQHLCustomerNotes.text.toString()))
                onFragmentInteractionListener!!.dataChanged(false)
            else onFragmentInteractionListener!!.dataChanged(false)

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onImageClick(imageUrl: String) {
       CustomerDialog.openZoomImageDialog(imageUrl,activity!!)
    }

    fun updateImageList(imageUrl: String) {
        imageStringArray.add(imageUrl)
        workSheetItemDetailNotesImageAdapter.updateList(imageStringArray, isCompleteOrCancel)
    }
}

