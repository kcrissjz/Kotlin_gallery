package com.kcriss.gallery.ui.photo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kcriss.gallery.R
import com.kcriss.gallery.model.PhotoItem
import kotlinx.android.synthetic.main.fragment_page_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class PagePhotoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val photoList  = arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        val textPosition : Int? = arguments?.getInt("PHOTO_POSITION")
        PagerPhotoListAdapter().also {
            viewPager2.adapter = it
            it.submitList(photoList)

        }
        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = "${position+1}/${photoList?.size}"
            }
        })
        viewPager2.setCurrentItem(textPosition?:0,false)

        saveButton.setOnClickListener{
            if (Build.VERSION.SDK_INT<29 && ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            }else{
                viewLifecycleOwner.lifecycleScope.launch { savePhoto() }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    viewLifecycleOwner.lifecycleScope.launch { savePhoto() }
                }else{
                    Toast.makeText(requireContext(),"失败",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private suspend fun savePhoto() {
        withContext(Dispatchers.IO){
            val holder =
                (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem)
                        as PagerPhotoViewHolder

            val toBitmap = holder.itemView.imageView.drawable.toBitmap()
//        if (MediaStore.Images.Media.insertImage(requireContext().contentResolver,toBitmap,"","") == null) {
//            Toast.makeText(requireContext(),"失败",Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(requireContext(),"成功",Toast.LENGTH_SHORT).show()
//        }
            val saveUri :Uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )?: kotlin.run {
                MainScope().launch { Toast.makeText(requireContext(),"失败",Toast.LENGTH_SHORT).show() }
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if(toBitmap.compress(Bitmap.CompressFormat.JPEG,90,it)){
                    MainScope().launch { Toast.makeText(requireContext(),"成功",Toast.LENGTH_SHORT).show() }
                }else{
                    MainScope().launch { Toast.makeText(requireContext(),"失败",Toast.LENGTH_SHORT).show() }
                }
            }
        }

    }
}
