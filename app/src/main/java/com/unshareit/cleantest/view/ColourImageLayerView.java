package com.unshareit.cleantest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.helper.widget.Layer;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.unshareit.cleantest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class ColourImageLayerView extends AppCompatImageView {
    private LayerDrawable mDrawables;

    private int myColor;        //接收选择的颜色
    private boolean myColorType;    //判断是否选择颜色，选择为true，未选择为false
    private Bitmap mBitmap;
    //边界颜色
    private int mBorderColor = -1;
    private boolean hasBorderColor = false;
    private Stack<Point> mStacks = new Stack<Point>();   //栈
    private List<Point> tempPoints = new ArrayList<>();
    int width,height;
    int index;
    public void setMyColor(int myColor) {
        this.myColor = myColor;
    }
    public void setMyColorType(boolean myColorType) {
        this.myColorType = myColorType;
    }
    public ColourImageLayerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        Drawable[] layers = new Drawable[50];
//        layers[0] = getResources().getDrawable(R.drawable.test);
//        layers[1] = getResources().getDrawable(R.drawable.icon_speed_up);
        mDrawables = new LayerDrawable(layers);
        setColor(125,125,125,0);
//        mDrawables.setLayerInset(1,200,200,500,500);
//        setBackground(mDrawables);
//        mDrawables = (LayerDrawable) getBackground();
        Log.e("MainActivity","setBackground===>");
    }

    public LayerDrawable getLayerDrawable(){
        return mDrawables;
    }

    public void setLayerDrawable(LayerDrawable layerDrawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDrawables.setDrawable(0,layerDrawable);
        }
    }

    public void setLayer(Drawable drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDrawables.setDrawable(0,drawable);
            setImageDrawable(mDrawables);
        }
        Log.e("MainActivity","setLayer===>"+drawable+" mDrawable=>"+mDrawables);
    }

    public void addLayer(Drawable drawable,int index){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mDrawables.addLayer(drawable);
            mDrawables.setDrawable(0,getDrawable());
            mDrawables.setDrawable(index,drawable);
            setImageDrawable(mDrawables);
        }
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Bitmap bmp = mBitmap;
        mDrawables.setBounds(0, 0, width, height);
        mDrawables.draw(new Canvas(bmp));
        setImageDrawable(new BitmapDrawable(bmp));
        Log.e("MainActivity","addLayer===>"+drawable+" mDrawable=>"+mDrawables);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                List<Point> points = new ArrayList<>();
//                points.addAll(tempPoints);
//                tempPoints.clear();
//                for (Point point: points){
//                    fillColorToSameArea2(point.x,point.y);
//                }
//            }
//        }.start();

        Log.e("MainActivity","addLayer===>"+mBitmap);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 规定让图片按比例显示
        width = measureWidth(widthMeasureSpec);
        height = measureWidth(heightMeasureSpec);
        setMeasuredDimension(width, height);

//        //根据drawable，去得到一个和view一样大小的bitmap
//        int width = mDrawables.getIntrinsicWidth();
//        int height = mDrawables.getIntrinsicHeight();
        if (mBitmap == null){
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            mDrawables.setBounds(0, 0, width, height);
            Canvas canvas = new Canvas(mBitmap);
            mDrawables.draw(canvas);
            setImageDrawable(new BitmapDrawable(mBitmap));
            Log.e("MainActivity","drawable===>"+mBitmap);
        }


//        Bitmap bm = drawable.getBitmap();
//        mBitmap = Bitmap.createScaledBitmap(bm, getMeasuredWidth(), getMeasuredHeight(), false);
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //这样，当时用wrap_content时，View就获得一个默认值200px，而不是填充整个父布局。
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final float x = event.getX();
        final float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.e("MainActivity","x==>"+x+" y==>"+y);
            //填色
            fillColorToSameArea((int)x, (int)y);
//            Drawable drawable = findDrawable(x, y);
//            if (drawable != null)
//                drawable.setColorFilter(randomColor(), PorterDuff.Mode.SRC_IN);
        }

        return super.onTouchEvent(event);
    }

    private int randomColor()
    {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return color;
    }

    private Drawable findDrawable(float x, float y, BitmapDrawable mBitmap)
    {
        final int numberOfLayers = mDrawables.getNumberOfLayers();
        Drawable drawable = null;
        Bitmap bitmap = null;
        for (int i = numberOfLayers - 1; i >= 0; i--)
        {
            drawable = mDrawables.getDrawable(i);
            if (drawable == null)
                continue;
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (mBitmap.getBitmap() == bitmap){
                Log.e("MainActivity","index==>"+i);
            }
            try
            {
                int pixel = bitmap.getPixel((int) x, (int) y);
                if (pixel == Color.TRANSPARENT)
                {
                    continue;
                }
            } catch (Exception e)
            {
                continue;
            }
            index = i;
            return drawable;
        }
        return null;
    }


    /**
     * 根据x,y获得该点颜色，进行填充
     * @param x
     * @param y
     */
    private void fillColorToSameArea(int x, int y)
    {
        Bitmap bm = mBitmap;
        Log.e("MainActivity","fillColorToSameArea===>"+mBitmap);
        int pixel = bm.getPixel(x, y);
        if (pixel == Color.TRANSPARENT || pixel == Color.WHITE || pixel == Color.BLACK || (hasBorderColor && mBorderColor == pixel))
        {
            return;
        }
        int newColor;
        if(myColorType)
            newColor = myColor;
        else
            newColor = randomColor();

        int w = bm.getWidth();
        int h = bm.getHeight();
        //拿到该bitmap的颜色数组
        int[] pixels = new int[w * h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        //填色
        tempPoints.add(new Point(x,y));
        fillColor(pixels, w, h, pixel, newColor, x, y);
        //重新设置bitmap
        bm.setPixels(pixels, 0, w, 0, 0, w, h);
        mBitmap = bm;
        setImageDrawable(new BitmapDrawable(bm));
    }

    /**
     * 根据x,y获得该点颜色，进行填充
     * @param x
     * @param y
     */
    private void fillColorToSameArea2(int x, int y)
    {
        Bitmap bm = mBitmap;
        Log.e("MainActivity","fillColorToSameArea===>"+mBitmap);
        int pixel = bm.getPixel(x, y);
        if (pixel == Color.TRANSPARENT  || pixel == Color.BLACK || (hasBorderColor && mBorderColor == pixel))
        {
            return;
        }
        int newColor;
        if(myColorType)
            newColor = myColor;
        else
            newColor = randomColor();

        int w = bm.getWidth();
        int h = bm.getHeight();
        //拿到该bitmap的颜色数组
        int[] pixels = new int[w * h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        //填色
        tempPoints.add(new Point(x,y));
        fillColor(pixels, w, h, pixel, newColor, x, y);
        //重新设置bitmap
        bm.setPixels(pixels, 0, w, 0, 0, w, h);
        mBitmap = bm;
        setImageDrawable(new BitmapDrawable(bm));
    }


    /**
     * @param pixels   像素数组
     * @param w        宽度
     * @param h        高度
     * @param pixel    当前点的颜色
     * @param newColor 填充色
     * @param i        横坐标
     * @param j        纵坐标
     */
    private void fillColor(int[] pixels, int w, int h, int pixel, int newColor, int i, int j)
    {
        //步骤1：将种子点(x, y)入栈；
        mStacks.push(new Point(i, j));

        //步骤2：判断栈是否为空，
        // 如果栈为空则结束算法，否则取出栈顶元素作为当前扫描线的种子点(x, y)，
        // y是当前的扫描线；
        while (!mStacks.isEmpty())
        {
            /**
             * 步骤3：从种子点(x, y)出发，沿当前扫描线向左、右两个方向填充，
             * 直到边界。分别标记区段的左、右端点坐标为xLeft和xRight；
             */
            Point seed = mStacks.pop();
            //L.e("seed = " + seed.x + " , seed = " + seed.y);
            int count = fillLineLeft(pixels, pixel, w, h, newColor, seed.x, seed.y);
            int left = seed.x - count + 1;
            count = fillLineRight(pixels, pixel, w, h, newColor, seed.x + 1, seed.y);
            int right = seed.x + count;


            /**
             * 步骤4：
             * 分别检查与当前扫描线相邻的y - 1和y + 1两条扫描线在区间[xLeft, xRight]中的像素，
             * 从xRight开始向xLeft方向搜索，假设扫描的区间为AAABAAC（A为种子点颜色），
             * 那么将B和C前面的A作为种子点压入栈中，然后返回第（2）步；
             */
            //从y-1找种子
            if (seed.y - 1 >= 0)
                findSeedInNewLine(pixels, pixel, w, h, seed.y - 1, left, right);
            //从y+1找种子
            if (seed.y + 1 < h)
                findSeedInNewLine(pixels, pixel, w, h, seed.y + 1, left, right);
        }
    }
    /**
     * 在新行找种子节点
     *
     * @param pixels
     * @param pixel
     * @param w
     * @param h
     * @param i
     * @param left
     * @param right
     */
    private void findSeedInNewLine(int[] pixels, int pixel, int w, int h, int i, int left, int right)
    {
        /**
         * 获得该行的开始索引
         */
        int begin = i * w + left;
        /**
         * 获得该行的结束索引
         */
        int end = i * w + right;
        boolean hasSeed = false;
        int rx = -1, ry = -1;
        ry = i;
        /**
         * 从end到begin，找到种子节点入栈（AAABAAAB，则B前的A为种子节点）
         */
        while (end >= begin)
        {
            if (pixels[end] == pixel)
            {
                if (!hasSeed)
                {
                    rx = end % w;
                    mStacks.push(new Point(rx, ry));
                    hasSeed = true;
                }
            } else
            {
                hasSeed = false;
            }
            end--;
        }
    }
    /**
     * 往右填色，返回填充的个数
     *
     * @return
     */
    private int fillLineRight(int[] pixels, int pixel, int w, int h, int newColor, int x, int y)
    {
        int count = 0;

        while (x < w)
        {
            //拿到索引
            int index = y * w + x;
            if (needFillPixel(pixels, pixel, index))
            {
                pixels[index] = newColor;
                count++;
                x++;
            } else
            {
                break;
            }
        }
        return count;
    }
    /**
     * 往左填色，返回填色的数量值
     *
     * @return
     */
    private int fillLineLeft(int[] pixels, int pixel, int w, int h, int newColor, int x, int y)
    {
        int count = 0;
        while (x >= 0)
        {
            //计算出索引
            int index = y * w + x;
            if (needFillPixel(pixels, pixel, index))
            {
                pixels[index] = newColor;
                count++;
                x--;
            } else
            {
                break;
            }
        }
        return count;
    }
    private boolean needFillPixel(int[] pixels, int pixel, int index)
    {
//        if (hasBorderColor)
//        {
//            return pixels[index] != mBorderColor;
//        } else
//        {
//            return pixels[index] == pixel;
//        }

        if (hasBorderColor)
        {
            return pixels[index] != mBorderColor;
        } else
        {
            int color = pixels[index];
            if (color == pixel){
                return true;
            }else if(Color.red(color) == 222 && Color.blue(color) == 222 && (Color.green(color) >= 219 && Color.green(color) < 223)){
                return true;
            }else{
                return false;
            }
//            if(color == pixel){
//                return true;
//            }else if(Color.red(color)>150&&Color.blue(color)>150&&Color.green(color)>150){
//                return true;
//            }else{
//                return false;
//            }
        }
    }

    //如果选择了颜色，传递选中的颜色，并设置为true
    public void setColor(int a,int r,int g,int b){
        setMyColor(Color.argb(a,r,g,b));
        setMyColorType(true);
    }


}
