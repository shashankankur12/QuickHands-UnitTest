package com.quickhandslogistics.modified.views.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.ContainerModel.ContainerDetailModel
import kotlinx.android.synthetic.main.content_container_detail.view.*

class ContainerDetailAdapter(val context: Activity) :
    RecyclerView.Adapter<ContainerDetailAdapter.ContainerItemHolder>()  {

    var containerDetailModel: ArrayList<ContainerDetailModel> = ArrayList()

    init {
        containerDetailModel.add(ContainerDetailModel("3254689 ","7 Doors", "300 Kg","5","Fe,Cu",  "Rush","1:00am - 1:45pm","Lorem ipsum dolor sit , ectetur adipiscing slit, do eiusmod tempor." ))
        containerDetailModel.add(ContainerDetailModel("32324289 ","3 Doors", "500 Kg","6","Zn,Cu",  "Rush","2:00am - 2:45pm","Lorem ipsum dolor sit , ectetur adipiscing slit, do eiusmod tempor." ))
        containerDetailModel.add(ContainerDetailModel("53354689 ","2 Doors", "370 Kg","7","Au,Ni",  "Rush","5:00am - 5:45pm","Lorem ipsum dolor sit , ectetur adipiscing slit, do eiusmod tempor." ))
        containerDetailModel.add(ContainerDetailModel("905394 ","6 Doors", "160 Kg","8","He,Cu",  "Rush","4:00am - 4:45pm","Lorem ipsum dolor sit , ectetur adipiscing slit, do eiusmod tempor." ))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerItemHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.content_container_detail, parent, false)
        return ContainerItemHolder(view)
    }

    override fun getItemCount(): Int {
        return containerDetailModel.size
    }

    private fun getItem(position: Int): ContainerDetailModel {
        return containerDetailModel[position]
    }

    override fun onBindViewHolder(
        holder: ContainerDetailAdapter.ContainerItemHolder,
        position: Int
    ) {
        holder.bind(getItem(position))

    }

    class ContainerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewContainerNumber: TextView = view.textViewContainerId
        var textViewContainerItem: TextView = view.textViewContainerItemid
        var textViewWeight: TextView = view.textViewContainerItemwghtValue
        var textViewItemPoLots: TextView = view.textViewContainerItemPoLotsId
        var textViewMix: TextView = view.textViewContainerMixId
        var textViewLunch: TextView = view.textViewLunchTimeId
        var textViewRush: TextView = view.textViewRushId
        var textViewNotes: TextView = view.textViewNotesId

        fun bind(containerData : ContainerDetailModel){
            textViewContainerNumber.text = containerData.ContainerNo
            textViewContainerItem.text = containerData.ContainerItem
           textViewWeight.text = containerData.Weight
           textViewItemPoLots.text = containerData.ItemPoLots
            textViewMix.text = containerData.Mix
            textViewLunch.text = containerData.LunchTime
            textViewRush.text = containerData.Rush
            textViewNotes.text = containerData.Notes
        }
    }

}