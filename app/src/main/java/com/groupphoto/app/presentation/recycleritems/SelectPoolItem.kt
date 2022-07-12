package com.groupphoto.app.presentation.recycleritems

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.text.isDigitsOnly
import com.groupphoto.app.R
import com.pawegio.kandroid.e
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_pool_select.view.*

class SelectPoolItem(private var poolName: String?, private var colorTag: String) :
    Item<GroupieViewHolder>() {

    val regex = "^[A-Za-z]*$".toRegex()
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.img_bg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorTag))

            if (poolName!!.contains(" ")){

                var initials = poolName!!.split(" ")
                e("my log ${initials[0]}")

//                var myInitial= initials[0].toCharArray()[0].toString().toUpperCase() + initials[1].toCharArray()[0].toString().toUpperCase()
//                var myInitial2= initials[0].substring(0,1).toUpperCase() + initials[1].substring(0,1).toUpperCase()
                var initialOne = initials[0].substring(0,1)
                var initialTwo = initials[1].substring(0,1)




                var finalInitial = ""
                if (regex.matches(initialOne)|| initialOne.isDigitsOnly())
                    finalInitial += initialOne

                if (regex.matches(initialTwo)|| initialTwo.isDigitsOnly())
                    finalInitial += initialTwo

                itemView.img_initial.text = finalInitial.toUpperCase()
            }
            else {

                var singleInitial = poolName!!.substring(0,1)
                if (regex.matches(singleInitial)|| singleInitial.isDigitsOnly())
                itemView.img_initial.text = singleInitial.toUpperCase()
            }
            itemView.pool_name.text = poolName
        }
    }

    override fun getLayout(): Int = R.layout.item_pool_select
}