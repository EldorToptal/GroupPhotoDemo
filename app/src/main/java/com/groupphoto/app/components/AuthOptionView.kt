package com.groupphoto.app.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.groupphoto.app.R
import com.groupphoto.app.util.Constants.INVALID_RESOURCE_ID
import com.groupphoto.app.util.layoutmanagers.DisplayUtil
import org.jetbrains.anko.textColor


class AuthOptionView : FrameLayout {
    var authIcon = 0
    var title: String? = ""
    var textSize: Float = 16f
    var textColor = 0

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

    companion object {
        private const val KOEF = 0.1f
    }

    private fun init(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AuthOptionView)
        textSize = typedArray.getDimension(
            R.styleable.AuthOptionView_authTextSize,
            DisplayUtil.dpToPx(context, 14f).toFloat()
        )
        title = typedArray.getString(R.styleable.AuthOptionView_authTitle)
        textColor = typedArray.getColor(
            R.styleable.AuthOptionView_authTextColor,
            ContextCompat.getColor(context, R.color.GrayscaleMineShaft)
        )
        authIcon =
            typedArray.getResourceId(R.styleable.AuthOptionView_authIcon, INVALID_RESOURCE_ID)

        val lpParent = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        lpParent.height = WRAP_CONTENT
        val llParent = LinearLayout(context)
        llParent.setPadding(0, DisplayUtil.dpToPx(context, 4f), 0, DisplayUtil.dpToPx(context, 4f))
        llParent.layoutParams = lpParent
        llParent.isClickable = true
        llParent.isFocusable = true
        llParent.background = ContextCompat.getDrawable(context, R.drawable.bg_border_gray_4dp)
        llParent.orientation = LinearLayout.HORIZONTAL
        val lpChildren = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lpChildren.gravity = Gravity.CENTER_VERTICAL

        val ivIcon = ImageView(context)
        ivIcon.setImageResource(authIcon)
        ivIcon.scaleX = 0.6f
        ivIcon.scaleY = 0.6f
        lpChildren.width = DisplayUtil.dpToPx(context, 24f)
        lpChildren.height = DisplayUtil.dpToPx(context, 24f)
        lpChildren.setMargins(DisplayUtil.dpToPx(context, 16f), 0, 0, 0)
        ivIcon.layoutParams = lpChildren
        llParent.addView(ivIcon)

        val tvTitle = TextView(context)
        tvTitle.text = title
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        tvTitle.textColor = ContextCompat.getColor(context, R.color.GrayscaleMineShaft)
        tvTitle.typeface = ResourcesCompat.getFont(context, R.font.book)
        lpChildren.setMargins(DisplayUtil.dpToPx(context, 8f), 0, 0, 0)
        lpChildren.width = WRAP_CONTENT
        lpChildren.height = WRAP_CONTENT
        lpChildren.gravity = Gravity.CENTER_VERTICAL
        tvTitle.layoutParams = lpChildren
        llParent.addView(tvTitle)

        llParent.invalidate()

        typedArray.recycle()

//        setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
////                    drawBitmapWithMask()
//                }
//                MotionEvent.ACTION_UP -> {
////                    drawBitmap()
//                }
//            }
//            false
//        }
        addView(llParent)
    }


}