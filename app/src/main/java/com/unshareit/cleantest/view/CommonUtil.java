package com.unshareit.cleantest.view;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

public class CommonUtil {
    /**
     * dp转成px
     */
    public static int dp2px(@NonNull Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int getScreenWidthInPx(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeightInPx(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
