package com.sudox.design.drawables.ripple

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import com.sudox.common.annotations.Checked

private const val RIPPLE_MASK_COLOR = Color.WHITE

class RippleMaskDrawable(@RippleMaskType var type: Int) : Drawable() {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var specifiedRadius: Float = 0F

    init {
        paint.color = RIPPLE_MASK_COLOR
    }

    override fun draw(canvas: Canvas) {
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()
        val radius = getRadius()

        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    internal fun getRadius(): Float {
        return if (type == RippleMaskType.BORDERED) {
            (Math.max(bounds.width(), bounds.height()) / 2).toFloat()
        } else if (type == RippleMaskType.BORDERLESS) {
            val area = (bounds.left - bounds.centerX()) *
                    (bounds.left - bounds.centerX()) + (bounds.top - bounds.centerY()) *
                    (bounds.top - bounds.centerY()).toDouble()

            Math.ceil(Math.sqrt(area)).toFloat()
        } else {
            specifiedRadius
        }
    }

    @Checked
    fun setRadius(radius: Float) {
        specifiedRadius = radius
        type = RippleMaskType.WITH_SPECIFIED_RADIUS
    }

    @Checked
    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter) {}
}