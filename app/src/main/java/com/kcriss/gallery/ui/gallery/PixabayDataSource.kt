package com.kcriss.gallery.ui.gallery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.kcriss.gallery.model.PhotoItem
import com.kcriss.gallery.model.Pixabay
import com.kcriss.gallery.utils.VolleySingleton

/**
 * Created by liujunzhe on 2020/6/10.
 */
enum class Networkstatus {
    INITIAL_LOADING,
    LOADING,
    LOADED,
    FAILED,
    COMPLETED
}

class PixabayDataSource(val context: Context) : PageKeyedDataSource<Int, PhotoItem>() {
    private val queryKey =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
    var retry: (() -> Any)? = null
    private val _networkStatus = MutableLiveData<Networkstatus>()
    val netWorkStatus: LiveData<Networkstatus> = _networkStatus

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        retry = null
        _networkStatus.postValue(Networkstatus.INITIAL_LOADING)
        val url =
            "https://pixabay.com/api/?key=16945284-328ca081f55aa972b531f47ef&q=${queryKey.random()}&per_page=50&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {

                val dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(dataList, null, 2)
                _networkStatus.postValue(Networkstatus.LOADED)

            },
            Response.ErrorListener {
                if (it.toString() == "com.android.volley.ClientError") {
                    _networkStatus.postValue(Networkstatus.COMPLETED)
                } else {
                    retry = { loadInitial(params, callback) }
                    _networkStatus.postValue(Networkstatus.FAILED)
                }
                Log.d("hello", " loadInitData:" + it)
            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        retry = null
        _networkStatus.postValue(Networkstatus.LOADING)
        val url =
            "https://pixabay.com/api/?key=16945284-328ca081f55aa972b531f47ef&q=${queryKey.random()}&per_page=50&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {

                val dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(dataList, params.key + 1)
                _networkStatus.postValue(Networkstatus.LOADED)

            },
            Response.ErrorListener {
                if (it.toString() == "com.android.volley.ClientError") {
                    _networkStatus.postValue(Networkstatus.COMPLETED)
                } else {
                    retry = { loadAfter(params, callback) }
                    _networkStatus.postValue(Networkstatus.FAILED)
                }

                Log.d("hello", " loadInitData:" + it)

            }
        ).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
    }


}