package com.unshareit.cleantest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap originalBitmap;
    private Matrix matrix = new Matrix();
    private float startX;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        imageView = findViewById(R.id.imageView);

        // 加载原始图片
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_man);

        // 显示图片的初始部分
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean isShow = true;
            @Override
            public void onGlobalLayout() {
                if (isShow){
                    isShow = false;
                    updateImage(1000);
                }
            }
        });


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - startX;
                        // 根据滑动距离更新显示的区域
                        updateImage(deltaX);
                        startX = event.getX();
                        break;
                }
                return true;
            }
        });
    }

    private void updateImage(float deltaX) {
        // 计算滑动的偏移量并进行裁剪和平移
        matrix.reset();
        float translationX = -deltaX * (originalBitmap.getWidth() / imageView.getWidth());
        matrix.postTranslate(translationX, 0);
        imageView.setImageMatrix(matrix);

        // 裁剪部分区域的Bitmap
        int startX = (int) (deltaX * (originalBitmap.getWidth() / imageView.getWidth()));
        int startY = 0;
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Log.e("ImageViewActivity","updateImage======>"+width+" imgWidth===>"+imageView.getWidth()+" startX===>"+startX);

        Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, startX, startY, width-400, height);
        imageView.setImageBitmap(croppedBitmap);
    }
}
