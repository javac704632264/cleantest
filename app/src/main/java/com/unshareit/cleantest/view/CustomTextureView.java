package com.unshareit.cleantest.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;

public class CustomTextureView extends TextureView {
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private MediaPlayer mediaPlayer;
    private Surface s;

    public CustomTextureView(Context context) {
        this(context, null);
//        init();
    }

    public CustomTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
//        init();
    }

    public CustomTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

    public void startVideo() {
        // 设置监听器，监听SurfaceTexture的准备情况
        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // 初始化MediaPlayer并设置Surface
                s = new Surface(surface);
                mediaPlayer = new MediaPlayer();
                new Thread(new MediaThread()).start();
//                mediaPlayer.setSurface(s);
//
//                try {
//                    // 设置要播放的视频文件的路径
//                    // 将MediaPlayer音量设置为零，即静音
//                    mediaPlayer.setVolume(0, 0);
//                    mediaPlayer.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // 视图尺寸发生变化时的处理
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                // SurfaceTexture销毁时的处理
                release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // SurfaceTexture更新时的处理
            }
        });
    }

    public void release(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class MediaThread implements Runnable{

        @Override
        public void run() {
            try {
                /**
                 * rtmp://live.hkstv.hk.lxdns.com/live/hks
                 rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
                 mms://space.hngd.gov.cn/live1
                 http://movie.ks.js.cn/flv/other/1_0.flv
                 http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8
                 */
                String uri = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
                mediaPlayer.setSurface(s);
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.setDataSource(uri);
                // mMediaPlayer.setDataSource(this, Uri.parse(uri));
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        System.out.println("===========video=================" + width + "   " + height + "    " + mp.getVideoWidth() + "   " + mp.getVideoHeight());
                        //尺寸动态变化
                        if (mp != null && width > 0 && height > 0 && mp.getVideoHeight() > 0 && mp.getVideoWidth() > 0) {
                            int videoWidth = mp.getVideoWidth();
                            int videoHeight = mp.getVideoHeight();
                            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                            int screenwidth = wm.getDefaultDisplay().getWidth();
                            float size = (videoHeight * 1.0f) / (videoWidth * 1.0f);
                            int surfaceHeight = (int) (screenwidth *size);
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                            // layoutParams.width = width;
                            layoutParams.height = surfaceHeight;
                            setLayoutParams(layoutParams);
                        }
                    }
                });
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
