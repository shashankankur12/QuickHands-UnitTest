package com.quickhandslogistics.utils

import LeadWorkInfo
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jsibbold.zoomage.ZoomageView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.addContainer.AddScheduleDialogAdapter
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_NORMAL
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_TIME
import java.util.*

@SuppressLint("StaticFieldLeak")
object CustomerDialog : AppConstant {
    private var mActivity: Activity? = null
    fun getDialog(view: Int, activity: Activity?): Dialog {
        mActivity = activity
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(R.color.colorOpaque)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(view)
        val layoutParams = dialog.window!!.attributes
        val window = dialog.window
        layoutParams.copyFrom(window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = layoutParams
        layoutParams.gravity = Gravity.CENTER
        return dialog
    }

    fun showGroupNoteDialog(activity: Activity?, title: String?, customerGroupNote: Triple<Pair<ArrayList<String>?, ArrayList<String>?>?, ArrayList<String>?, ArrayList<String>?>) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.custome_alert_dialog, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val layoutDailyNote = dialog.findViewById<LinearLayout>(R.id.layoutDailyNote)
        val layoutWeeklyNote = dialog.findViewById<LinearLayout>(R.id.layoutWeeklyNote)
        val layoutMonthlyNote = dialog.findViewById<LinearLayout>(R.id.layoutMonthlyNote)
        val layoutCustomNote = dialog.findViewById<LinearLayout>(R.id.layoutCustomNote)
        val dailyNoteRecycler: RecyclerView = dialog.findViewById(R.id.recycler_view_daily_note)
        val weeklyNoteRecycler: RecyclerView = dialog.findViewById(R.id.recycler_view_weekly_note)
        val MonthlyNoteRecycelr: RecyclerView = dialog.findViewById(R.id.recycler_view_monthly_note)
        val CustomNoteRecycler: RecyclerView = dialog.findViewById(R.id.recycler_view_custom_note)
        val confirm = dialog.findViewById<Button>(R.id.confirm_button)
        val dailyNoteList = customerGroupNote.first?.first
        val weeklyNoteList = customerGroupNote.second
        val monthlyNoteList = customerGroupNote.third
        val customNoteList = customerGroupNote.first?.second
        if (dailyNoteList != null && dailyNoteList.size > 0) {
            val mDailyNoteAdapter = ListContentAdapter(dailyNoteList)
            val manager1: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            dailyNoteRecycler.layoutManager = manager1
            dailyNoteRecycler.adapter = mDailyNoteAdapter
            dailyNoteRecycler.visibility = View.VISIBLE
            layoutDailyNote.visibility = View.VISIBLE
        } else {
            dailyNoteRecycler.visibility = View.GONE
            layoutDailyNote.visibility = View.GONE
        }
        if (weeklyNoteList != null && weeklyNoteList.size > 0) {
            val mWeeklyNoteAdapter = ListContentAdapter(weeklyNoteList)
            val manager2: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            weeklyNoteRecycler.layoutManager = manager2
            weeklyNoteRecycler.adapter = mWeeklyNoteAdapter
            weeklyNoteRecycler.visibility = View.VISIBLE
            layoutWeeklyNote.visibility = View.VISIBLE
        } else {
            weeklyNoteRecycler.visibility = View.GONE
            layoutWeeklyNote.visibility = View.GONE
        }
        if (monthlyNoteList != null && monthlyNoteList.size > 0) {
            val mMonthlyNoteAdaptor = ListContentAdapter(monthlyNoteList)
            val manager3: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            MonthlyNoteRecycelr.layoutManager = manager3
            MonthlyNoteRecycelr.adapter = mMonthlyNoteAdaptor
            MonthlyNoteRecycelr.visibility = View.VISIBLE
            layoutMonthlyNote.visibility = View.VISIBLE
        } else {
            MonthlyNoteRecycelr.visibility = View.GONE
            layoutMonthlyNote.visibility = View.GONE
        }
        if (customNoteList != null && customNoteList.size > 0) {
            val mCustomNoteAdaptor = ListContentAdapter(customNoteList)
            val manager3: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            CustomNoteRecycler.layoutManager = manager3
            CustomNoteRecycler.adapter = mCustomNoteAdaptor
            CustomNoteRecycler.visibility = View.VISIBLE
            layoutCustomNote.visibility = View.VISIBLE
        } else {
            CustomNoteRecycler.visibility = View.GONE
            layoutCustomNote.visibility = View.GONE
        }
        titleTextView.text = title
        confirm.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun showWorkScheduleDialog(activity: Activity?, resources: Resources, leadWorkInfo: LeadWorkInfo?) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.view_work_schedule, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val textViewScheduleType = dialog.findViewById<TextView>(R.id.textViewScheduleType)
        val textViewStatus = dialog.findViewById<TextView>(R.id.textViewStatus)
        val textViewScheduleOutBound = dialog.findViewById<TextView>(R.id.textViewScheduleOutBound)
        val textViewScheduleOutBoundStartTime =
            dialog.findViewById<TextView>(R.id.textViewScheduleOutBoundStartTime)
        val textViewScheduleLiveLoad = dialog.findViewById<TextView>(R.id.textViewScheduleLiveLoad)
        val textViewScheduleLiveLoadStartTime =
            dialog.findViewById<TextView>(R.id.textViewScheduleLiveLoadStartTime)
        val textViewScheduleDrops = dialog.findViewById<TextView>(R.id.textViewScheduleDrops)
        val textViewScheduleDropsStartTime =
            dialog.findViewById<TextView>(R.id.textViewScheduleDropsStartTime)
        val textViewScheduleUnfinished =
            dialog.findViewById<TextView>(R.id.textViewScheduleUnfinished)
        val textViewScheduleUnfinishedStartTime =
            dialog.findViewById<TextView>(R.id.textViewScheduleUnfinishedStartTime)
        val textViewWorkItemsCount = dialog.findViewById<TextView>(R.id.textViewWorkItemsCount)
        val textViewWorkItemsLeadName =
            dialog.findViewById<TextView>(R.id.textViewWorkItemsLeadName)
        val relativeLayoutSide = dialog.findViewById<RelativeLayout>(R.id.relativeLayoutSide)
        val confirm = dialog.findViewById<Button>(R.id.confirm_button)

        titleTextView.text = DateUtils.changeDateString(PATTERN_API_RESPONSE, PATTERN_NORMAL, leadWorkInfo?.date!!)
        textViewScheduleType.text = leadWorkInfo.department?.let {
            UIUtils.getSpannableText(resources.getString(R.string.department_full),
                it.toLowerCase().capitalize()
            )
        }
        textViewWorkItemsCount.text = String.format(resources.getString(R.string.total_containers_s), leadWorkInfo.totalContainers)
        textViewWorkItemsLeadName.text = String.format(resources.getString(R.string.lead_name), leadWorkInfo.lead)
        textViewScheduleUnfinished.text = String.format(resources.getString(R.string.unfinished_drop), "0"/*scheduleDetail.scheduleTypes?.drops?.size.toString()*/)
        textViewScheduleUnfinishedStartTime.text =/*DateUtils.convertMillisecondsToTimeString((scheduleDetail.scheduleTypes?.drops!![0].startTime)!!.toLong())*/""

        if (leadWorkInfo.outbounds != null) {
            textViewScheduleOutBound.text = String.format(resources.getString(R.string.out_bound_s), leadWorkInfo.outbounds?.count)
            if (!leadWorkInfo.outbounds?.time.isNullOrEmpty())
                textViewScheduleOutBoundStartTime.text = DateUtils.changeDateString(PATTERN_API_RESPONSE, PATTERN_TIME, leadWorkInfo.outbounds?.time!![0])
        }

        if (leadWorkInfo.live != null) {
            textViewScheduleLiveLoad.text = String.format(resources.getString(R.string.live_load_s), leadWorkInfo.live?.count)
            if (!leadWorkInfo.live?.time.isNullOrEmpty())
                textViewScheduleLiveLoadStartTime.text = DateUtils.changeDateString(PATTERN_API_RESPONSE, PATTERN_TIME, leadWorkInfo.live?.time!![0])
        }

        if (leadWorkInfo.drops != null) {
            textViewScheduleDrops.text =
                String.format(resources.getString(R.string.drops_s), leadWorkInfo.drops?.count)
            if (!leadWorkInfo.drops?.time.isNullOrEmpty()) textViewScheduleDropsStartTime.text = DateUtils.changeDateString(PATTERN_API_RESPONSE, PATTERN_TIME, leadWorkInfo.drops?.time!!)
        }

        if (leadWorkInfo.department == AppConstant.EMPLOYEE_DEPARTMENT_INBOUND) {
            textViewScheduleOutBoundStartTime.visibility = View.GONE
            textViewScheduleOutBound.visibility = View.GONE
        } else if (leadWorkInfo.department == AppConstant.EMPLOYEE_DEPARTMENT_OUTBOUND) {
            textViewScheduleDrops.visibility = View.GONE
            textViewScheduleDropsStartTime.visibility = View.GONE
            textViewScheduleLiveLoad.visibility = View.GONE
            textViewScheduleLiveLoadStartTime.visibility = View.GONE
        }

        ScheduleUtils.changeStatusUIByValue(resources, AppConstant.WORK_ITEM_STATUS_SCHEDULED, textViewStatus, relativeLayoutSide)
        confirm.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun showAddNoteDialog(activity: Activity, title: String?, uploadContainer: ArrayList<ContainerDetails>, liveLoadContainer: ArrayList<ContainerDetails>, dropOffContainer: ArrayList<ContainerDetails>, iDialogOnClick: IDialogOnClick) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.add_container_custome_dialog, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val recyclerViewSchedule = dialog.findViewById<RecyclerView>(R.id.recycler_view_schedules)
        val addButton = dialog.findViewById<Button>(R.id.add_button)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)
        var mAllScheduleList: ArrayList<ContainerDetails> = ArrayList()
        mAllScheduleList.addAll(uploadContainer)
        mAllScheduleList.addAll(liveLoadContainer)
        mAllScheduleList.addAll(dropOffContainer)


        recyclerViewSchedule.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
             val mWeeklyNoteAdapter = AddScheduleDialogAdapter(mAllScheduleList, activity)
            adapter = mWeeklyNoteAdapter
        }

        titleTextView.text = title
        cancelButton.setOnClickListener { dialog.dismiss() }
        addButton.setOnClickListener { iDialogOnClick.onSendRequest(dialog) }

        dialog.show()
    }

    fun showLeadNoteDialog(activity: Activity?, title: String?, individualNote: String?, groupNote: String?, individualHeader: String, groupHeader: String) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.lead_note_dialog, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val layoutIndividualNote = dialog.findViewById<LinearLayout>(R.id.layoutIndividualNote)
        val layoutGroupNote = dialog.findViewById<LinearLayout>(R.id.layoutGroupNote)
        val individualNoteText: TextView = dialog.findViewById(R.id.individual_note)
        val groupNoteText: TextView = dialog.findViewById(R.id.groupNote)
        val individualNoteHeader: TextView = dialog.findViewById(R.id.individual_note_header)
        val groupNoteHeader: TextView = dialog.findViewById(R.id.group_note_header)
        val confirm = dialog.findViewById<Button>(R.id.confirm_button)

        if(individualNote.isNullOrEmpty() || individualNote.equals("NA")){
            layoutIndividualNote.visibility=View.GONE
        }else{
            layoutIndividualNote.visibility=View.VISIBLE
        }

        if(groupNote.isNullOrEmpty()){
            layoutGroupNote.visibility=View.GONE
        }else{
            layoutGroupNote.visibility=View.VISIBLE
        }

        individualNoteHeader.text=individualHeader
        groupNoteHeader.text=groupHeader
        individualNoteText.text= individualNote
        groupNoteText.text= groupNote
        titleTextView.text = title
        confirm.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun showNoteDialog(activity: Activity?, title: String?, groupNote: String?) {
        mActivity = activity
        val dialog =
            getDialog(R.layout.comman_note_dialog, activity)
        //        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val titleTextView = dialog.findViewById<TextView>(R.id.title_text)
        val note: TextView = dialog.findViewById(R.id.individual_note)
        val confirm = dialog.findViewById<Button>(R.id.confirm_button)

        note.text= groupNote?.capitalize()
        titleTextView.text = title
        confirm.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

     fun openZoomImageDialog(url: String, activity: Activity) {
        val dialog = getDialog(R.layout.dialog_zoom_image, activity)

        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val imageClose = dialog.findViewById<ImageView>(R.id.image_close)
        val imageView: ZoomageView = dialog.findViewById(R.id.imageView)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility=View.VISIBLE

        Glide.with(activity)
            .load(url)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility=View.GONE
                    return false
                }

            })
            .into(imageView)
        dialog.setCancelable(true)
        dialog.show()
        imageClose.setOnClickListener { dialog.dismiss() }
    }

    interface IDialogOnClick{
        fun onSendRequest( dialog: Dialog)
    }
}