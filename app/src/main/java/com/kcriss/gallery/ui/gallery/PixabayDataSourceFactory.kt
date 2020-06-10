package com.kcriss.gallery.ui.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.kcriss.gallery.model.PhotoItem


/**
 * Created by liujunzhe on 2020/6/10.
 */
class PixabayDataSourceFactory(val context: Context) : DataSource.Factory<Int, PhotoItem>() {
    private val _pixabayDataSource = MutableLiveData<PixabayDataSource>()
    val pixabayDataSource: LiveData<PixabayDataSource> = _pixabayDataSource
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also {
            _pixabayDataSource.postValue(it)
        }
    }
}