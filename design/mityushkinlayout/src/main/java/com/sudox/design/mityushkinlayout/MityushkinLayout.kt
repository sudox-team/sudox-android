package com.sudox.design.mityushkinlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import com.sudox.design.mityushkinlayout.yankintemplates.YankinTemplatesAdapter

class MityushkinLayout : ViewGroup {

    var adapter: MityushkinLayoutAdapter? = null
        set(value) {
            field = value?.apply {
                onAttached(this@MityushkinLayout)
            }

            requestLayout()
            invalidate()
        }

    private var rectangles: Array<Rect>? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        adapter = YankinTemplatesAdapter()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (adapter != null && childCount > 0) {
            val unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

            for (i in 0 until childCount) {
                measureChild(getChildAt(i), unspecifiedSpec, unspecifiedSpec)
            }

            rectangles = adapter?.getTemplate(childCount)?.layout(widthMeasureSpec, heightMeasureSpec, adapter!!, this)

            if (rectangles != null) {
                val height = paddingTop + paddingBottom + rectangles!!.last().bottom
                val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
                    MeasureSpec.getSize(widthMeasureSpec)
                } else {
                    paddingLeft + paddingRight + rectangles!!.last().right
                }

                setMeasuredDimension(width, height)
                return
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (rectangles != null) {
            val leftBorder = ((measuredWidth - paddingLeft) / 2 - rectangles!!.last().right / 2) + paddingLeft

            rectangles!!.forEachIndexed { index, rect ->
                getChildAt(index).layout(
                        leftBorder + rect.left,
                        paddingTop + rect.top,
                        leftBorder + rect.right,
                        paddingTop + rect.bottom
                )
            }
        }
    }
}