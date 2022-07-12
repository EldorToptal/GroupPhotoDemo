package com.groupphoto.app.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.BackupOptions
import com.groupphoto.app.databinding.ItemSubscriptionSelectBinding
import com.groupphoto.app.util.extensions.hide
import com.groupphoto.app.util.layoutmanagers.DisplayUtil

class GroupPhotoSelectionView : FrameLayout {

    private lateinit var binding: ItemSubscriptionSelectBinding
    private lateinit var titles: Array<String>
    private lateinit var subtitles: Array<String>
    private lateinit var prices: Array<String>
    private lateinit var parentLayout: LinearLayout
    private val INVALID_RESOURCE_ID = -1
    private var viewType = ViewType.TITLE
    var selectionListener: OnItemSelectedListener? = null
    private var selectedPosition = 0

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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GroupPhotoSelectionView)
        val viewTypeAttrs = typedArray.getInt(
            R.styleable.GroupPhotoSelectionView_selectionTypes,
            INVALID_RESOURCE_ID
        )
        viewType = when (viewTypeAttrs) {
            0 -> ViewType.TITLE
            1 -> ViewType.SUBSCRIPTION
            else -> ViewType.TITLE
        }

        val titlesId =
            typedArray.getResourceId(
                R.styleable.GroupPhotoSelectionView_titles,
                INVALID_RESOURCE_ID
            )
        if (titlesId != 0) {
            titles = resources.getStringArray(titlesId)
        }
        if (viewType == ViewType.SUBSCRIPTION) {
            val subTitlesId =
                typedArray.getResourceId(
                    R.styleable.GroupPhotoSelectionView_subtitles,
                    INVALID_RESOURCE_ID
                )
            if (subTitlesId != 0) {
                subtitles = resources.getStringArray(subTitlesId)
            }
            val pricesId =
                typedArray.getResourceId(
                    R.styleable.GroupPhotoSelectionView_prices,
                    INVALID_RESOURCE_ID
                )
            if (pricesId != 0) {
                prices = resources.getStringArray(pricesId)
            }
        }

        val lpParent = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        parentLayout = LinearLayout(context)
        parentLayout.orientation = LinearLayout.VERTICAL
        addView(parentLayout)
        val lpSelectionOption = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        lpSelectionOption.setMargins(0, DisplayUtil.dpToPx(context, 8f), 0, 0)
        for (counter in 0 until titles.size) {
            val itemBinding =
                ItemSubscriptionSelectBinding.inflate(LayoutInflater.from(context), this, false)
            itemBinding.tvTitle.text = titles[counter]
            if (viewType == ViewType.SUBSCRIPTION) {
                itemBinding.tvSubtitle.text = subtitles[counter]
                itemBinding.tvSubscriptionPrice.text = prices[counter]
            } else {
                itemBinding.tvSubtitle.hide()
                itemBinding.tvSubscriptionPrice.hide()
            }
            itemBinding.root.tag = counter
            itemBinding.root.layoutParams = lpSelectionOption
            itemBinding.root.setOnClickListener(::onOptionClicked)
            itemBinding.ivCheck.setImageResource(R.drawable.ic_off)
            if (counter == 0)
                onOptionClicked(itemBinding.root)
            parentLayout.addView(itemBinding.root)
        }

        typedArray.recycle()
    }

    private fun onOptionClicked(view: View) {
        val rootView = view as ViewGroup
        val position = view.tag as Int
        selectedPosition = position
        selectionListener?.onItemSelected(position)
        changeToDefaultState()
        (rootView.getChildAt(0) as ImageView).setImageResource(R.drawable.ic_on)
        ((rootView.getChildAt(1) as LinearLayout).getChildAt(0) as TextView).typeface =
            ResourcesCompat.getFont(context, R.font.medium)
    }


    private fun changeToDefaultState() {
        parentLayout.children.forEach { view ->
            ((view as ViewGroup).getChildAt(0) as ImageView).setImageResource(R.drawable.ic_off)
            (((view as ViewGroup).getChildAt(1) as LinearLayout).getChildAt(0) as TextView).typeface =
                ResourcesCompat.getFont(context, R.font.book)

        }
    }

    fun getSelectedOption(): BackupOptions {
        return when (selectedPosition) {
            0 -> BackupOptions.ENTIRE_LIBRARY
            1 -> BackupOptions.HUNDRED_PHOTOS
            2 -> BackupOptions.FROM_NOW_ON
            3 -> BackupOptions.DO_NOT_BACK_UP
            else -> {
                BackupOptions.ENTIRE_LIBRARY
            }
        }
    }

    fun selectBackUpOption(backupOptions: BackupOptions) {
        if (titles.size == 4) {
            when (backupOptions) {
                BackupOptions.ENTIRE_LIBRARY -> {
                    onOptionClicked(parentLayout.getChildAt(0))
                }
                BackupOptions.HUNDRED_PHOTOS -> {
                    onOptionClicked(parentLayout.getChildAt(1))
                }
                BackupOptions.FROM_NOW_ON -> {
                    onOptionClicked(parentLayout.getChildAt(2))
                }
                BackupOptions.DO_NOT_BACK_UP -> {
                    onOptionClicked(parentLayout.getChildAt(3))
                }
            }
        }
    }

    enum class ViewType {
        SUBSCRIPTION,
        TITLE,
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }
}