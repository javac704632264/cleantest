package com.ai.subscription.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ai.subscription.R;

public class DotsView extends View {
    private Paint mPaint;
    private Paint mSelectPaint;
    private int mRadios;
    private int mBtw;
    private int mSelect = 0;
    private int mCount = 7;

    public DotsView(Context context) {
        super(context);
        init();
    }

    public DotsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setSelect(int mSelect) {
        this.mSelect = mSelect;
        postInvalidate();
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        requestLayout();
    }

    public void setRadios(int mRadios) {
        this.mRadios = mRadios;
        requestLayout();
    }

    public void setBtw(int mBtw) {
        this.mBtw = mBtw;
        requestLayout();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#99E1A055"));
        mSelectPaint = new Paint();
        mSelectPaint.setColor(Color.parseColor("#E8A254"));
        mRadios = getResources().getDimensionPixelSize(R.dimen.common_dimens_2_5dp);
        mBtw = getResources().getDimensionPixelSize(R.dimen.common_dimens_6dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCount < 0)
            return;
        for (int i = 0; i < mCount; i++) {
            if (i == mSelect) {
                canvas.drawCircle(mRadios * (i * 2 + 1) + i * mBtw, mRadios, mRadios, mSelectPaint);
            } else {
                canvas.drawCircle(mRadios * (i * 2 + 1) + i * mBtw, mRadios, mRadios, mPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int targetHeight = 2 * mRadios;
        int targetWidth = mRadios * (mCount * 2) + (mCount - 1) * mBtw;
        super.onMeasure(MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(targetHeight, MeasureSpec.EXACTLY));
    }
}
