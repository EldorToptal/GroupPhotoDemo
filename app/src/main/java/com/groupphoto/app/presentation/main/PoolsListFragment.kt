package com.groupphoto.app.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.data.remote.model.PoolItem
import com.groupphoto.app.presentation.main.pools.GalleryActivity
import com.groupphoto.app.presentation.main.pools.NewPoolActivity
import com.groupphoto.app.presentation.recycleritems.SelectPoolItem
import com.groupphoto.app.presentation.viewmodels.PoolsListEvent
import com.groupphoto.app.presentation.viewmodels.PoolsListViewModel
import com.pawegio.kandroid.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_pool_list.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.viewModel

class PoolsListFragment : Fragment() {

    private val viewModel: PoolsListViewModel by viewModel()
    private var poolListAdapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_pool_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is PoolsListEvent.OnGetPoolsList -> onGetPoolsList(event.pools)
            }
        })


        viewModel.getPoolsList()
        poolListAdapter = GroupAdapter<GroupieViewHolder>().apply {
            setOnItemClickListener { item, view ->
                requireActivity().startActivity<GalleryActivity>()
            }
        }
        pool_list_recycler.adapter = poolListAdapter


        new_pool_constraints.setOnClickListener {
            requireActivity().startActivity<NewPoolActivity>()
        }
//        poolListAdapter.addAll(UI.pools)

    }


    private fun onGetPoolsList(pools: MutableList<PoolItem>?){
        e("success get pools list")


        if ( pools != null){
            for ( item in pools){
                if (item.title == "Home"){
                    if (item.colorTag != null)
                        poolListAdapter.add(0,SelectPoolItem(item.title, item.colorTag))
                    else
                        poolListAdapter.add(0,SelectPoolItem(item.title, "#1A88FF"))

                }
                else {
                    if (item.colorTag != null)
                        poolListAdapter.add(SelectPoolItem(item.title, item.colorTag))
                    else
                        poolListAdapter.add(SelectPoolItem(item.title, "#1A88FF"))


                }
            }
        }
    }
    companion object {
        private const val TAG = "Rings Fragment"
    }
}