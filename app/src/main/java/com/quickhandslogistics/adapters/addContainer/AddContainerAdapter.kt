package com.quickhandslogistics.adapters.addContainer

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_INBOUND
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_LIVE
import com.quickhandslogistics.utils.AppConstant.Companion.WORKSHEET_WORK_ITEM_OUTBOUND
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ValueUtils
import com.quickhandslogistics.views.addContainer.AddContainerActivity
import kotlinx.android.synthetic.main.item_add_container.view.*
import java.util.*
import kotlin.collections.ArrayList


class AddContainerAdapter(private val onAdapterClick: AddContainerActivity, private val resources: Resources, private val context: Context) : RecyclerView.Adapter<AddContainerAdapter.ViewHolder>() {
    private var containerDetails: ArrayList<ContainerDetails> = ArrayList()
    private var isShowingDialog=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_container, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return containerDetails.size
    }

    private fun getItem(position: Int): ContainerDetails {
        return containerDetails[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener, TextWatcher {

        private val textViewContainerType: TextView = view.textViewContainerType
        private val textViewRemove: TextView = view.textViewRemove
        private val editTextQuantity: EditText = view.editTextQuantity
        private val editTextStartTime: EditText = view.editTextStartTime

        fun bind(containerDetails: ContainerDetails) {
            when {
                containerDetails.workItemType.equals(WORKSHEET_WORK_ITEM_OUTBOUND) -> textViewContainerType.text =
                    resources.getString(R.string.out_bound_added)
                containerDetails.workItemType.equals(WORKSHEET_WORK_ITEM_LIVE) -> textViewContainerType.text =
                    resources.getString(R.string.live_load_added)
                containerDetails.workItemType.equals(WORKSHEET_WORK_ITEM_INBOUND) -> textViewContainerType.text =
                    resources.getString(R.string.drop_added)
            }

            if (containerDetails.startTime != null)
                editTextStartTime.setText(DateUtils.convertMillisecondsToTimeString(containerDetails.startTime?.toLong()!!))
            else editTextStartTime.setText("")

            if (containerDetails.workItemType.equals(WORKSHEET_WORK_ITEM_INBOUND)){
                editTextQuantity.setText(ValueUtils.getDefaultOrValue(containerDetails.numberOfDrops))
                if (containerDetails.numberOfDrops.isNullOrEmpty())
                    isShowingDialog=false
            }
            else {
                editTextQuantity.setText(ValueUtils.getDefaultOrValue(containerDetails.sequence))
                if (containerDetails.sequence.isNullOrEmpty())
                    isShowingDialog=false
            }

            if (containerDetails.workItemType.equals(WORKSHEET_WORK_ITEM_INBOUND)){
                editTextQuantity.isEnabled=true
            }
            else {
                editTextQuantity.isEnabled=false
            }

            textViewRemove.setOnClickListener(this)
            editTextStartTime.setOnClickListener(this)
            editTextQuantity.addTextChangedListener(this)

        }

        override fun onClick(view: View?) {
            if (!ConnectionDetector.isNetworkConnected(context)) {
                ConnectionDetector.createSnackBar(context as Activity?)
                return
            }

            view?.let {
                when (view.id) {
                    textViewRemove.id -> {
                        val item = getItem(adapterPosition)
                        removeContainerData(adapterPosition,item)
                    }
                    editTextStartTime.id -> {
                        var timeInMillis: Long = 0
                        if (containerDetails[adapterPosition].startTime != null) {
                            timeInMillis = containerDetails[adapterPosition].startTime?.toLong()!!
                        }
                        if (timeInMillis > 0) {
                            editTime(timeInMillis, adapterPosition)
                        } else {
                            editTime(Date().time, adapterPosition)
                        }
                    }

                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.toString().equals("0")) {
                val item = getItem(adapterPosition)
                isShowingDialog = false
                if (item.workItemType.equals(WORKSHEET_WORK_ITEM_INBOUND))
                    containerDetails[adapterPosition].numberOfDrops = s.toString()
                else containerDetails[adapterPosition].sequence = s.toString()

                onAdapterClick.addQuantity(adapterPosition, item, s.toString())
            } else {
                if (!isShowingDialog) {
                    CustomProgressBar.getInstance().showMessageDialog(
                        resources.getString(R.string.add_container_quantity_message),
                        context
                    )
                    isShowingDialog = true
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun editTime(timeInMillis: Long, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
            addStartTime(position, calendar.timeInMillis.toString())

            }, mHour, mMinute, false
        ).show()


    }

    private fun addStartTime(position: Int, timeInMilli: String) {
        val item= getItem(position)
        onAdapterClick.addTimeInList(position,item,timeInMilli)
        containerDetails[position].startTime = timeInMilli
        notifyDataSetChanged()
    }

    fun addContainerData(workItemType: String, containerList: ArrayList<ContainerDetails>) {
        this.containerDetails.clear()
        this.containerDetails.addAll(containerList)
        notifyDataSetChanged()
    }

    fun removeContainerData(position: Int, item: ContainerDetails) {
        onAdapterClick.removeItemFromList(position,item)
        containerDetails.removeAt(position)
        notifyDataSetChanged()
    }

    fun getContainerList(): ArrayList<ContainerDetails> {
        return containerDetails
    }

}