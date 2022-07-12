package com.groupphoto.app.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.groupphoto.app.R
import com.groupphoto.app.util.layoutmanagers.DisplayUtil

class GroupPhotoToolbar : LinearLayout {
    var title : String? = ""
    var titleColor : Int = 0
    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, attrSetId: Int) : super(context, attrs, attrSetId) {
        initial(attrs)
    }

    fun initial(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GroupPhotoToolbar)
        title = typedArray.getString(R.styleable.GroupPhotoToolbar_tbTitle)
        titleColor = typedArray.getColor(
            R.styleable.GroupPhotoToolbar_tbTitleColor,
            ContextCompat.getColor(context, R.color.GrayscaleMineShaft)
        )

        val lpChildren = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lpChildren.gravity = Gravity.CENTER_VERTICAL

        val ivBack = ImageView(context)
        ivBack.setImageResource(R.drawable.ic_back)
        ivBack.scaleX = 0.6f
        ivBack.scaleY = 0.6f
        lpChildren.width = DisplayUtil.dpToPx(context, 24f)
        lpChildren.height = DisplayUtil.dpToPx(context, 24f)
        lpChildren.setMargins(DisplayUtil.dpToPx(context, 16f), 0, 0, 0)
        ivBack.layoutParams = lpChildren
        addView(ivBack)
        typedArray.recycle()
    }
}