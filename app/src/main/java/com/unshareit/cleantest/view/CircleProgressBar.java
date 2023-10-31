package com.unshareit.cleantest.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;
import com.unshareit.cleantest.R;


@SuppressLint("ModuleApiLint")
public class CircleProgressBar extends View {
    /**
     * 圆环默认宽度
     */
    private float defaultStrokeWidth = 10;
    /**
     * 默认进度宽度
     */
    private float progressWidth = 5;
    /**
     * 进度值0~100
     */
    private int mProgress;
    /**
     * 起始角度0~360
     * -90为正上方
     * 0为3点位置
     */
    private int fromAngle = 0;//
    private int mRadius;//圆弧半径 不包括圆环厚度
    private boolean isCapRound = true;//进度是开头和结尾否是圆形的
    private Paint mPaint;
    private Paint mBgPaint;
    private Paint mSolidCirclePaint;
//    private Paint mBitmapPaint;
    RectF ovl;//圆环内环绘制的区域
    private boolean isProgressing = false;//是否正在绘制进度 防止线程多重绘制
//    Bitmap mBitmap;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);


        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setDither(true);
        mBgPaint.setStyle(Paint.Style.STROKE);

        mSolidCirclePaint = new Paint();
        mSolidCirclePaint.setAntiAlias(true);
        mSolidCirclePaint.setDither(true);
        mSolidCirclePaint.setStyle(Paint.Style.FILL);
        mSolidCirclePaint.setColor(Color.parseColor("#FFBB86FC"));

//        mBitmapPaint = new Paint();
//        mBitmapPaint.setAntiAlias(true);
//        mBitmapPaint.setDither(true);
//        mBitmapPaint.setStyle(Paint.Style.FILL);

        int strokeColor = Color.parseColor("#FFF247");
        int strokeBgColor = Color.parseColor("#006548");
        if (attrs != null){
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.circleProgressView);
            strokeBgColor = array.getColor(R.styleable.circleProgressView_strokeBgColor, Color.parseColor("#006548"));
            strokeColor = array.getColor(R.styleable.circleProgressView_progressColor, Color.parseColor("#FFF247"));
            defaultStrokeWidth = (int) array.getDimension(R.styleable.circleProgressView_strokeWidth, 10);
            progressWidth = (int) array.getDimension(R.styleable.circleProgressView_progressWidth, 5);
            mRadius = (int) array.getDimension(R.styleable.circleProgressView_circleRadius, 10);
//            mRadius = (int) (DeviceHelper.getScreenWidth(getContext()) * 0.25F);
            mProgress = array.getInteger(R.styleable.circleProgressView_progresses, 0);
            fromAngle = array.getInteger(R.styleable.circleProgressView_fromAngle, 0);
            isCapRound = array.getBoolean(R.styleable.circleProgressView_isCapRound, true);
            if (fromAngle < 0){
                fromAngle = 0;
            }else if (fromAngle > 360){
                fromAngle = -90 + fromAngle%360;
            }
            array.recycle();
        }
        if (isCapRound){
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        mPaint.setStrokeWidth(progressWidth);
        mPaint.setColor(strokeColor);

        mBgPaint.setStrokeWidth(defaultStrokeWidth);
        mBgPaint.setColor(strokeBgColor);
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.prayer_progress_dot);
//        int screenWidth = DeviceHelper.getScreenWidth(ObjectStore.getContext());
//        if (screenWidth < 720){
//            mRadius = (int) ObjectStore.getContext().getResources().getDimension(R.dimen.dp_65);
//        }
        ovl = new RectF(getPaddingLeft() + defaultStrokeWidth/2, getPaddingTop()  + defaultStrokeWidth/2, getPaddingLeft() + 2 * mRadius  + defaultStrokeWidth * 3/2, getPaddingTop() + 2 * mRadius + defaultStrokeWidth*3/2);
    }

    public void setRadius(int radius){
        ovl = new RectF(getPaddingLeft() + defaultStrokeWidth/2, getPaddingTop()  + defaultStrokeWidth/2, getPaddingLeft() + 2 * mRadius  + defaultStrokeWidth * 3/2, getPaddingTop() + 2 * mRadius + defaultStrokeWidth*3/2);
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension((int)(getPaddingLeft() + getPaddingRight() + 2 *(mRadius + defaultStrokeWidth)),  (int)(getPaddingTop() + getPaddingBottom() + 2 *(mRadius + defaultStrokeWidth)));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(ovl, fromAngle - 90, 360, false, mBgPaint);
        canvas.drawArc(ovl, fromAngle - 90, 360 * mProgress/100, false, mPaint);
        canvas.drawCircle(mRadius+defaultStrokeWidth,mRadius+defaultStrokeWidth,mRadius,mSolidCirclePaint);
//        if (mProgress != 0)
//        canvas.drawBitmap(mBitmap, (float) (mRadius + defaultStrokeWidth/2 + (mRadius + defaultStrokeWidth/2) *Math.sin( 2*PI/360*360 * mProgress/100) - mBitmap.getWidth()/2 + getPaddingLeft() + defaultStrokeWidth/2),
//                (float)(mRadius + defaultStrokeWidth/2 - (mRadius + defaultStrokeWidth/2) *Math.cos( 2*PI/360*360 * mProgress/100) - mBitmap.getHeight()/2 + getPaddingTop() + defaultStrokeWidth/2), mBitmapPaint);
    }


    /**
     * 设置当前进度
     *
     * @param progress 当前进度（0-100）
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setStrokeColor(String color ){
        mBgPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    public void setProgressColor(String color){
        mPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    public void setFillCircleColor(String color){
        mSolidCirclePaint.setColor(Color.parseColor(color));
        invalidate();
    }

    /**
     * 设置当前进度，并展示进度动画。如果动画时间小于等于0，则不展示动画
     *
     * @param progress 当前进度（0-100）
     * @param animTime 动画时间（毫秒）
     */
    public void setProgress(int progress, long animTime, OnProgressListener onProgressListener) {
        if (animTime <= 0) setProgress(progress);
        else {
            ValueAnimator animator = ValueAnimator.ofInt(0, progress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int) animation.getAnimatedValue();
                    if (mProgress <= progress){
                        if (onProgressListener != null){
                            onProgressListener.onProgress(mProgress);
                        }
                        invalidate();
                    }
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }

    public interface OnProgressListener{
        void onProgress(int progress);
    }
}
