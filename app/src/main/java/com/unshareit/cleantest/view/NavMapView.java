package com.unshareit.cleantest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class NavMapView extends View {
    private Bitmap[] mBitmaps;
    private OnClickBitmapListener clickBitmapListener;

    public NavMapView(Context context) {
        super(context);
    }

    public NavMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置图片
     * @author yubin
     * @date 2013-10-12
     */
    public final void setBitmaps(Bitmap[] bitmap) {
        this.mBitmaps = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmaps != null) {
            for (int i = 0; i < mBitmaps.length; i++) {
                canvas.drawBitmap(mBitmaps[i], 0, 0, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            which(event.getX(), event.getY());
        }
        return true;
    }

    public void which(float x, float y) {
        if (mBitmaps != null) {
            for (int i = 0; i < mBitmaps.length; i++) {
                // 判断坐标点不超过图片得宽高
                if ((int) x > mBitmaps[0].getWidth() || (int) y > mBitmaps[0].getHeight()) {
                    clickBitmapListener.ClickBitmap(-1);
                    break;
                }
                Bitmap mBitmap = mBitmaps[i];
                // 判断坐标点是否是在图片得透明区域
                if (mBitmap.getPixel((int) x, (int )y) != 0) {
                    clickBitmapListener.ClickBitmap(i);
                    break;
                }
            }
        }
    }

    public final void setOnClickBitmapListener(OnClickBitmapListener listener) {
        this.clickBitmapListener = listener;
    };

    public interface OnClickBitmapListener {
        /**
         * @param index -1表示超出范围
         * @author yubin
         * @date 2013-10-12
         */
        void ClickBitmap(int index);
    }
}
