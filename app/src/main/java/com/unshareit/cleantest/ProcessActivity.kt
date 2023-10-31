package com.unshareit.cleantest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.unshareit.cleantest.utils.FastBlurUtil
import com.unshareit.cleantest.view.AutoLoopHorizontalScrollView
import com.unshareit.cleantest.view.ClippedViewContainer
import com.unshareit.cleantest.view.DividerView

class ProcessActivity: AppCompatActivity() {
    var btnProcessInsert: Button? = null
    var imgBlur: ImageView? = null
    var imgPic: ImageView? = null
    var autoScrollView: AutoLoopHorizontalScrollView? = null
    var customTimeLineView: ImageView? = null
    var dividerView: DividerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process)
        initView()
        setListener()
    }

    private fun initView(){
        btnProcessInsert = findViewById(R.id.btn_insert_process)
        imgBlur = findViewById(R.id.img_blur)
        var scaledBitmap: Bitmap? = null
        val originBitmap = BitmapFactory.decodeResource(resources,R.drawable.img_blur)

        scaledBitmap = Bitmap.createScaledBitmap(originBitmap,originBitmap.getWidth() / 20,
            originBitmap.getHeight() / 20,
            false)
        val blurBitmap = FastBlurUtil.doBlur(scaledBitmap,8,true)
//        imgBlur?.setBlurFactor(8)
//        imgBlur?.setFullImageByUrl("http://upload-images.jianshu.io/upload_images/1825662-4c4e9bc7148749b7.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/620",
//            "http://upload-images.jianshu.io/upload_images/1825662-4c4e9bc7148749b7.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/620")
        Log.e("ProcessActivity","开始加载图片==>")
        imgPic = findViewById(R.id.img_pic)
        autoScrollView = findViewById(R.id.autoScrollView)
        autoScrollView?.addContentView(imgPic!!)
        autoScrollView?.setScrolling(true)

        customTimeLineView = findViewById(R.id.customTimeLineView)
        val imgBottom = findViewById<ImageView>(R.id.img_bottom)
        dividerView = findViewById(R.id.dividerView)
        val leftBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_woman)
        imgBottom?.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.icon_man))
        customTimeLineView?.setImageBitmap(leftBitmap)

//        changeProgress(50)

        fullImageView = findViewById(R.id.fullImageView)
        partialImageView = findViewById(R.id.partialImageView)
        seekBar = findViewById(R.id.seekBar)

//        val sliceView: SliceView? = findViewById(R.id.sliceView)
//        sliceView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
//            var isShow = true
//            override fun onGlobalLayout() {
//                if (isShow){
//                    isShow = false
//                    sliceView?.invalidate()
//                }
//            }
//        })

        val clippedContainer: ClippedViewContainer? = findViewById(R.id.clippedContainer)
        dividerView?.setImageView(customTimeLineView!!,clippedContainer!!,leftBitmap)

//        // 创建一个切割区域的 Path，这里演示一个矩形切割区域
//        val clipPath = Path()
//        clipPath.addRect(0F, 0F, 1000F, 1000F, Path.Direction.CW)
//        // 设置切割区域
//        clippedContainer!!.setClipPath(clipPath)

        // 设置滑块监听器
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePartialImage(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updatePartialImage(progress: Int) {
        val containerWidth = fullImageView.width
        val imageWidth = fullImageView.drawable.intrinsicWidth
        var offset = (imageWidth - containerWidth) * progress / 100

        val bitmap = (fullImageView.drawable as BitmapDrawable).bitmap
        if (offset >= bitmap.width){
            offset = bitmap.width
        }else if (offset <= 0){
            offset = 0
        }
        val partialBitmap = Bitmap.createBitmap(bitmap, offset, 0, bitmap.width, bitmap.height)

        partialImageView.setImageBitmap(partialBitmap)
    }

    private lateinit var fullImageView: ImageView
    private lateinit var partialImageView: ImageView
    private lateinit var seekBar: SeekBar



    fun changeProgress( progress: Int) {
        if (customTimeLineView != null) {
            val drawable: GradientDrawable = getResources().getDrawable(R.drawable.icon_man) as GradientDrawable;
            val params = customTimeLineView?.getLayoutParams();
            drawable.setSize(params?.width!! * progress / 100, params?.height!!);
            customTimeLineView?.setImageDrawable(drawable);
        }
    }
var lastX = 0
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val x = event?.x
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                lastX = event?.x.toInt()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val offsetX = (x!! - lastX).toInt()
//                val params: LinearLayout.LayoutParams = customTimeLineView?.layoutParams as LinearLayout.LayoutParams
//                params.leftMargin = customTimeLineView?.left!! + offsetX
//                customTimeLineView?.layoutParams = params
////                customTimeLineView?.scrollBy(offsetX,0)
//            }
//        }
//        return super.onTouchEvent(event)
//    }

    private fun setListener(){
        btnProcessInsert?.setOnClickListener {
//            val json = AppUtils.getAssetContent(this,"top_app_final.json")
//            val jsonArray = JSONArray(json)
//            for (i in 0 until  jsonArray?.length()){
//                val obj = jsonArray.optJSONObject(i)
//                val pkgId = obj.getInt("pkgid")
//                val pkgname = obj.getString("pkgname")
//                val appData = AppData()
//                appData.appId = pkgId
//                appData.pkgname = pkgname
//                AppDataBaseManager.getInstance().insertAppData(applicationContext,appData)
//                Log.e("MainActivity","pkgId==>$pkgId  pkgname==>$pkgname")
//            }
//            Glide.with(this).load("http://rs.mymuslimdaily.com/8vcC/tmWc/230605/xinwen60_IC6y.png").
//            listener(object : RequestListener<Drawable>{
//                override fun onLoadFailed(
//                    p0: GlideException?,
//                    p1: Any?,
//                    p2: Target<Drawable>?,
//                    p3: Boolean
//                ): Boolean {
//                    Log.e("ProcessActivity","onLoadFailed===>",p0)
////                    p0?.printStackTrace()
//                    return false
//                }
//
//                override fun onResourceReady(
//                    p0: Drawable?,
//                    p1: Any?,
//                    p2: Target<Drawable>?,
//                    p3: DataSource?,
//                    p4: Boolean
//                ): Boolean {
//                    Log.e("ProcessActivity","onResourceReady===>")
//                    return false
//                }
//
//
//            }).
//            into(imgBlur!!)


            Glide.with(this).asBitmap().
            load("http://upload-images.jianshu.io/upload_images/1825662-4c4e9bc7148749b7.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/62")
                .placeholder(R.drawable.img_blur).error(R.drawable.img_blur).centerCrop().into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Log.e("ProcessActivity","onResourceReady==>"+resource)
                        var scaledBitmap = Bitmap.createScaledBitmap(resource,resource.getWidth() / 20,
                            resource.getHeight() / 20,
                            false)
                        val blurBitmap = FastBlurUtil.doBlur(scaledBitmap,8,true)
                        imgBlur?.setImageBitmap(blurBitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.e("ProcessActivity","onLoadCleared==>"+placeholder)
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Log.e("ProcessActivity","onLoadStarted==>"+placeholder)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.e("ProcessActivity","onLoadFailed==>"+errorDrawable)
                    }
                })
        }
    }
}