package com.unshareit.cleantest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.unshareit.cleantest.clean.db.room.AppDataBaseManager
import com.unshareit.cleantest.clean.db.room.bean.AdData
import com.unshareit.cleantest.clean.db.room.bean.AppData
import com.unshareit.cleantest.clean.utils.ContextUtils
import com.unshareit.cleantest.clean.utils.StorageManager
import com.unshareit.cleantest.utils.AppUtils
import com.unshareit.cleantest.utils.BlurImageView
import com.unshareit.cleantest.utils.FastBlurUtil
import com.unshareit.cleantest.view.CircleProgressView
import com.unshareit.cleantest.view.ColourImageLayerView
import com.unshareit.cleantest.view.MyGLSurfaceView
import org.json.JSONArray
import java.io.File

class MainActivity : AppCompatActivity() {
    var btn1: Button? = null
    var btnAd: Button? = null
    var imgPic: ColourImageLayerView? = null
    var btnPic: Button? = null
    var btnPic2:Button? = null
    var circleProgress: CircleProgressView? = null
    var imgBlur: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setListener()
    }

    fun initView(){
        btn1 = findViewById(R.id.btn1)
        btnAd = findViewById(R.id.btn_ad)
        imgPic = findViewById(R.id.img_pic)
        btnPic = findViewById(R.id.btn_pic)
        btnPic2 = findViewById(R.id.btn_pic_2)
        val mEffectView = findViewById<MyGLSurfaceView>(R.id.effectsview)
        ContextUtils.init(applicationContext)

        imgPic?.setLayer(resources.getDrawable(R.drawable.img_blur))
        Log.e("MainActivity","path==>${Environment.getExternalStorageDirectory().absolutePath + File.separator}")


        imgBlur = findViewById(R.id.img_blur)
        var scaledBitmap: Bitmap? = null
        val originBitmap = BitmapFactory.decodeResource(resources,R.drawable.medium_nm_1)
        scaledBitmap = Bitmap.createScaledBitmap(originBitmap,originBitmap.getWidth() / 100,
            originBitmap.getHeight() / 100,
            false)
        val blurBitmap = FastBlurUtil.doBlur(scaledBitmap,8,true)
        imgBlur?.setImageBitmap(blurBitmap)

//        circleProgress = findViewById(R.id.progressView)
//        circleProgress?.setProgressType(CircleProgressView.TYPE_CLIP)
//        circleProgress?.setBackgroundCircleColor(Color.parseColor("#f1f1f1"));
//        circleProgress?.setProgressWidth(CommonUtil.dp2px(this, 10));
//        circleProgress?.setProgressColor(Color.parseColor("#d81b60"));
//        circleProgress?.setStartAngle(90);
//        circleProgress?.setProgress(60.0F, true);

//        val layers = arrayOfNulls<Drawable>(2)
//        layers[0] = resources.getDrawable(R.drawable.img_2)
//        layers[1] = getResources().getDrawable(R.drawable.img_3);
//        var mDrawables: LayerDrawable? = LayerDrawable(layers)
//        imgPic?.addLayer(layers[0],0)
//        imgPic?.addLayer(layers[1],1)


    }

    fun setListener(){
        btn1?.setOnClickListener {
            val json = AppUtils.getAssetContent(this,"top_app_final.json")
            val jsonArray = JSONArray(json)
            for (i in 0 until  jsonArray?.length()){
                val obj = jsonArray.optJSONObject(i)
                val pkgId = obj.getInt("pkgid")
                val pkgname = obj.getString("pkgname")
                val appData = AppData()
                appData.appId = pkgId
                appData.pkgname = pkgname
                AppDataBaseManager.getInstance().insertAppData(applicationContext,appData)
                Log.e("MainActivity","pkgId==>$pkgId  pkgname==>$pkgname")
            }
        }

        btnAd?.setOnClickListener {
            val json = AppUtils.getAssetContent(this,"ad_rule.json")
            val jsonArray = JSONArray(json)
            for (i in 0 until  jsonArray?.length()){
                val obj = jsonArray.optJSONObject(i)
                val adPath = obj.getString("ad_path")
                val adName = obj.getString("ad_name")
                val adDesc = obj.getString("desc")
                val adData = AdData()
                adData.id = (i+1)
                adData.adName = adName
                adData.adPath = StorageManager.getRootPath()+adPath
                adData.desc = adDesc
                AppDataBaseManager.getInstance().insertAdData(applicationContext,adData)
                Log.e("MainActivity","adPath==>${StorageManager.getRootPath()+adPath}  adName==>$adName")
            }
        }

        btnPic?.setOnClickListener {
            var mDrawables: LayerDrawable? = null
            val layers = arrayOfNulls<Drawable>(2)
            layers[0] = resources.getDrawable(R.drawable.test4)
            layers[1] = getResources().getDrawable(R.drawable.img_4)
            imgPic?.addLayer(layers[1],1)
//            imgPic?.setImageDrawable(mDrawables)
        }

        btnPic2?.setOnClickListener {
            var mDrawables: LayerDrawable? = null
            val layers = arrayOfNulls<Drawable>(2)
            layers[0] = resources.getDrawable(R.drawable.test4)
            layers[1] = getResources().getDrawable(R.drawable.img_5);
            imgPic?.addLayer(layers[1],1)
        }

    }


}