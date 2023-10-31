package com.unshareit.cleantest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ClippedViewContainer extends FrameLayout {
    private Path clipPath;

    public ClippedViewContainer(Context context) {
        super(context);
        init();
    }

    public ClippedViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        clipPath = new Path();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 保存当前画布状态
        canvas.save();

        // 根据 clipPath 剪切画布，只显示剪切区域内的内容
        canvas.clipPath(clipPath);

        // 绘制子视图
        super.dispatchDraw(canvas);

        // 恢复画布状态
        canvas.restore();
    }

    // 设置切割区域的形状，这里使用 Path 作为切割区域
    public void setClipPath(Path path) {
        clipPath = path;
        invalidate(); // 通知视图重新绘制
    }
}
