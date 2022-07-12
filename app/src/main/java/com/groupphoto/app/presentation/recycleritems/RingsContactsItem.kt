package com.groupphoto.app.presentation.recycleritems

import com.groupphoto.app.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_ring_contact.view.*

class RingsContactsItem(private var contactName: String?, private var image: Int) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.contact_image.setImageResource(image)
            itemView.contact_name.text = contactName
        }
    }

    override fun getLayout(): Int = R.layout.item_ring_contact
}