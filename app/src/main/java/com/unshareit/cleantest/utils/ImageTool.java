package com.unshareit.cleantest.utils;

import android.graphics.Point;

import java.util.Stack;

public class ImageTool {
    private Stack<Point> mStacks = new Stack<>();
    private int mBorderColor = -1;
    private boolean hasBorderColor = false;

    public ImageTool( int mBorderColor ,boolean hasBorderColor ){
        this.mBorderColor = mBorderColor ;
        this.hasBorderColor= hasBorderColor ;
    }

    /**
     * @param pixels   像素数组
     * @param currentTouchPixel    当前触摸点的 颜色
     * @param newColor 填充色
     */
    public void fillColor(int[] pixels, int bitmapWidth, int bitmapHeight, int currentTouchPixel, int newColor, int x, int y) {
        //步骤1：将种子点(x, y)入栈；
        mStacks.push(new Point(x, y));

        //步骤2：判断栈是否为空，
        // 如果栈为空则结束算法，否则取出栈顶元素作为当前扫描线的种子点(x, y)，
        // y是当前的扫描线；

        while (!mStacks.isEmpty()) {
            /*
             * 步骤3：从种子点(x, y)出发，沿当前扫描线向左、右两个方向填充，直到边界。
             * 分别标记区段的左、右端点坐标为xLeft和xRight
             */
            Point seed = mStacks.pop();
            int count = fillLineLeft(pixels, currentTouchPixel, bitmapWidth, newColor, seed.x, seed.y);
            int left = seed.x - count + 1;

            count = fillLineRight(pixels, currentTouchPixel, bitmapWidth,  newColor, seed.x + 1, seed.y);
            int right = seed.x + count;

            //从y-1找种子
            if (seed.y - 1 >= 0){
                findSeedInNewLine(pixels, currentTouchPixel, bitmapWidth,  seed.y - 1, left, right);
            }
            //从y+1找种子
            if (seed.y + 1 < bitmapHeight){
                findSeedInNewLine(pixels, currentTouchPixel, bitmapWidth,  seed.y + 1, left, right);
            }
        }
    }

    /**
     * 往左填色，返回填色的数量值
     */
    private int fillLineLeft(int[] pixels, int pixel, int bitmapWidth, int newColor, int x, int y) {
        int count = 0;
        while (x >= 0) {
            int index = y * bitmapWidth + x;
            if (needFillPixel(pixels, pixel, index)) {
                pixels[index] = newColor;
                count++;
                x--;

            } else {
                break;
            }
        }
        return count;
    }

    /**
     * 往右填色，返回填充的个数
     */
    private int fillLineRight(int[] pixels, int currentTouchPixel, int bitmapWidth,  int newColor, int x, int y) {
        int count = 0;

        while (x < bitmapWidth) {
            int index = y * bitmapWidth + x;
            if (needFillPixel(pixels, currentTouchPixel, index)) {
                pixels[index] = newColor;
                count++;
                x++;
            } else {
                break;
            }
        }
        return count;
    }

    private boolean needFillPixel(int[] pixels, int pixel, int index) {
        if (hasBorderColor) {
            return pixels[index] != mBorderColor;
        } else {
            // 这个值是经过多次实验的来的，取这个值锯齿效果不那么明显
            return pixels[index] > 0xFFBBBBBB ;
            // 直接跟当前pixel比较，有较明显的锯齿，原因不明
            //return pixels[index] > pixel ;
        }

    }

    /**
     * 在新行找种子节点
     */
    private void findSeedInNewLine(int[] pixels, int currentTouchPixel, int bitmapWidth,  int indexY, int left, int right) {
        // 获得该行的开始索引
        int begin = indexY * bitmapWidth + left;
        // 获得该行的结束索引
        int end = indexY * bitmapWidth + right;

        boolean hasSeed = false;
        int newSeedIndexX ;

        while (end >= begin) {
            if (pixels[end] == currentTouchPixel) {
                if (!hasSeed) {
                    newSeedIndexX = end % bitmapWidth;
                    mStacks.push(new Point(newSeedIndexX, indexY));
                    hasSeed = true;
                }
            } else {
                hasSeed = false;
            }
            end--;
        }
    }
}
