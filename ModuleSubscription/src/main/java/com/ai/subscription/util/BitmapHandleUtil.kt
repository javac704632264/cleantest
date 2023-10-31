package com.ai.subscription.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

fun View.getViewBitmap(): Bitmap? {
    val targetView = this
    val width = targetView.measuredWidth
    if (width == 0) {
        return null
    }
    val height = targetView.measuredHeight
    if (height == 0) {
        return null
    }
    //for oom
    return kotlin.runCatching {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        targetView.draw(canvas)
        bmp
    }.getOrNull()
}