package com.quickhandslogistics.adapters.workSheet

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.item_select_status.view.*
import kotlinx.android.synthetic.main.item_select_status.view.textViewStatus
import kotlinx.android.synthetic.main.item_show_less_more.view.*

class WorkSheetItemStatusAdapter(private val resources: Resources, private val onAdapterClick: WorkSheetItemDetailContract.View.OnAdapterItemClickListener) :
    Adapter<RecyclerView.ViewHolder>() {

    private var statusList: LinkedHashMap<String, String> = LinkedHashMap()
    private var initialDisplayStatus: String = ""
    private var selectedDisplayStatus: String = ""
    private var isCompleted: Boolean=false
    private val ShowMoreLess = 4
    private val NormalView = 1

    init {
        statusList[resources.getString(R.string.scheduled)] = AppConstant.WORK_ITEM_STATUS_SCHEDULED
        statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
        statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
        statusList[resources.getString(R.string.completed)] = AppConstant.WORK_ITEM_STATUS_COMPLETED
//        statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
        statusList[resources.getString(R.string.show_more)] = AppConstant.WORK_ITEM_SHOW_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder
        if (viewType==ShowMoreLess){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_show_less_more, parent, false)
            viewHolder= ViewHolder2(view, parent.context)
        }else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_select_status, parent, false)
            viewHolder= ViewHolder1(view, parent.context)
        }
        return viewHolder

    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    private fun getItem(position: Int): Pair<String, String> {
        val key = ArrayList(statusList.keys)[position]
        val value = statusList[key]
        return Pair(key!!, value!!)
    }

    override fun getItemViewType(position: Int): Int {
        if (position.equals(4)){
            return ShowMoreLess
        }else{
            return NormalView
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType.equals(ShowMoreLess)){
            val categoryViewHolder: ViewHolder2 = holder as ViewHolder2
            categoryViewHolder.bind2(getItem(position))
        }else {
            val categoryViewHolder: ViewHolder1 = holder as ViewHolder1
            categoryViewHolder.bind1(getItem(position))
        }
    }

    inner class ViewHolder1(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewStatus: TextView = view.textViewStatus
        private val imageViewAdd: ImageView = view.imageViewAdd
        private val textViewDescription: TextView = view.textViewDescription

        fun bind1(pair: Pair<String, String>) {
            textViewStatus.text = pair.first

            when (pair.first) {
                resources.getString(R.string.in_progress) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
                    textViewDescription.text = resources.getString(R.string.in_progress_description)
                }
                resources.getString(R.string.on_hold) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
                    textViewDescription.text = resources.getString(R.string.on_hold_description)
                }
                resources.getString(R.string.scheduled) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_light))
                    textViewDescription.text = resources.getString(R.string.scheduled_description)
                }
                resources.getString(R.string.cancelled) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                    textViewDescription.text = resources.getString(R.string.cancelled_description)
                }
                resources.getString(R.string.completed) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.buildingTitle))
                    textViewDescription.text = resources.getString(R.string.completed_description)
                }
                resources.getString(R.string.unfinished) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.color_yellow))
                    textViewDescription.text = resources.getString(R.string.unfinished_description)
                }
                resources.getString(R.string.not_open) -> {
                    textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.detailHeader))
                    textViewDescription.text = resources.getString(R.string.not_done_description)
                }
            }

            if (selectedDisplayStatus == pair.first) {
                textViewStatus.typeface = Typeface.DEFAULT_BOLD
                imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick)
            } else {
                textViewStatus.typeface = Typeface.DEFAULT
                imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick_blank)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        if (isCompleted) {
                            CustomProgressBar.getInstance().showErrorDialog(resources.getString(R.string.updtae_status_after_signature_message), context)
                        } else {
                            val pair = getItem(adapterPosition)
                            if (selectedDisplayStatus != pair.first) {
                                selectedDisplayStatus = pair.first
                                notifyDataSetChanged()
                                onAdapterClick.onSelectStatus(pair.second)
                            }
                        }
                    }
                }
            }
        }
    }


    inner class ViewHolder2(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textView: TextView = view.textViewMoreLess


        fun bind2(pair: Pair<String, String>) {
            textView.text = pair.first

            textView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    textView.id -> {
                        if (statusList.size.equals(5))
                            updateShowMoreStatusList()
                            else
                            updateShowLessStatusList()

                    }
                }
            }
        }
    }

    fun updateInitialStatus(initialStatus: String, isCompleted: Boolean) {
        this.initialDisplayStatus = initialStatus
        this.isCompleted = isCompleted
        selectedDisplayStatus = initialStatus
        notifyDataSetChanged()
    }

    fun updateStatusList(statusList: LinkedHashMap<String, String>) {
        this.statusList.clear()
        this.statusList.putAll(statusList)
        notifyDataSetChanged()
    }

    fun updateShowMoreStatusList() {
        this.statusList.clear()
        this.statusList.put(resources.getString(R.string.scheduled), AppConstant.WORK_ITEM_STATUS_SCHEDULED)
        this.statusList.put(resources.getString(R.string.in_progress), AppConstant.WORK_ITEM_STATUS_IN_PROGRESS)
        this.statusList.put(resources.getString(R.string.on_hold), AppConstant.WORK_ITEM_STATUS_ON_HOLD)
        this.statusList.put(resources.getString(R.string.completed), AppConstant.WORK_ITEM_STATUS_COMPLETED)
        this.statusList.put(resources.getString(R.string.show_less), AppConstant.WORK_ITEM_SHOW_LESS)
        this.statusList.put(resources.getString(R.string.unfinished), AppConstant.WORK_ITEM_STATUS_UNFINISHED)
        this.statusList.put(resources.getString(R.string.not_open), AppConstant.WORK_ITEM_STATUS_NOT_OPEN)
        this.statusList.put(resources.getString(R.string.cancelled), AppConstant.WORK_ITEM_STATUS_CANCELLED)
        notifyDataSetChanged()
    }

    fun updateShowLessStatusList() {
        this.statusList.clear()
        this.statusList.put(resources.getString(R.string.scheduled), AppConstant.WORK_ITEM_STATUS_SCHEDULED)
        this.statusList.put(resources.getString(R.string.in_progress), AppConstant.WORK_ITEM_STATUS_IN_PROGRESS)
        this.statusList.put(resources.getString(R.string.on_hold), AppConstant.WORK_ITEM_STATUS_ON_HOLD)
        this.statusList.put(resources.getString(R.string.completed), AppConstant.WORK_ITEM_STATUS_COMPLETED)
        this.statusList.put(resources.getString(R.string.show_more), AppConstant.WORK_ITEM_SHOW_MORE)
        notifyDataSetChanged()
    }
}