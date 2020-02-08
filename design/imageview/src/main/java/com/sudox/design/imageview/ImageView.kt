package com.sudox.design.imageview

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.use
import androidx.core.graphics.drawable.toBitmap
import com.sudox.design.common.getAnimator

/**
 * Более производительный и эффективный ImageView
 *
 * Поддерживает установки картинки по умолчанию, а также
 * анимации при их смене.
 */
open class ImageView : View {

    var bitmap: Bitmap? = null
        set(value) {
            val bitmapIsNotEqualed = bitmap != value

            field = if (value != null && (value.height != layoutParams.height || value.width != layoutParams.width)) {
                ThumbnailUtils.extractThumbnail(value, layoutParams.width, layoutParams.height)
            } else {
                value
            }

            if (bitmapIsNotEqualed) {
                invalidate()
            }
        }

    var defaultDrawable: Drawable? = null
        set(value) {
            field = value

            if (bitmap == null) {
                invalidate()
            }
        }

    var popOutAnimator: Animator? = null
    var popInAnimator: Animator? = null

    private var bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.customImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, 0).use {
            defaultDrawable = it.getDrawableOrThrow(R.styleable.ImageView_defaultDrawable)
            popOutAnimator = it.getAnimator(context, R.styleable.ImageView_popOutAnimator)
            popInAnimator = it.getAnimator(context, R.styleable.ImageView_popInAnimator)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        defaultDrawable!!.setBounds(0, 0, widthSize, heightSize)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, 0F, 0F, bitmapPaint)
        } else {
            defaultDrawable!!.draw(canvas)
        }
    }

    /**
     * Устанавливает Drawable как картинку
     *
     * @param drawable Drawable для установки. Если равен null,
     * то будет установлена картинка по умолчанию
     */
    fun setDrawable(drawable: Drawable?) {
        bitmap = drawable?.toBitmap()
    }
}