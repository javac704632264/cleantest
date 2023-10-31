package com.unshareit.cleantest.clean.document;


import static com.unshareit.cleantest.clean.document.DocumentFilePermission.ANDROID_DATA_URI;

import android.app.Activity;
import android.content.Context;


public class DataPermissionUtils {
    /**
     * 判断是否已经获取了Data权限
     */
    public static boolean isGrant(Context context) {
        return DocumentFilePermission.isGrant(context, ANDROID_DATA_URI);
    }

    /**
     * android 11 android/data目录授权
     * @param context
     * @param REQUEST_CODE_FOR_DIR
     */
    public static void startDataAuthorize(Activity context, int REQUEST_CODE_FOR_DIR) {
        DocumentFilePermission.startForSpecificUriRoot(context, ANDROID_DATA_URI, REQUEST_CODE_FOR_DIR);
    }
}
