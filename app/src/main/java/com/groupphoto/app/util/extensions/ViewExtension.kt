package com.groupphoto.app.util.extensions

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

fun ViewGroup.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}