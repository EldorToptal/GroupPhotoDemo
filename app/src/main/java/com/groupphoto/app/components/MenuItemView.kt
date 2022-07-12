package com.groupphoto.app.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.groupphoto.app.R
import com.groupphoto.app.util.Constants.INVALID_RESOURCE_ID
import com.groupphoto.app.util.extensions.addRipple
import com.groupphoto.app.util.layoutmanagers.DisplayUtil

class MenuItemView : LinearLayout {
    private var title: String? = ""
    private var icon: Int = 0

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, attrSetId: Int) : super(
        context,
        attrs,
        attrSetId
    ) {
        init(attrs)
    }

    fun init(attrs: AttributeSet) {
        this.orientation = HORIZONTAL
        setPadding(
            DisplayUtil.dpToPx(context, 12f),
            DisplayUtil.dpToPx(context, 12f),
            DisplayUtil.dpToPx(context, 12f),
            DisplayUtil.dpToPx(context, 12f),
        )
        isClickable = true
        isFocusable = true
        this.addRipple()
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView)
        title = typedArray.getString(R.styleable.MenuItemView_menuTitle)
        icon = typedArray.getResourceId(R.styleable.MenuItemView_menuIcon, INVALID_RESOURCE_ID)


        val lpIcon = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lpIcon.gravity = Gravity.CENTER_VERTICAL
        lpIcon.width = DisplayUtil.dpToPx(context, 24f)
        lpIcon.height = DisplayUtil.dpToPx(context, 24f)
        lpIcon.setMargins(0, 0, DisplayUtil.dpToPx(context, 8f), 0)

        val ivIcon = ImageView(context)
        ivIcon.layoutParams = lpIcon
        ivIcon.setImageResource(icon)

        val lpTitle = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        lpTitle.gravity = Gravity.CENTER_VERTICAL

        val tvTitle = TextView(context)
        tvTitle.setTextAppearance(R.style.TextStyleMenuItem)
        tvTitle.layoutParams = lpTitle
        tvTitle.text = title

        addView(ivIcon)
        addView(tvTitle)

    }

}