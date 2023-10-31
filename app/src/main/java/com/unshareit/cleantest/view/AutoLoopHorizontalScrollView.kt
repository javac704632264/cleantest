package com.unshareit.cleantest.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import com.unshareit.cleantest.R

class AutoLoopHorizontalScrollView(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {

    private val handle = Handler(Looper.getMainLooper())
    private val autoScrollInterval = 1000L // 自动滚动间隔
    private var scrolling = false

    private var contentWidth = 0
    private var childView: View? = null

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (!scrolling) {
                smoothScrollBy(2, 0) // 控制滚动速度和方向
            }else{
                smoothScrollBy(2, 0)
            }
            handle.postDelayed(this, 10) // 通过多次平滑滚动实现循环滚动
        }
    }

    init {
        postDelayed(autoScrollRunnable, autoScrollInterval)
    }


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        Log.e("AutoLoop","onScrollChanged====>$l  width==>${getChildAt(0).width} screenWidth==>${CommonUtil.getScreenWidthInPx(context)}")
        if (l >= getChildAt(0).width) {
            Log.e("AutoLoop","滚动到末尾====>")
//            scrollTo(0, 0) // 当滚动到末尾时，回到开头
        }else if(l%CommonUtil.getScreenWidthInPx(context)==0){
            Log.e("AutoLoop","增加视图====>")
            addContentView()
        }
    }

    private fun updateContentWidth() {
        contentWidth = getChildAt(0).width
    }

    fun addContentView() {
        val contentLayout = getChildAt(0) as LinearLayout
        val imgView: ImageView? = ImageView(context)
        imgView?.layoutParams = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        imgView?.setBackgroundResource(R.drawable.medium_nm_1)
//        val parent: ViewGroup = childView?.parent as ViewGroup
//        parent?.removeView(childView)
        contentLayout.addView(imgView)
        updateContentWidth()
    }

    fun addContentView(view : View){
        childView = view
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handle.removeCallbacks(autoScrollRunnable)
        postDelayed(autoScrollRunnable, autoScrollInterval)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handle.removeCallbacks(autoScrollRunnable)
    }

    fun setScrolling(scrolling: Boolean) {
        this.scrolling = scrolling
    }
}