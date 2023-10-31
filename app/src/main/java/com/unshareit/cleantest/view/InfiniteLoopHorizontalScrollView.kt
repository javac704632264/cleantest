package com.unshareit.cleantest.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class InfiniteLoopHorizontalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val handle = Handler()
    private val scrollSpeed = 2
    private val scrollDelay = 10L
    private var isScrolling = false
    private var contentWidth = 0

    init {
        post {
            startScrolling()
        }
    }

    private fun startScrolling() {
        if (!isScrolling) {
            isScrolling = true
            handle.post(scrollRunnable)
        }
    }

    private fun stopScrolling() {
        if (isScrolling) {
            isScrolling = false
            handle.removeCallbacks(scrollRunnable)
        }
    }

    private val scrollRunnable = object : Runnable {
        override fun run() {
            scrollBy(scrollSpeed, 0)
            if (scrollX >= contentWidth) {
                scrollTo(scrollX - contentWidth, 0)
            }
            handle.postDelayed(this, scrollDelay)
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> stopScrolling()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startScrolling()
        }
        return super.onTouchEvent(ev)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        contentWidth = getChildAt(0).width
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startScrolling()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopScrolling()
    }
}