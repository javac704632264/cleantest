package com.unshareit.cleantest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.unshareit.cleantest.R;

public class SliceView extends FrameLayout {
    private Paint paint;

    public SliceView(Context context) {
        super(context);
        init();
    }

    public SliceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.teal_200)); // 设置切割的颜色
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 在这里指定子视图的布局
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 根据需求设置子视图的位置和大小
            child.layout(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 在这里绘制切割效果，可以使用Canvas的不同方法来实现所需的切割形状
        // 例如，使用drawRect绘制矩形切割区域
//        canvas.drawRect(0, 0, 200, 200, paint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.clipRect(0, 0, getWidth(), getHeight());
    }


}
