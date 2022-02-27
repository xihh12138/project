package com.example.myapplication.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children

class TagLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val childrenSizeList = mutableListOf<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val myWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val myWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val myHeightSize = MeasureSpec.getSize(heightMeasureSpec)
        val myHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthUsed = 0
        var heightUsed = 0

        val iterator = children.iterator()
        while (iterator.hasNext()) {
            val child = iterator.next()
            val layoutParams = child.layoutParams

            var widthMeasureSpec = 0
            var heightMeasureSpec = 0

            widthMeasureSpec = when (myWidthMode) {
                MeasureSpec.AT_MOST -> when (layoutParams.width) {
                    LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(
                        myWidthSize - widthUsed, MeasureSpec.AT_MOST
                    )
                    LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(
                        myWidthSize - widthUsed, MeasureSpec.AT_MOST
                    )
                    else -> MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY)
                }
                MeasureSpec.EXACTLY -> when (layoutParams.width) {
                    LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(
                        myWidthSize - widthUsed, MeasureSpec.AT_MOST
                    )
                    LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(
                        myWidthSize - widthUsed, MeasureSpec.AT_MOST
                    )
                    else -> MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY)
                }
                else -> MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            }

            heightMeasureSpec = when (myHeightMode) {
                MeasureSpec.AT_MOST -> when (layoutParams.height) {
                    LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(
                        myHeightSize - heightUsed, MeasureSpec.AT_MOST
                    )
                    LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(
                        myHeightSize - heightUsed, MeasureSpec.AT_MOST
                    )
                    else -> MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY)
                }
                MeasureSpec.EXACTLY -> when (layoutParams.height) {
                    LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(
                        myHeightSize - heightUsed, MeasureSpec.AT_MOST
                    )
                    LayoutParams.WRAP_CONTENT -> MeasureSpec.makeMeasureSpec(
                        myHeightSize - heightUsed, MeasureSpec.AT_MOST
                    )
                    else -> MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY)
                }
                else -> MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            }
            child.measure(widthMeasureSpec,heightMeasureSpec)
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }
}