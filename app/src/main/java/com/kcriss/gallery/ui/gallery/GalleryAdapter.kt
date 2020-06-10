package com.kcriss.gallery.ui.gallery

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kcriss.gallery.R
import com.kcriss.gallery.model.PhotoItem
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

/**
 * Created by liujunzhe on 2020/6/8.
 */
class GalleryAdapter(private val galleryViewModel: GalleryViewModel): PagedListAdapter <PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {
    private var hasFooter = false
    private var networkstatus:Networkstatus? = null
    init {
        galleryViewModel.retry()
    }
    fun updataNetworkstatus(networkstatus: Networkstatus?){
        this.networkstatus = networkstatus
        if (networkstatus == Networkstatus.INITIAL_LOADING) hideFooter() else showFooter()
    }
    private fun hideFooter(){
        if (hasFooter) {
            notifyItemRemoved(itemCount-1)
        }
        hasFooter = false
    }
    private fun showFooter(){
        if (hasFooter) {
            notifyItemChanged(itemCount-1)
        }else{
            hasFooter = true
            notifyItemInserted(itemCount-1)
        }
    }
    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount-1) R.layout.gallery_footer else R.layout.gallery_cell
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.gallery_cell ->PhotoViewHolder.newInstance(parent).also {holder ->
                holder.itemView.setOnClickListener {
                    Bundle().apply {
                        putParcelableArrayList("PHOTO_LIST", ArrayList(currentList!!))
                        putInt("PHOTO_POSITION", holder.layoutPosition)
                        holder.itemView.findNavController()
                            .navigate(R.id.action_galleryFragment_to_pagePhotoFragment, this)
                    }
                }
            }
            else -> FooterViewHolder.newInstance(parent).also {
                it.itemView.setOnClickListener {
                    galleryViewModel.retry()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            R.layout.gallery_footer -> (holder as FooterViewHolder).bindWithNetworkStatus(networkstatus)
            else ->{
                val photoItem = getItem(position)?:return
                (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)
            }
        }
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    companion object{
        fun newInstance(parent: ViewGroup):PhotoViewHolder{
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell,parent,false)
            return PhotoViewHolder(view)
        }
    }
    fun bindWithPhotoItem(photoItem: PhotoItem){
        with(itemView) {
            shimmerGalleryLayout.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            textViewUser.text = photoItem.photoUser
            textViewLikes.text = photoItem.photoLikes.toString()
            textViewFavo.text = photoItem.photoFavorites.toString()
            imageView.layoutParams.height = photoItem.photoHeight
            Glide.with(this)
                .load(photoItem.previewUrl)
                .placeholder(R.drawable.photo_placeholder)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false.also { itemView.shimmerGalleryLayout?.stopShimmerAnimation() }
                    }

                })
                .into(imageView)
        }
    }
}
class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    companion object{
        fun newInstance(parent: ViewGroup):FooterViewHolder{
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer,parent,false)
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return FooterViewHolder(view)
        }
    }
    fun bindWithNetworkStatus(networkstatus: Networkstatus?){
        when(networkstatus){
            Networkstatus.LOADING ->{
                itemView.progressBar.visibility = View.VISIBLE
                itemView.textView.text = "正在加载中..."
                itemView.isClickable = false
            }
            Networkstatus.FAILED -> {
                itemView.progressBar.visibility = View.GONE
                itemView.textView.text = "加载失败 点击重试！！"
                itemView.isClickable = true
            }
            else ->{
                itemView.progressBar.visibility = View.GONE
                itemView.textView.text = "全部加载完成～～"
                itemView.isClickable = false
            }
        }
    }
}