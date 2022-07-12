package com.groupphoto.app.domain

import com.groupphoto.app.R
import com.groupphoto.app.presentation.recycleritems.RingsContactsItem
import com.groupphoto.app.presentation.recycleritems.SelectPoolItem

object UI {
    val contacts = listOf(
        RingsContactsItem("Aldwin David", R.drawable.sample_pool_ic),
        RingsContactsItem("Cardo Dalisay", R.drawable.sample_pool_ic2),
        RingsContactsItem("John Doe", R.drawable.sample_pool_ic),
        RingsContactsItem("Mogul Khan", R.drawable.sample_pool_ic2)
    )
    val pools = listOf(
        SelectPoolItem("Home", "#00FFFF"),
        SelectPoolItem("Scooby", "#00FFFF"),
        SelectPoolItem("My Boo", "#00FFFF"),
        SelectPoolItem("Justin Bieber","#00FFFF")
    )
    val poolsList = listOf(
        SelectPoolItem("Home", "#00FFFF"),
        SelectPoolItem("Scooby", "#00FFFF"),
        SelectPoolItem("My Boo", "#00FFFF"),
        SelectPoolItem("Justin Bieber", "#00FFFF")
    )

}