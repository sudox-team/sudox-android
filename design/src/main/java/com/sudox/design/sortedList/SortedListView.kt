package com.sudox.design.sortedList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getFontOrThrow
import androidx.core.content.res.use
import androidx.recyclerview.widget.RecyclerView
import com.sudox.design.R
import com.sudox.design.sortedList.decorations.StickyLettersDecoration
import com.sudox.design.sortedList.decorations.StickyLettersProvider

class SortedListView : RecyclerView {

    private var lettersDecoration: StickyLettersDecoration? = null

    internal val lettersPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    internal var lettersRightPadding = 0
    internal var lettersTopPadding = 0
    internal var lettersMargin = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.sortedListViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.SortedListView, defStyleAttr, 0).use {
            lettersPaint.textSize = it.getDimensionPixelSizeOrThrow(R.styleable.SortedListView_lettersTextSize).toFloat()
            lettersPaint.color = it.getColorOrThrow(R.styleable.SortedListView_lettersColor)
            lettersPaint.typeface = it.getFontOrThrow(R.styleable.SortedListView_lettersFont)

            lettersRightPadding = it.getDimensionPixelSizeOrThrow(R.styleable.SortedListView_lettersRightPadding)
            lettersTopPadding = it.getDimensionPixelSizeOrThrow(R.styleable.SortedListView_lettersTopPadding)
            lettersMargin = it.getDimensionPixelSizeOrThrow(R.styleable.SortedListView_lettersMargin)
        }
    }

    fun setLettersProvider(provider: StickyLettersProvider) {
        if (lettersDecoration != null) {
            removeItemDecoration(lettersDecoration!!)
        }

        lettersDecoration = StickyLettersDecoration(this, provider, context)
        addItemDecoration(lettersDecoration!!)
    }
}