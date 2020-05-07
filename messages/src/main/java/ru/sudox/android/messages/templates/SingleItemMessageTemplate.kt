package ru.sudox.android.messages.templates

import android.graphics.Rect
import android.view.View
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.design.mityushkinlayout.MityushkinLayoutAdapter
import ru.sudox.design.mityushkinlayout.MityushkinLayoutTemplate
import ru.sudox.design.common.views.RoundedView
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object SingleItemMessageTemplate : MityushkinLayoutTemplate {

    override var dependsFromChildSize = true

    override fun layout(widthMeasureSpec: Int, heightMeasureSpec: Int, adapter: MityushkinLayoutAdapter, layout: MityushkinLayout): Array<Rect> {
        (adapter as MessageTemplatesAdapter).let {
            val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
            val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
            val child = layout.getChildAt(0)

            val width = min(if (widthMode != View.MeasureSpec.EXACTLY) {
                child.measuredWidth
            } else {
                widthSize
            }, it.maxSingleViewWidth)

            it.setCorners(layout)

            val aspect = child.measuredHeight.toFloat() / child.measuredWidth.toFloat()
            val height = min(max((width * aspect).roundToInt(), it.minSingleViewHeight), it.maxSingleViewHeight)

            return arrayOf(Rect(0, 0, width, height))
        }
    }
}