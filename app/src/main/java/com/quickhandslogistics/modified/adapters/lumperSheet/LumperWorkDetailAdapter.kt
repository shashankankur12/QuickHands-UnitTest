package com.quickhandslogistics.modified.adapters.lumperSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.customerSheet.ContainerDetailItemAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperWorkDetailContract
import kotlinx.android.synthetic.main.item_lumper_work_detail.view.*

class LumperWorkDetailAdapter(
    private val resources: Resources,
    private var adapterItemClickListener: LumperWorkDetailContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<LumperWorkDetailAdapter.ViewHolder>() {

    private val parameters: ArrayList<String> = ArrayList()
    private val buildingOps: HashMap<String, String> = HashMap()

    init {
        parameters.add("Container No.")
        parameters.add("Door")
        parameters.add("Pallet")
        parameters.add("Color")
        parameters.add("Size")

        buildingOps["Door"] = "34"
        buildingOps["Container No."] = "GFHHG54564"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lumper_work_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    fun getItem(position: Int): String {
        return ""
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var textViewWorkItemType: TextView = itemView.textViewWorkItemType
        var textViewCustomerNote: TextView = itemView.textViewCustomerNote
        var textViewQHLNote: TextView = itemView.textViewQHLNote
        var textViewStatus: TextView = itemView.textViewStatus
        var clickableViewBO: View = itemView.clickableViewBO
        var recyclerViewBO: RecyclerView = itemView.recyclerViewBO
        var linearLayoutCustomerNotes: LinearLayout = itemView.linearLayoutCustomerNotes
        var linearLayoutQHLNotes: LinearLayout = itemView.linearLayoutQHLNotes

        init {
            recyclerViewBO.apply {
                layoutManager = LinearLayoutManager(context)
            }

            clickableViewBO.setOnClickListener(this)
            linearLayoutCustomerNotes.setOnClickListener(this)
            linearLayoutQHLNotes.setOnClickListener(this)
        }

        fun bind() {
            recyclerViewBO.adapter = ContainerDetailItemAdapter(buildingOps, parameters)

/*            val workItemTypeDisplayName =
                ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            textViewWorkItemType.text = workItemTypeDisplayName

            if (!workItemDetail.notesQHLCustomer.isNullOrEmpty() &&
                workItemDetail.notesQHLCustomer != AppConstant.NOTES_NOT_AVAILABLE
            ) {
                linearLayoutCustomerNotes.visibility = View.VISIBLE
                textViewCustomerNote.text = workItemDetail.notesQHLCustomer
            } else {
                linearLayoutCustomerNotes.visibility = View.GONE
            }

            recyclerViewBO.adapter = ContainerDetailItemAdapter(
                workItemDetail.buildingOps,
                workItemDetail.buildingDetailData?.parameters
            )*/
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    clickableViewBO.id -> {
                      //  val workItemDetail = getItem(adapterPosition)
                        adapterItemClickListener.onBOItemClick(buildingOps, parameters)
                    }
                    linearLayoutCustomerNotes.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        adapterItemClickListener.onNotesItemClick("Customer Notes Sample")
                    }
                    linearLayoutQHLNotes.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        adapterItemClickListener.onNotesItemClick("QHL Notes Sample")
                    }
                }
            }
        }
    }
}