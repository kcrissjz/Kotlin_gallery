package com.kcriss.gallery.ui.photo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kcriss.gallery.R
import com.kcriss.gallery.model.PhotoItem
import kotlinx.android.synthetic.main.pager_photo_view.view.*

/**
 * Created by liujunzhe on 2020/6/9.
 */
class PagerPhotoListAdapter :ListAdapter<PhotoItem, PagerPhotoViewHolder>(
    DIFFCALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
            LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent,false).apply {
                return PagerPhotoViewHolder(this)
            }
    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).fullUrl)
            .placeholder(R.drawable.photo_placeholder)
            .into(holder.itemView.imageView)
    }


    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }

    }

}

class PagerPhotoViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
