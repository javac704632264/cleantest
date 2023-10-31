package com.unshareit.cleantest.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomTimeLineView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var timelineX = 0f
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(timelineX, 0f, timelineX, height.toFloat(), linePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                timelineX = event.x.coerceIn(0f, width.toFloat())
                invalidate()
            }
        }
        return true
    }
}