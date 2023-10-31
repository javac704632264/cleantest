package com.ai.subscription.util;

import static android.renderscript.Allocation.USAGE_SCRIPT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageSaveUtils {

    //创建文件的方法

    public static boolean createFile(String path) throws IOException {
        boolean isSuccess = true;
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (parentFile.exists()) {
            if (!parentFile.isDirectory()) {
                isSuccess &= deleteFolder(parentFile.getAbsolutePath());
                isSuccess &= createDirectory(parentFile.getAbsolutePath());
            }
        } else {
            isSuccess &= createDirectory(parentFile.getAbsolutePath());
        }

        if (!file.exists()) {
            isSuccess &= file.createNewFile();
        } else {
            if (file.isFile()) {
                return file.canRead() & file.canWrite();
            }

            isSuccess &= deleteFolder(file.getAbsolutePath());
            if (isSuccess) {
                isSuccess &= createFile(file.getAbsolutePath());
            }
        }

        return isSuccess;
    }

    public static boolean deleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (!file.exists()) {
            return true;
        } else {
            return file.isFile() ? deleteFile(sPath) : deleteDirectory(sPath);
        }
    }

    @SuppressLint({"NewApi"})
    public static boolean createDirectory(String path) {
        File dir = new File(path);
        boolean isSuccess = true;
        File parentFile = dir.getParentFile();
        if (parentFile.exists()) {
            if (!parentFile.isDirectory()) {
                isSuccess &= deleteFolder(parentFile.getAbsolutePath());
                isSuccess &= createDirectory(parentFile.getAbsolutePath());
            }
        } else {
            isSuccess &= createDirectory(parentFile.getAbsolutePath());
        }

        if (!dir.exists()) {
            isSuccess &= dir.mkdirs();
        } else {
            if (dir.isDirectory()) {
                return dir.canExecute() & dir.canRead() & dir.canWrite();
            }

            isSuccess &= deleteFolder(dir.getAbsolutePath());
            if (isSuccess) {
                isSuccess &= createDirectory(dir.getAbsolutePath());
            }
        }

        return isSuccess;
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (!file.exists()) {
            return true;
        } else {
            if (file.isFile()) {
                flag = file.delete();
            }

            return flag;
        }
    }

    public static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }

        File dirFile = new File(sPath);
        if (!dirFile.exists()) {
            return true;
        } else if (!dirFile.isDirectory()) {
            return false;
        } else {
            boolean flag = true;
            File[] files = dirFile.listFiles();
            if (files != null && files.length > 0) {
                if (null != files) {
                    for(int i = 0; i < files.length; ++i) {
                        if (files[i].isFile()) {
                            flag = deleteFile(files[i].getAbsolutePath());
                            if (!flag) {
                                break;
                            }
                        } else {
                            flag = deleteDirectory(files[i].getAbsolutePath());
                            if (!flag) {
                                break;
                            }
                        }
                    }
                }

                if (!flag) {
                    return false;
                } else {
                    return dirFile.delete();
                }
            } else {
                return dirFile.delete();
            }
        }
    }

    /***
     * 获取拍照后的路径
     * @param data
     * @return
     */
    public static String getTakePhotoPath(Context context,Intent data) {
        Bitmap photo = null;
        Uri uri = data.getData();
        if (uri != null) {
            photo = BitmapFactory.decodeFile(uri.getPath());
        }
        if (photo == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                photo = (Bitmap) bundle.get("data");
            } else {
                return "";
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            // 获取 SD 卡根目录
            String saveDir = context.getExternalCacheDir().getAbsolutePath()+ File.separator+"camera";//Environment.getExternalStorageDirectory() + "/fiberstore_photos";
            // 新建目录
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdir();
            // 生成文件名
//            SimpleDateFormat t = new SimpleDateFormat("xiebin");
            String filename = System.currentTimeMillis()+".png";
            /**新建文件*/
            File file = new File(saveDir, filename);
            /***打开文件输出流*/
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            /**
             * 对应Bitmap的compress(Bitmap.CompressFormat format, int quality, OutputStream stream)方法中第一个参数。
             * CompressFormat类是个枚举，有三个取值：JPEG、PNG和WEBP。其中，
             * PNG是无损格式（忽略质量设置），会导致方法中的第二个参数压缩质量失效，
             * JPEG不解释，
             * 而WEBP格式是Google新推出的，据官方资料称“在质量相同的情况下，WEBP格式图像的体积要比JPEG格式图像小40%。
             */
            photo.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            /***相片的完整路径*/
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    //读取图片旋转角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            LogW.out("readPictureDegree : orientation = " + orientation);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //旋转图片
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotation = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return rotation;
    }

    //使用RenderScript
    public static Bitmap rsBlur(Context context, Bitmap source, float radius, float scale){
        int scaleWidth = (int) (source.getWidth() * scale);
        int scaleHeight = (int) (source.getHeight() * scale);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, scaleWidth,
                scaleHeight,false);

        Bitmap inputBitmap = scaledBitmap;

        //创建RenderScript
        RenderScript renderScript = RenderScript.create(context);

        //创建Allocation
        Allocation input = Allocation.createFromBitmap(renderScript, inputBitmap,  Allocation.MipmapControl.MIPMAP_NONE, USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        //创建ScriptIntrinsic
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        intrinsicBlur.setInput(input);

        intrinsicBlur.setRadius(radius);

        intrinsicBlur.forEach(output);

        output.copyTo(inputBitmap);

        renderScript.destroy();

        return inputBitmap;
    }
}
