package com.sudox.messenger.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.*
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.messages.R
import kotlin.math.max


class DialogItemView : ViewGroup {

    private val nameView = AppCompatTextView(context).apply { addView(this) }
    private val contentTextView = AppCompatTextView(context).apply { addView(this) }
    private val photoView = CircleImageView(context).apply { addView(this) }
    private val dateView = AppCompatTextView(context).apply { addView(this) }
    private val countMessagesView = AppCompatTextView(context).apply { addView(this) }

    private var imageHeight = 0
    private var imageWidth = 0
    private var imageActiveColor = 0
    private var imageActiveRadius = 0
    private var imageActiveInnerRadius = 0
    private var messageStatusIcon: Drawable? = null

    private var innerImageToTextMargin = 0
    private var innerDialogNameToTopMargin = 0
    private var innerDialogNameToContentMargin = 0
    private var innerDateToTopMargin = 0
    private var innerDateToCountMargin = 0
    private var innerMessageStatusMargin = 0
    private var innerContentToRightViewMargin = 0

    private var isNewMessage = true

    private val countMessagesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintCountColor = 0
    private var countMessagesRect = RectF()
    private var countMessagesHeight = 0
    private var countMessagesRadius = 0
    private var innerCounterHorizontalMargin = 0
    private var innerCounterVerticalMargin = 0

    private var dialogContentTextAppearance = 0
    private var dialogContentNewTextAppearance = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.DialogItemView, defStyleAttr, 0).use {
            setTextAppearance(nameView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNameTextAppearance))
            setTextAppearance(dateView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogDateTextAppearance))
            dialogContentTextAppearance = it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogContentTextAppearance)
            dialogContentNewTextAppearance = it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNewContentTextAppearance)
            setTextAppearance(countMessagesView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogMessageCountTextAppearance))

            imageHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageHeight)
            imageWidth = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageWidth)

            imageActiveColor = it.getColorOrThrow(R.styleable.DialogItemView_imageActiveColor)
            imageActiveRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageActiveRadius)
            imageActiveInnerRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageActiveInnerRadius)
            messageStatusIcon = it.getDrawableOrThrow(R.styleable.DialogItemView_messageStatusIcon)

            innerImageToTextMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerImageToTextMargin)
            innerDialogNameToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDialogNameToTopMargin)
            innerDialogNameToContentMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDialogNameToContentMargin)
            innerDateToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDateToTopMargin)
            innerDateToCountMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDateToCountMargin)
            innerMessageStatusMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerMessageStatusMargin)
            innerContentToRightViewMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerContentToRightViewMargin)
            innerCounterHorizontalMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesInnerHorizontalMargin)
            innerCounterVerticalMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesInnerVerticalMargin)

            countMessagesHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesHeight)
            countMessagesRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_countMessagesRadius)

            paintCountColor = it.getColorOrThrow(R.styleable.DialogItemView_messageCountColor)
        }

        textViewSettings()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(dateView, widthMeasureSpec, heightMeasureSpec)
        measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoView, widthMeasureSpec, heightMeasureSpec)
        if (isNewMessage) {
            measureChild(countMessagesView, widthMeasureSpec, heightMeasureSpec)
        }

        val contentTextWidth = availableWidth - paddingLeft - imageWidth - innerImageToTextMargin  - innerContentToRightViewMargin - max(innerCounterHorizontalMargin * 2 + countMessagesView.measuredWidth, dateView.measuredWidth)- paddingRight

        measureChild(contentTextView, MeasureSpec.makeMeasureSpec(contentTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
        measureChild(nameView, MeasureSpec.makeMeasureSpec(contentTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec)

        val needHeight = paddingTop + nameView.measuredHeight + innerDialogNameToContentMargin + max(contentTextView.measuredHeight, photoView.measuredHeight) + paddingBottom
        val needWidth = paddingLeft + photoView.measuredWidth + innerImageToTextMargin + contentTextWidth + innerContentToRightViewMargin + max(innerCounterHorizontalMargin * 2 + countMessagesView.measuredWidth, dateView.measuredWidth) + paddingRight

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val rightBorder = width - paddingRight


        val photoLeftBorder = paddingLeft
        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + photoView.measuredHeight
        val photoRightBorder = photoLeftBorder + photoView.measuredWidth

        val dialogNameLeftBorder = photoRightBorder + innerImageToTextMargin
        val dialogNameTopBorder = paddingTop + innerDialogNameToTopMargin
        val dialogNameBottomBorder = dialogNameTopBorder + nameView.measuredHeight
        val dialogNameRightBorder = dialogNameLeftBorder + nameView.measuredWidth

        val contentLeftBorder = dialogNameLeftBorder
        val contentTopBorder = dialogNameBottomBorder + innerDialogNameToContentMargin
        val contentRightBorder = contentLeftBorder + contentTextView.measuredWidth
        val contentBottomBorder = contentTopBorder + contentTextView.measuredHeight

        val dateLeftBorder = rightBorder - dateView.measuredWidth
        val dateTopBorder = paddingTop + innerDateToTopMargin
        val dateRightBorder = rightBorder - paddingRight
        val dateBottomBorder = dateTopBorder + dateView.measuredHeight

        if(isNewMessage) {
            val counterLeftBorder = rightBorder - countMessagesView.measuredWidth - innerCounterHorizontalMargin
            val counterTopBorder = dateBottomBorder + innerDateToCountMargin + innerCounterVerticalMargin
            val counterRightBorder = rightBorder - paddingRight - innerCounterHorizontalMargin
            val counterBottomBorder = counterTopBorder + dateView.measuredHeight

            countMessagesView.layout(counterLeftBorder, counterTopBorder, counterRightBorder, counterBottomBorder)
        }

        photoView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)
        nameView.layout(dialogNameLeftBorder, dialogNameTopBorder, dialogNameRightBorder, dialogNameBottomBorder)
        contentTextView.layout(contentLeftBorder, contentTopBorder, contentRightBorder, contentBottomBorder)
        dateView.layout(dateLeftBorder, dateTopBorder, dateRightBorder, dateBottomBorder)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if(isNewMessage && countMessagesView.text != "0") {
            val leftCountBorder = width.toFloat() - paddingRight - innerCounterHorizontalMargin * 2 - countMessagesView.measuredWidth
            val topCountBorder = paddingTop.toFloat() + innerDateToTopMargin + dateView.measuredHeight + innerDateToCountMargin
            val rightCountBorder = width.toFloat() - paddingRight
            val bottomCountBorder = topCountBorder + 2 * innerCounterVerticalMargin + countMessagesView.measuredHeight.toFloat()
            countMessagesRect = RectF(leftCountBorder, topCountBorder, rightCountBorder, bottomCountBorder)
            countMessagesPaint.color = paintCountColor

            canvas.drawRoundRect(
                    countMessagesRect,
                    countMessagesRadius.toFloat(),
                    countMessagesRadius.toFloat(),
                    countMessagesPaint
            )
        }
        super.dispatchDraw(canvas)
    }


    fun setContentText(content: String){
        contentTextView.text = content
    }

    fun setIsNewMessage(isNew: Boolean){
        this.isNewMessage = isNew
        textViewSettings()

        requestLayout()
        invalidate()
    }

    fun setCountMessages(number: Int){
        countMessagesView.text = number.toString()
    }

    fun setDialogImage(drawable: Drawable){
        photoView.setImageDrawable(drawable)
    }

    fun setDialogImage(bitmap: Bitmap){
        photoView.setImageBitmap(bitmap)
    }

    fun setDialogName(name: String){
        nameView.text = name
    }

    fun setLastDate(date: String){
        dateView.text = date
    }

    private fun textViewSettings(){
        //text appearance
        if(isNewMessage) {
            setTextAppearance(contentTextView, dialogContentNewTextAppearance)
        } else {
            setTextAppearance(contentTextView, dialogContentTextAppearance)
        }
        //dialog name view settings
        nameView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        nameView.gravity = Gravity.LEFT
        nameView.ellipsize = TextUtils.TruncateAt.END
        nameView.isSingleLine = true
        nameView.maxLines = 1

        //dialog content view settings
        contentTextView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        contentTextView.gravity = Gravity.LEFT
        contentTextView.ellipsize = TextUtils.TruncateAt.END
        if (isNewMessage) {
            contentTextView.maxLines = 2
        } else {
            contentTextView.maxLines = 1
        }


        //dialog image settings
        photoView.layoutParams = LayoutParams(imageWidth, imageHeight)
        photoView.scaleType = ImageView.ScaleType.CENTER_CROP

        //date view settings
        dateView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        dateView.gravity = Gravity.CENTER
        dateView.isSingleLine = true
        dateView.maxLines = 1

        //counter view settings
        countMessagesView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        countMessagesView.gravity = Gravity.CENTER_VERTICAL
        countMessagesView.setPadding(0,0,0,0)
        countMessagesView.isSingleLine = true
        countMessagesView.maxLines = 1
        countMessagesView.includeFontPadding = false
    }
}