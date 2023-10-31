package com.ai.networklibrary.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.ai.networklibrary.convert.BitmapConvert;

import okhttp3.Response;

/**
 * Created by hao on 2018/2/26.
 */

public abstract class BitmapCallback extends AbsCallback<Bitmap> {
    private BitmapConvert convert;

    public BitmapCallback() {
        convert = new BitmapConvert();
    }

    public BitmapCallback(int maxWidth, int maxHeight) {
        convert = new BitmapConvert(maxWidth, maxHeight);
    }

    public BitmapCallback(int maxWidth, int maxHeight, Bitmap.Config decodeConfig, ImageView.ScaleType scaleType) {
        convert = new BitmapConvert(maxWidth, maxHeight, decodeConfig, scaleType);
    }

    @Override
    public Bitmap convertResponse(Response response) throws Throwable {
        Bitmap bitmap = convert.convertResponse(response);
        response.close();
        return bitmap;
    }
}
