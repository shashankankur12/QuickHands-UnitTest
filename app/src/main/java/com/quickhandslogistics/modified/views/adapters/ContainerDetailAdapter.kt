package com.quickhandslogistics.modified.views.adapters

import android.app.Activity
import android.text.Spanned
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
        containerDetailModel.add(ContainerDetailModel("Container No ","3254689"))
        containerDetailModel.add(ContainerDetailModel("Container Item ","7 Doors"))
        containerDetailModel.add(ContainerDetailModel("Weight ","370 Kg"))
        containerDetailModel.add(ContainerDetailModel("Item PO Lots ","5"))
        containerDetailModel.add(ContainerDetailModel("Mix ","Fe, Cu"))
        containerDetailModel.add(ContainerDetailModel("Rush ","5:00am - 5:45am"))
        containerDetailModel.add(ContainerDetailModel("Notes ","Lorem ipsum "))
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

    override fun onBindViewHolder(holder: ContainerItemHolder,
        position: Int) { holder.bind(getItem(position))

    }

    class ContainerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewContainerNumberKey: TextView = view.textViewContainerno
        var textViewContainerNumberValue: TextView = view.textViewContainerId

        fun bind(containerData : ContainerDetailModel){

            textViewContainerNumberKey.text = containerData.key
            textViewContainerNumberValue.text = containerData.value
        }
    }

}