package com.groupphoto.app.presentation.recycleritems

import com.groupphoto.app.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_local_contact.view.*

class LocalContactsItem(private var contactName: String?, private var image: Int) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.local_contact_image.setImageResource(image)
            itemView.local_contact_name.text = contactName
        }
    }

    override fun getLayout(): Int = R.layout.item_local_contact
}