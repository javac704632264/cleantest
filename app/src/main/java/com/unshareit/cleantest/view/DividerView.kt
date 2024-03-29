package com.unshareit.cleantest.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.unshareit.cleantest.R


class DividerView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var dividerX = 0f
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 50f
    }

    private lateinit var imageView: ImageView
    private lateinit var imgBottom: ClippedViewContainer

    var leftBitmap: Bitmap? = null
    var rightBitmap: Bitmap? = null
    var originBitmap: Bitmap? = null

    private val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_draw)


    fun setImageView(imageView: ImageView,imgBottom: ClippedViewContainer,bitmap: Bitmap?) {
        this.imageView = imageView
        this.imgBottom = imgBottom
        leftBitmap = bitmap

//        rightBitmap = imgBottom.drawable.toBitmap()
        dividerX = (CommonUtil.getScreenWidthInPx(context).toFloat() - defaultBitmap.width).toFloat()/2
        Log.e(
            "ImageViewActivity",
            "dividerX======>" + dividerX
        )
        imageView?.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            var isShow = true
            override fun onGlobalLayout() {
                if (isShow){
                    isShow = false
//                    originBitmap = leftBitmap//Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)
//                    val canvas = Canvas(originBitmap!!)
//                    canvas.drawBitmap(leftBitmap!!, 0f, 0f, null)
//                    imageView.setImageBitmap(originBitmap)
                    updateImage(dividerX)
                    invalidate()
                }
            }
        })

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawLine(dividerX, 0f, dividerX, height.toFloat(), dividerPaint)
//        val dst = Rect() // 屏幕 >>目标矩形
//        dst.left = dividerX.toInt();
//        dst.top = 0;
//        dst.right = (defaultBitmap.width+dividerX).toInt();
//        dst.bottom = 0 + height;
        canvas.drawBitmap(defaultBitmap,dividerX,0f,null)
//        canvas.drawBitmap(defaultBitmap, null, dst, null);
//        updateImageDisplay()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                dividerX = event.x.coerceIn(0f, width.toFloat())
                updateImage(dividerX)
                invalidate()
//                updateImageDisplay()
            }
        }
        return true
    }

    private fun updateImage(deltaX: Float) {
        // 计算滑动的偏移量并进行裁剪和平移
//        matrix.reset()
//        val translationX: Float = -deltaX * (originBitmap!!.getWidth() / imageView.width)
//        val imgMartrix = Matrix()
//        val scaleX: Float = (imageView.width / originBitmap!!.getWidth()).toFloat()
//        val scaleY: Float = (imageView.height / originBitmap!!.getHeight()).toFloat()
//        val scale: Float = Math.min(scaleX,scaleY)
//        imgMartrix.setScale(1.5f,1.5f)
//        matrix.postTranslate(translationX, 0f)
//        imageView.imageMatrix = imgMartrix

        // 裁剪部分区域的Bitmap
//        val startX: Int = (deltaX * (originBitmap!!.getWidth() / imageView.width)).toInt()
//        val startY = 0
        val width: Int = imageView?.width
//        val height: Int = originBitmap!!.getHeight()

        var offsetWidth = deltaX
        if (offsetWidth >= width){
            offsetWidth = width.toFloat()
        }
        Log.e(
            "ImageViewActivity",
            "updateImage======>" + width + " imgWidth===>" + imageView?.width + " startX===>" + deltaX+" offsetWidth==>"+offsetWidth+" imgHeight==>"+imageView?.height
        )
//        val croppedBitmap = Bitmap.createBitmap(originBitmap!!, 0, 0, imageView?.width, imageView?.height)
//        imageView.setImageBitmap(croppedBitmap)

        val clipPath = Path()
        clipPath.addRect(0F, 0F, offsetWidth+defaultBitmap.width/2, imageView?.height.toFloat(), Path.Direction.CW)
        // 设置切割区域
        imgBottom!!.setClipPath(clipPath)
    }

    private fun updateImageDisplay() {
        val imageWidth = this.leftBitmap?.width
        var dividerPosition = dividerX.toInt()
        if (dividerPosition >= imageWidth!!){
            dividerPosition = imageWidth-5
        }else if (dividerPosition <= 0){
            dividerPosition = 5
        }

        val leftBitmap = Bitmap.createBitmap(imageView.drawable.toBitmap(), 0, 0, dividerPosition, this.leftBitmap?.height!!)
//        val rightBitmap = Bitmap.createBitmap(imgBottom.drawable.toBitmap(), dividerPosition, 0, imageWidth - dividerPosition, this.rightBitmap?.height!!)

        val combinedBitmap = Bitmap.createBitmap(imageWidth, leftBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
//        canvas.drawRect(0.0f,0.0f,dividerPosition*1.0f,0.0f, Paint())
        canvas.drawBitmap(leftBitmap, 0f, 0f, null)
//        canvas.drawBitmap(rightBitmap, dividerPosition.toFloat(), 0f, null)

//        val combinedRightBitmap = Bitmap.createBitmap(imageWidth, leftBitmap.height, Bitmap.Config.ARGB_8888)
//        val canvasright = Canvas(combinedRightBitmap)
//        canvasright.drawRect(0.0f,0.0f,imageWidth - dividerPosition*1.0f,0.0f,Paint())
//        canvasright.drawBitmap(rightBitmap, dividerPosition.toFloat(), 0f, null)


        imageView.setImageBitmap(combinedBitmap)
//        imgBottom.setImageBitmap(combinedRightBitmap)
    }

    fun fromDrawable(drawable: Drawable): Bitmap? {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        if (bitmap != null) {
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
            return bitmap
        }
        return null
    }

}