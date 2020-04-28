package com.quickhandslogistics.modified.adapters.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.ContainerModel.ContainerDetailModel
import kotlinx.android.synthetic.main.content_container_detail.view.*

class ContainerDetailAdapter : RecyclerView.Adapter<ContainerDetailAdapter.ViewHolder>() {

    var containerDetailModel: ArrayList<ContainerDetailModel> = ArrayList()

    init {
        containerDetailModel.add(ContainerDetailModel("Container No ", "3254689"))
        containerDetailModel.add(ContainerDetailModel("Container Item ", "7 Doors"))
        containerDetailModel.add(ContainerDetailModel("Weight ", "370 Kg"))
        containerDetailModel.add(ContainerDetailModel("Item PO Lots ", "5"))
        containerDetailModel.add(ContainerDetailModel("Mix ", "Fe, Cu"))
        containerDetailModel.add(ContainerDetailModel("Rush ", "5:00am - 5:45am"))
        containerDetailModel.add(
            ContainerDetailModel(
                "Notes ",
                "Lorem ipsum dolor sit , ectetur adipiscing slit, do eiusmod tempor."
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_container_detail, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return containerDetailModel.size
    }

    private fun getItem(position: Int): ContainerDetailModel {
        return containerDetailModel[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewHeader: TextView = view.textViewHeader
        var textViewValue: TextView = view.textViewValue

        fun bind(containerData: ContainerDetailModel) {
            textViewHeader.text = containerData.key
            textViewValue.text = containerData.value
        }
    }

}