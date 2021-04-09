package com.quickhandslogistics.adapters.workSheet

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailNoteImageContract
import kotlinx.android.synthetic.main.item_work_detail_note_image.view.*


class WorkSheetDetailsNoteImageAdapter(private var onAdapterClick: WorkSheetItemDetailNoteImageContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<WorkSheetDetailsNoteImageAdapter.ViewHolder>() {

    private var imageStringArray :ArrayList<String> = ArrayList()
    private var isCompleteOrCancel: Boolean =false

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
        private val imageViewRemoveImage: ImageView = view.imageViewRemoveImage
        private val progressBar: ProgressBar = view.progressBar

        fun bind(image: String) {
            progressBar.visibility=View.VISIBLE
            Glide.with(context)
                .load(image)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility=View.GONE
                        return false
                    }

                })
                .into(noteImage)

            imageViewRemoveImage.visibility=if (isCompleteOrCancel) View.GONE else View.VISIBLE
            mainRootLayout.setOnClickListener(this)
            imageViewRemoveImage.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    mainRootLayout.id -> {
                    val imageUrl=getItem(adapterPosition)
                        onAdapterClick.onImageClick(imageUrl)
                    }
                    imageViewRemoveImage.id -> {
                        imageStringArray.removeAt(adapterPosition)
                        notifyDataSetChanged()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun getImageArrayList(): ArrayList<String> {
        return imageStringArray
    }

    fun updateList(imageStringArray: ArrayList<String>, isCompleteOrCancel: Boolean) {
        this.imageStringArray.clear()
        this.isCompleteOrCancel=isCompleteOrCancel
        this.imageStringArray.addAll(imageStringArray)
        notifyDataSetChanged()
    }
}
