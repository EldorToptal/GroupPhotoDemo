package com.groupphoto.app.components

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.widget.AppCompatTextView
import com.groupphoto.app.R
import com.groupphoto.app.util.extensions.dp
import com.groupphoto.app.util.layoutmanagers.DisplayUtil
import com.groupphoto.app.util.extensions.updateColor


open class GroupPhotoButton : FrameLayout {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, attrSetId: Int) : super(
        context,
        attrs,
        attrSetId
    ) {
        initial(attrs)
    }


    private var textButton = object : AppCompatTextView(context) {
        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return false
        }
    }
        .apply {
            val myLayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            myLayoutParams.gravity = Gravity.CENTER
            layoutParams = myLayoutParams

        }
    private val progressBar: ProgressBar by lazy {
        return@lazy ProgressBar(context).apply {
            val myLayoutParams = FrameLayout.LayoutParams(
                DisplayUtil.dpToPx(context, 20f),
                DisplayUtil.dpToPx(context, 20f)
            )
            myLayoutParams.gravity = Gravity.CENTER
            layoutParams = myLayoutParams
        }
    }

    var ctext: String? = ""
    var dimension: Float = 18f
    var startColor = 0
    var endColor = 0
    var disabledColor = 0
    var disabledTextColor = 0
    var isWithStroke = false
    private var textColor = ContextCompat.getColor(context, android.R.color.white)
    var isButtonEnabled = true
    var isLoading = false
    private var rect = RectF()
    private var corner = 0f
    private var viewHeight = SUGGESTED_HEIGHT.dp
    private var viewWidth = SUGGESTED_WIDTH.dp

    companion object {
        private const val SHADOW_RADIUS = 12.5f
        private const val KOEF = 0.1f
        private const val SUGGESTED_WIDTH = 150
        private const val SUGGESTED_HEIGHT = 110
    }

    private var buttonPaint = Paint().apply {
        isAntiAlias = true
    }

    fun initial(attrs: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GroupPhotoButton)
        dimension = typedArray.getDimension(
            R.styleable.GroupPhotoButton_buttonTextSize,
            DisplayUtil.dpToPx(context, 14f).toFloat()
        )
        ctext = typedArray.getString(R.styleable.GroupPhotoButton_buttonText)
        startColor = typedArray.getColor(
            R.styleable.GroupPhotoButton_buttonColor,
            ContextCompat.getColor(context, R.color.colorPrimary)
        )
        textColor = typedArray.getColor(
            R.styleable.GroupPhotoButton_buttonTextColor,
            ContextCompat.getColor(context, android.R.color.white)
        )
        isWithStroke = typedArray.getBoolean(R.styleable.GroupPhotoButton_buttonWithStroke, false)

        disabledColor = ContextCompat.getColor(context, R.color.buttonDisabledColor)
        disabledTextColor = ContextCompat.getColor(context, R.color.buttonDisabledTextColor)

        textButton.text = ctext
        textButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension)
        textButton.gravity = Gravity.CENTER
        textButton.typeface = ResourcesCompat.getFont(context, R.font.medium)
        addView(textButton)

        typedArray.recycle()

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawBitmapWithMask()
                }
                MotionEvent.ACTION_UP -> {
                    drawBitmap()
                }
            }
            false
        }

        this.setPadding(
            0,
            DisplayUtil.dpToPx(context, 16f),
            0,
            DisplayUtil.dpToPx(context, 16f)
        )

    }

    fun initial(
        textSize: Float,
        text: String,
        startColor: Int = ContextCompat.getColor(context, R.color.colorPrimary),
        endColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    ) {
        dimension = textSize
        ctext = text
        this.startColor = startColor
        this.endColor = endColor


        disabledColor = ContextCompat.getColor(context, R.color.buttonDisabledColor)
        disabledTextColor = ContextCompat.getColor(context, R.color.buttonDisabledTextColor)

        textButton.text = ctext
        textButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension)
        textButton.gravity = Gravity.CENTER
        textButton.typeface = ResourcesCompat.getFont(context, R.font.medium)
        addView(textButton)

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawBitmapWithMask()
                }
                MotionEvent.ACTION_UP -> {
                    drawBitmap()
                }
            }
            false
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w > 0 && h > 0) {
            viewHeight = h
            viewWidth = if (SUGGESTED_WIDTH.dp > w) SUGGESTED_WIDTH.dp else w
            drawBitmap()
        }
    }

    private fun initParams() {
        isEnabled = isButtonEnabled
//        isFocusable = true
//        isClickable = true

        if (!isButtonEnabled) {
            buttonPaint.clearShadowLayer()
            buttonPaint.shader = LinearGradient(
                0f,
                0f,
                viewWidth.toFloat(),
                viewHeight.toFloat(),
                disabledColor,
                disabledColor,
                Shader.TileMode.CLAMP
            )
            textButton.setTextColor(disabledTextColor)
        } else {
//            buttonPaint.setShadowLayer(SHADOW_RADIUS.dp, 0f, 0f, ContextCompat.getColor(context, R.color.buttonShadowLayer))
            buttonPaint.shader = LinearGradient(
                0f,
                0f,
                0f,
                viewHeight.toFloat(),
                startColor,
                startColor,
                Shader.TileMode.CLAMP
            )
            textButton.setTextColor(textColor)
        }
    }

    private fun drawBitmap() {
        initParams()
        rect = RectF(
            0f,
            0f,
            viewWidth.toFloat(),
            viewHeight.toFloat()
        )
        corner = DisplayUtil.dpToPx(context, 4f).toFloat()

        val bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
        val canvasButton = Canvas(bitmap)
        canvasButton.drawRoundRect(rect, corner, corner, buttonPaint)
        val drawable = BitmapDrawable(context.resources, bitmap)

        background = drawable
    }

    private fun drawBitmapWithMask() {
        initParams()
        rect = RectF(
            0f,
            0f,
            viewWidth.toFloat(),
            viewHeight.toFloat()
        )
        corner = DisplayUtil.dpToPx(context, 4f).toFloat()

        val bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
        val canvasButton = Canvas(bitmap)
        canvasButton.drawRoundRect(rect, corner, corner, buttonPaint)
        val paint = Paint().apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.buttonMask)
        }
        canvasButton.drawRoundRect(rect, corner, corner, paint)
        val drawable = BitmapDrawable(context.resources, bitmap)

        background = drawable
    }

    fun setText(text: String) {
        textButton.text = text
    }

    fun setText(resId: Int) {
        textButton.text = context.getString(resId)
    }

    fun disable() {
        isButtonEnabled = false
//        isClickable = false
        isEnabled = false
        if (background != null) {
            drawBitmap()
        }
    }

    fun enable() {
        isButtonEnabled = true
//        isClickable = true
        isEnabled = true
        drawBitmap()
    }

    public fun showLoading() {
        isLoading = true
//        isClickable = false
        isEnabled = false
        if (indexOfChild(progressBar) == -1)
            addView(progressBar)
        progressBar.post {
            progressBar.updateColor(R.color.white)
        }
        textButton.visibility = View.GONE
    }

    public fun hideLoading() {
        isLoading = false
//        isClickable = true
        isEnabled = true
        if (indexOfChild(progressBar) != -1)
            removeView(progressBar)
        textButton.visibility = View.VISIBLE
    }
}