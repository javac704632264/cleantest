package com.unshareit.cleantest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * This imageView can show blur image.
 * Then it will be expanded to automatically display one image with two styles
 * one is small and blurry, another is the origin image,So here are two urls for these two images.
 */
public class BlurImageView extends RelativeLayout {

  public final static int DEFAULT_BLUR_FACTOR = 8;
  private Context mContext;

  private int mBlurFactor = DEFAULT_BLUR_FACTOR;

  private String mBlurImageUrl, mOriginImageUrl;
  private int greyColor = Color.parseColor("#66CCCCCC");
  private Drawable defaultDrawable = new ColorDrawable(greyColor);
  private Drawable failDrawable = new Drawable() {
    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Override
    public void draw(Canvas canvas) {
      canvas.drawColor(greyColor);
      String failString = "load failure";
      canvas.translate((canvas.getWidth() - textPaint.measureText(failString)) / 2,
          canvas.getHeight()/2);
      textPaint.setColor(Color.DKGRAY);
      textPaint.setTextSize(30);
      canvas.drawText(failString, 0, failString.length(), textPaint);
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
      return 0;
    }
  };
  private ImageLoader imageLoader;
  private DisplayImageOptions displayImageOptions;

  private ImageView imageView;
  private LoadingCircleProgressView loadingCircleProgressView;

  private boolean enableProgress = true;

  public BlurImageView(Context context) {
    this(context, null);
  }

  public BlurImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mContext = context.getApplicationContext();
    init();
  }

  private void init() {
    initUIL();
    imageLoader = ImageLoader.getInstance();

    initChildView();
    initDisplayImageOptions();
  }

  private void initUIL() {
    ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(mContext);
    config.threadPriority(Thread.NORM_PRIORITY - 2);
    config.denyCacheImageMultipleSizesInMemory();
    config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
    config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
    config.tasksProcessingOrder(QueueProcessingType.LIFO);
    config.writeDebugLogs(); // Remove for release app

    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(config.build());
  }

  private void initDisplayImageOptions() {
    displayImageOptions = new DisplayImageOptions.Builder()
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
  }

  private void initChildView() {
    imageView = new ImageView(mContext);
    imageView.setLayoutParams(
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    imageView.setImageDrawable(defaultDrawable);

    loadingCircleProgressView = new LoadingCircleProgressView(mContext);
    LayoutParams progressBarLayoutParams =
        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    progressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    loadingCircleProgressView.setLayoutParams(progressBarLayoutParams);
    loadingCircleProgressView.setVisibility(GONE);

    addView(imageView);
    addView(loadingCircleProgressView);
  }

  private SimpleImageLoadingListener blurLoadingListener = new SimpleImageLoadingListener() {
    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
      imageView.setImageDrawable(failDrawable);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
      imageView.setImageBitmap(getBlurBitmap(loadedImage));
    }
  };

  private SimpleImageLoadingListener fullLoadingListener = new SimpleImageLoadingListener() {
    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
      imageView.setImageDrawable(failDrawable);
      Log.e("Image Load error", "cannot load Small image, please check url or network status");
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
      imageView.setImageBitmap(getBlurBitmap(loadedImage));
      imageLoader.displayImage(mOriginImageUrl, imageView, displayImageOptions,
          new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
              setLoadingProgressRatio(5, 100);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
              setLoadingProgressRatio(5, 100);
              Log.e("Image Load error", "cannot load Big image, please check url or network status");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
              setLoadingProgressRatio(100, 100);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
              Log.w("Image Load cancel", "the image loading process is cancelled");
              setLoadingProgressRatio(100, 100);
            }
          },
          new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
              if (!enableProgress) {
                return;
              }
              setLoadingProgressRatio(current, total);
            }
          });
    }
  };

  private void setLoadingProgressRatio(int current, int total) {
    if (current < total) {
      if (loadingCircleProgressView.getVisibility() == GONE) {
        loadingCircleProgressView.setVisibility(VISIBLE);
      }
      loadingCircleProgressView.setCurrentProgressRatio((float) current / total);
    } else {
      loadingCircleProgressView.setVisibility(GONE);
    }
  }

  /**
   * This method will fetch bitmap from resource and make it blurry, display
   * @param blurImageRes the image resource id which is needed to be blurry
   */
  public void setBlurImageByRes(int blurImageRes) {
    buildDrawingCache();
    Bitmap blurBitmap = FastBlurUtil.doBlur(getDrawingCache(), mBlurFactor, true);
    imageView.setImageBitmap(blurBitmap);
  }

  /**
   * This image won't be blurry.
   * @param originImageRes The origin image resource id.
   */
  public void setOriginImageByRes(int originImageRes) {
    Bitmap originBitmap = BitmapFactory.decodeResource(mContext.getResources(), originImageRes);
    imageView.setImageBitmap(originBitmap);
  }

  public void setBlurImageByUrl(String blurImageUrl) {
    mBlurImageUrl = blurImageUrl;
    cancelImageRequestForSafty();
    imageLoader.loadImage(blurImageUrl, blurLoadingListener);
  }

  public void setOriginImageByUrl(String originImageUrl) {
    mOriginImageUrl = originImageUrl;
    imageLoader.displayImage(originImageUrl, imageView);
  }

  /**
   * This will load two Images literally. The small size blurry one and the big size original one.
   * @param blurImageUrl This is a small image url and will be loaded fast and will be blurry
   *                     automatically.
   * @param originImageUrl After show the blurry image, it will load the origin image automatically
   *                       and replace the blurry one after finish loading.
   */
  public void setFullImageByUrl(String blurImageUrl, String originImageUrl) {
    mBlurImageUrl = blurImageUrl;
    mOriginImageUrl = originImageUrl;
    cancelImageRequestForSafty();
    imageLoader.loadImage(blurImageUrl, displayImageOptions, fullLoadingListener);
  }

  public Bitmap getBlurBitmap(Bitmap loadedBitmap) {
    // make this bitmap mutable
    loadedBitmap = loadedBitmap.copy(loadedBitmap.getConfig(), true);
    return FastBlurUtil.doBlur(loadedBitmap, getBlurFactor(), true);
  }

  private int getBlurFactor() {
    return mBlurFactor;
  }

  public void setBlurFactor(int blurFactor) {
    if (blurFactor < 0) {
      throw new IllegalArgumentException("blurFactor must not be less than 0");
    }
    mBlurFactor = blurFactor;
  }

  public void cancelImageRequestForSafty() {
    imageLoader.cancelDisplayTask(imageView);
  }

  public void clear() {
    cancelImageRequestForSafty();
    imageView.setImageBitmap(null);
  }

  /**
   * If you disable progress, then it won't show a loading progress view when you're loading image.
   * Default the progress view is enabled.
   */
  public void disableProgress() {
    this.enableProgress = false;
  }

  public void setProgressBarBgColor(int bgColor) {
    this.loadingCircleProgressView.setProgressBgColor(bgColor);
  }

  public void setProgressBarColor(int color) {
    this.loadingCircleProgressView.setProgressColor(color);
  }

  public void setFailDrawable(Drawable failDrawable) {
    this.failDrawable = failDrawable;
  }

  public void setDefaultDrawable(Drawable defaultDrawable) {
    this.defaultDrawable = defaultDrawable;
  }

}
