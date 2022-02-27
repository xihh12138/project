package com.example.myapplication.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.children
import java.lang.ref.WeakReference

@RequiresApi(Build.VERSION_CODES.M)
class BagDragListenerLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    companion object {
        val TAG = BagDragListenerLayout::class.java.canonicalName
    }

    private var childBounds = mutableListOf<Rect>()

    private var logView: TextView? = null
    private var bag1View: ViewGroup? = null
    private var bag2View: ViewGroup? = null

    private var draggedView: View? = null

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

    override fun onFinishInflate() {
        super.onFinishInflate()
        bag1View = children.find { it is ViewGroup } as ViewGroup
        bag2View = children.find { it !== bag1View && it is ViewGroup } as ViewGroup
        logView = children.find { it is TextView } as TextView

        bag1View?.children?.forEach {
            it.setOnLongClickListener {
                draggedView = it
                ViewCompat.startDragAndDrop(it, null, DragShadowBuilder(it), it, 0)
                false
            }
            it.setOnDragListener(bagItemDragListener)
        }
        bag2View?.children?.forEach {
            it.setOnLongClickListener {
                draggedView = it
                ViewCompat.startDragAndDrop(it, null, DragShadowBuilder(it), it, 0)
                false
            }
            it.setOnDragListener(bagItemDragListener)
        }
//        bag1View.setOnDragListener(BagDragListener())
//        bag2View.setOnDragListener(BagDragListener())
    }

    private val bagItemDragListener = OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d(TAG, "ACTION_DRAG_STARTED:${v.contentDescription}")
//                    if (event.localState === v) {
//                        v.visibility = View.INVISIBLE
//                    }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d(TAG, "ACTION_DRAG_ENTERED:${v.contentDescription}")
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d(TAG, "ACTION_DRAG_EXITED:${v.contentDescription}")
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d(TAG, "ACTION_DRAG_ENDED:${v.contentDescription}")
//                    if (event.localState === v) {
//                        v.visibility = View.VISIBLE
//                    }
            }
            DragEvent.ACTION_DROP -> {
                Log.d(TAG, "ACTION_DROP:${v.contentDescription}")
                if (event.localState !== v) {
                    (event.localState as View).let {
                        //交换颜色
                        val oldForeGround = it.foreground
                        it.foreground = v.foreground
                        v.foreground = oldForeGround

                        //松手后的动画
                        val originalX = v.translationX
                        val originalY = v.translationY
                        v.translationX = event.x - v.width / 2
                        v.translationY = event.y - v.height / 2
                        v.animate()
                            .translationX(originalX)
                            .translationY(originalY)
                            .setDuration(1500)
                            .start()
                    }
                }
                onDropInvoke(WeakReference(v))
            }
        }
        true
    }

    private fun onDropInvoke(v: WeakReference<View>) {
        var fromBagText = ""
        var toBagText = ""
        val itemText = (draggedView?.contentDescription) ?: ""
        if (draggedView?.parent === bag1View) {
            fromBagText = "bag1"
        } else if (draggedView?.parent === bag2View) {
            fromBagText = "bag2"
        }
        if (v.get()?.parent === bag1View) {
            toBagText = "bag1"
        } else if (v.get()?.parent === bag2View) {
            toBagText = "bag2"
        }

        logView?.append("$fromBagText -----$itemText-----> $toBagText \n")


        draggedView = null
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

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

    private class BagDragListener : OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(TAG, "ACTION_DRAG_STARTED:${v.contentDescription}")
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d(TAG, "ACTION_DRAG_ENTERED:${v.contentDescription}")
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.d(TAG, "ACTION_DRAG_EXITED:${v.contentDescription}")
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d(TAG, "ACTION_DRAG_ENDED:${v.contentDescription}")
                }
                DragEvent.ACTION_DROP -> {
                    Log.d(TAG, "ACTION_DROP:${v.contentDescription}")
                }
            }
            return true
        }
    }
}