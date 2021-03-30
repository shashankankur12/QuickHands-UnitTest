package com.quickhandslogistics.adapters.workSheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailNoteImageContract
import kotlinx.android.synthetic.main.item_work_detail_note_image.view.*
import java.io.File


class WorkSheetDetailsNoteImageAdapter(private var onAdapterClick: WorkSheetItemDetailNoteImageContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<WorkSheetDetailsNoteImageAdapter.ViewHolder>() {

    private var imageStringArray = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work_detail_note_image, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return imageStringArray.size
    }

    private fun getItem(position: Int): String {
        return imageStringArray[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val mainRootLayout: ConstraintLayout = view.mainRootLayout
        private val noteImage: ImageView = view.noteImage


        fun bind(image: String) {
            val file = File(image)

            Glide.with(context)
                .load(file.path).error(R.drawable.dummy)
                .into(noteImage)

            mainRootLayout.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    mainRootLayout.id -> {
                    val imageUrl=getItem(adapterPosition)
                        onAdapterClick.onImageClick(imageUrl)
                    }
                }
            }
        }
    }

    fun updateList(imageStringArray: ArrayList<String>) {
        this.imageStringArray.clear()
        this.imageStringArray.addAll(imageStringArray)
        notifyDataSetChanged()
    }
}
