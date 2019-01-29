package com.sudox.design.layouts

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.motion.MotionLayout
import android.support.v4.view.NestedScrollingParent2
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

class TouchConstraintLayout : ConstraintLayout, NestedScrollingParent2 {

    // TouchConstraintLayout должен быть связан с MotionLayout'ом внутри себя
    var motionLayout: MotionLayout? = null
    var maxScrollY: Int = 0
    var totalScrollY: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onStartNestedScroll(child: View, target: View, axis: Int, type: Int): Boolean {
        return axis == ViewCompat.SCROLL_AXIS_VERTICAL && motionLayout != null
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {}
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        val dy = Math.abs(dyUnconsumed)

        if (dyUnconsumed < 0) {
            // Scroll to down
            totalScrollY = Math.min(totalScrollY + dy, maxScrollY)

            // Update animation progress
            updateProgress()

            // If scroll ended
            if (totalScrollY == maxScrollY) {
                stopNestedScroll()
            }
        } else if (dyUnconsumed > 0) {
            // Scroll to up
            totalScrollY = Math.max(totalScrollY - dy, 0)

            // Update animation progress
            updateProgress()

            // If scroll ended
            if (totalScrollY == 0) {
                stopNestedScroll()
            }
        }
    }

    private fun updateProgress() {
        // Calculate the animation progress
        val progress = totalScrollY.toFloat() / maxScrollY.toFloat()

        // Update the animation progress
        if (motionLayout!!.progress != progress) {
            motionLayout!!.progress = progress
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {}
    override fun onNestedScrollAccepted(child: View, target: View, axis: Int, type: Int) {}
}