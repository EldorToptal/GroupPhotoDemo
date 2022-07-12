package com.groupphoto.app.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.groupphoto.app.R
import com.groupphoto.app.databinding.ViewEditTextBinding
import com.groupphoto.app.util.extensions.hide
import com.groupphoto.app.util.extensions.show
import android.text.InputFilter

import android.text.Spanned
import com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
import com.groupphoto.app.util.extensions.invisible


class GroupPhotoEditText : FrameLayout {
    private lateinit var binding: ViewEditTextBinding
    private var INPUT_TEXT = 0
    private var INPUT_EMAIL = 1
    private var INPUT_PHONE = 2
    private var INPUT_MONEY = 3
    private var INPUT_PASSWORD = 4
    private var inputType = INPUT_TEXT
    private var hint = ""
    private var isRequired = true

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
        binding = ViewEditTextBinding.inflate(LayoutInflater.from(context))

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GroupPhotoEditText)
        inputType = typedArray.getInt(R.styleable.GroupPhotoEditText_etInputTypes, INPUT_TEXT)
        hint = typedArray.getString(R.styleable.GroupPhotoEditText_etHint) ?: ""
        isRequired = typedArray.getBoolean(R.styleable.GroupPhotoEditText_etRequired, true)

        binding.teGroupPhoto.hint = hint

        binding.teGroupPhoto.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                binding.llBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.border_blue_4dp)
            else {
                binding.llBackground.background =
                    ContextCompat.getDrawable(context, R.drawable.border_grey_4dp)
                validateType()
            }
        }

        binding.teGroupPhoto.doAfterTextChanged {
            if (binding.tvError.isShown) {
                validateType()
            }
        }

        when (inputType) {
            INPUT_EMAIL -> {
                binding.ivInputType.setImageResource(R.drawable.ic_letter)
                binding.teGroupPhoto.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            INPUT_PHONE -> {
                binding.ivInputType.setImageResource(R.drawable.ic_phone)
                binding.teGroupPhoto.inputType = InputType.TYPE_CLASS_PHONE
            }
            INPUT_PASSWORD -> {
                binding.ivInputType.setImageResource(R.drawable.ic_lock)
                binding.tlGroupPhoto.setEndIconMode(END_ICON_PASSWORD_TOGGLE)
            }
        }

        typedArray.recycle()

        addView(binding.root)
    }

    fun validateType() {
        when (inputType) {
            INPUT_TEXT -> {
            }
            INPUT_EMAIL -> {
                val hasError = !binding.teGroupPhoto.text.toString().contains("@")
                handleError(
                    hasError,
                    context.getString(R.string.WarningEmailInvalid)
                )
            }
            INPUT_PHONE -> {
                val filter =
                    InputFilter { source, start, end, dest, dstart, dend ->
                        for (i in start until end) {
                            if (!Character.isDigit(source[i]) && !Character.isSpaceChar(source[i])) {
                                return@InputFilter ""
                            }
                        }
                        null
                    }
                binding.teGroupPhoto.setFilters(arrayOf(filter))
            }
            INPUT_MONEY -> {

            }
        }

        handleError(
            isRequired && binding.teGroupPhoto.text.toString().isEmpty(),
            context.getString(R.string.WarningRequiredField)
        )

    }

    fun handleError(hasError: Boolean, message: String = "Unknown") {
        if (hasError) {
            binding.ivError.show()
            binding.tvError.show()
            binding.llBackground.background =
                ContextCompat.getDrawable(context, R.drawable.border_red_4dp)
            binding.tvError.text = message
        } else {
            binding.tvError.text = ""
            binding.ivError.hide()
            binding.llBackground.background =
                if (binding.teGroupPhoto.hasFocus())
                    ContextCompat.getDrawable(context, R.drawable.border_blue_4dp)
                else
                    ContextCompat.getDrawable(context, R.drawable.border_grey_4dp)
            binding.tvError.invisible()
        }
    }

    fun hasError() : Boolean{
        return binding.tvError.isShown || getText().isEmpty()
    }

    fun getText() : String{
        return binding.teGroupPhoto.text.toString()
    }

}