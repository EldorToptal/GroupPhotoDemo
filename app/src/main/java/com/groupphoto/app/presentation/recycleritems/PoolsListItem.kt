package com.groupphoto.app.presentation.recycleritems

import com.groupphoto.app.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_pool_list.view.*

class PoolsListItem(private var poolName: String?, private var image: Int) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.pool_list_image.setImageResource(image)
            itemView.pool_list_name.text = poolName
        }
    }

    override fun getLayout(): Int = R.layout.item_pool_list
}