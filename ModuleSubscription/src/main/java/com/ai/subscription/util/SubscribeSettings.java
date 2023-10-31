package com.ai.subscription.util;

import android.content.Context;

import com.doodlecamera.base.core.settings.Settings;
import com.doodlecamera.base.core.utils.lang.ObjectStore;

public class SubscribeSettings extends Settings {
    private static final String SETTINGS_NAME = SubscribeCipher.fileName();

    private static final String KEY_QUERY_SUBSCRIBE_COUNT = SubscribeCipher.keyQuerySubscribeCount();

    private static final String KEY_SUB_SUCCESS_PRODUCT_ID = SubscribeCipher.keySubSuccessProductId();

    // 1 已付费    0  未付费
    private static final String KEY_SUBSCRIBE_SUCCESS = SubscribeCipher.keySubscribeSuccessStatus();

    private static final String KEY_SUBSCRIBE_SUCC_TIME = SubscribeCipher.keySubscribeSuccessTime();

    // $4.99/year
    private static final String KEY_SUB_SUCCESS_PRICE = SubscribeCipher.keySubSuccessPrice();

    private static final String KEY_SUB_QUIT_INTERCEPT_LAST_TIME = "sub_quit_intercept_last_show_time";

    private static final String KEY_SUB_PRODUCT_DETAILS = "sub_product_detail";
    private static final String KEY_SUB_PRODUCT_DETAILS_LAST_CACHE_TIME = "sub_product_detail_last_cache_time";

    private static final String KEY_SUB_PRODUCT_DETAILS_PRICE = "sub_product_detail_price";

    private static final String KEY_SUB_CONNECT_SUCCESSFUL_TIME = "sub_connect_successful_time";
    private static final String KEY_SUB_CONNECT_START_TIME = "sub_connect_start_time";

    private static final String KEY_SUB_ENTER_TIME = "sub_enter_time";

    private static SubscribeSettings sInstance = null;

    public static synchronized SubscribeSettings getInstance() {
        if (sInstance == null)
            sInstance = new SubscribeSettings(ObjectStore.getContext());
        return sInstance;
    }

    private SubscribeSettings(Context context) {
        super(context, SETTINGS_NAME);
    }

    public static void setProductId(String id) {
        getInstance().set(KEY_SUB_SUCCESS_PRODUCT_ID, id);
    }

    public static String getProductId() {
        return getInstance().get(KEY_SUB_SUCCESS_PRODUCT_ID, "");
    }

    public static void setProductPrice(String price) {
        getInstance().set(KEY_SUB_SUCCESS_PRICE, price);
    }

    public static String getProductPrice() {
        return getInstance().get(KEY_SUB_SUCCESS_PRICE, "");
    }

    public static void resetProduct() {
        setProductPrice("");
        setProductId("");
        setSubscribeState(0);
        setSubscribeSuccTime(-1);
    }

    public static int getKeyQuerySubscribeCount() {
        return getInstance().getInt(KEY_QUERY_SUBSCRIBE_COUNT, 0);
    }

    public static void setKeyQuerySubscribeCount(int number) {
        getInstance().setInt(KEY_QUERY_SUBSCRIBE_COUNT, number);
    }

    public static int getSubscribeState() {
        return getInstance().getInt(KEY_SUBSCRIBE_SUCCESS, 0);
    }

    public static void setSubscribeState(int number) {
        getInstance().setInt(KEY_SUBSCRIBE_SUCCESS, number);
    }

    public static long getSubscribeSuccTime() {
        return getInstance().getLong(KEY_SUBSCRIBE_SUCC_TIME, -1);
    }

    public static long getLastShowEnterVIPTime() {
        return getInstance().getLong(KEY_SUB_ENTER_TIME, -1);
    }

    public static void setShowEnterVIPTime() {
        getInstance().setLong(KEY_SUB_ENTER_TIME, System.currentTimeMillis());
    }

    public static void setSubscribeSuccTime(long millsTime) {
        getInstance().setLong(KEY_SUBSCRIBE_SUCC_TIME, millsTime);
    }

    public static long getQuitInterceptTime() {
        return getInstance().getLong(KEY_SUB_QUIT_INTERCEPT_LAST_TIME, -1);
    }

    public static void setQuitInterceptTime(long millsTime) {
        getInstance().setLong(KEY_SUB_QUIT_INTERCEPT_LAST_TIME, millsTime);
    }

    public static String getProductDetails() {
        return getInstance().get(KEY_SUB_PRODUCT_DETAILS, "");
    }

    public static void setProductDetails(String details) {
        getInstance().set(KEY_SUB_PRODUCT_DETAILS, details);
    }

    public static long getProDetailLastCacheTime() {
        return getInstance().getLong(KEY_SUB_PRODUCT_DETAILS_LAST_CACHE_TIME, -1);
    }

    public static void setProDetailLastCacheTime(long millsTime) {
        getInstance().setLong(KEY_SUB_PRODUCT_DETAILS_LAST_CACHE_TIME, millsTime);
    }

    public static String getProductDetailsPrice() {
        return getInstance().get(KEY_SUB_PRODUCT_DETAILS_PRICE, "");
    }

    public static void setProductDetailsPrice(String price) {
        getInstance().set(KEY_SUB_PRODUCT_DETAILS_PRICE, price);
    }
}
