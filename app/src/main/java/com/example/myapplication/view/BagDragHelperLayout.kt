package com.example.myapplication.view

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.customview.widget.ViewDragHelper

@RequiresApi(Build.VERSION_CODES.M)
class BagDragHelperLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    companion object {
        val TAG = BagDragHelperLayout::class.java.canonicalName
    }

    private var childBounds = mutableListOf<Rect>()

    private var logView: TextView? = null
    private var bag1View: ViewGroup? = null
    private var bag2View: ViewGroup? = null

    private var captureLeft = 0
    private var captureTop = 0

    private var dragHelper = ViewDragHelper.create(this, BagDragCallback())

    private inner class BagDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            Log.d(TAG, "tryCaptureView:${child.contentDescription}")
            return false
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int) = left

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int) = top

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            capturedChild.elevation = elevation + 1
            captureLeft = capturedChild.left
            captureTop = capturedChild.top
        }

        override fun onViewPositionChanged(
            changedView: View, left: Int, top: Int, dx: Int, dy: Int
        ) {

        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            dragHelper.settleCapturedViewAt(captureLeft, captureTop)
            ViewCompat.postInvalidateOnAnimation(this@BagDragHelperLayout)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        dragHelper.processTouchEvent(ev)
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        childBounds.clear()
        var maxWidth = 0

        children.forEachIndexed { index, view ->
            val lp = view.layoutParams as MarginLayoutParams
            val heightUsed = getHeightUsed()

            val childWidthMeasureSpec = getChildMeasureSpec(
                widthMeasureSpec,
                paddingStart + paddingEnd + lp.leftMargin + lp.rightMargin,
                lp.width
            )
            val childHeightMeasureSpec = getChildMeasureSpec(
                heightMeasureSpec,
                (paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed),
                lp.height
            )
            view.measure(childWidthMeasureSpec, childHeightMeasureSpec)

            maxWidth = Math.max(maxWidth, view.measuredWidth)

            putChildBound(
                index,
                Rect(
                    paddingStart + lp.leftMargin,
                    heightUsed + lp.topMargin,
                    paddingStart + lp.leftMargin + view.measuredWidth,
                    view.measuredHeight + heightUsed + lp.topMargin
                )
            )
        }

        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec), resolveSize(getHeightUsed(), heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.forEachIndexed { index, view ->
            val bound = childBounds[index]
            view.layout(bound.left, bound.top, bound.right, bound.bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        bag1View = children.find { it is ViewGroup } as ViewGroup
        bag2View = children.find { it !== bag1View && it is ViewGroup } as ViewGroup
        logView = children.find { it is TextView } as TextView
    }

    private fun putChildBound(index: Int, bound: Rect) {
        if (childBounds.size <= index) {
            childBounds.add(index, bound)
        } else {
            childBounds[index] = bound
        }
    }

    private fun getHeightUsed() = if (childBounds.isEmpty()) {
        paddingTop
    } else {
        childBounds.last().bottom
    }
}