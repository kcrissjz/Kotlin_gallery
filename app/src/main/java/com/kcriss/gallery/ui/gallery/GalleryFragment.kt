package com.kcriss.gallery.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kcriss.gallery.R
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {
    val TAG = "GalleryFragment"
    private val galleryViewModel by viewModels<GalleryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        val galleryAdapter = GalleryAdapter(galleryViewModel)
        recycleView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        galleryViewModel.pageListLiveData.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)
        })
        swiperRefresh.setOnRefreshListener {
            galleryViewModel.resetQuery()
        }
        galleryViewModel.networkStatus.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onActivityCreated: "+it)
            galleryAdapter.updataNetworkstatus(it)
            swiperRefresh.isRefreshing = it == Networkstatus.INITIAL_LOADING
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeindcator -> {
                swiperRefresh.isRefreshing = true
                galleryViewModel.resetQuery()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
