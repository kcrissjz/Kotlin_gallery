package com.kcriss.gallery.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData


/**
 * Created by liujunzhe on 2020/6/8.
 */


class GalleryViewModel(application: Application) : AndroidViewModel(application) {
     val factory = PixabayDataSourceFactory(application)
     val networkStatus: LiveData<Networkstatus>
          get() = Transformations.switchMap(factory.pixabayDataSource) {it.netWorkStatus}
     val pageListLiveData = factory.toLiveData(1)

     fun resetQuery(){
          pageListLiveData.value?.dataSource?.invalidate()
     }
     fun retry(){
          factory.pixabayDataSource.value?.retry?.invoke()
     }
}