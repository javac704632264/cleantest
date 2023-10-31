package com.ai.subscription.component;

import java.util.Arrays;
import java.util.List;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/19
 * Time   :   5:55 下午
 */
public class SubPortal {

    //注意::  修改以下字段名， DEFAULT_VALUE 中的云控值 也要同步修改
    public static final String PORTAL_MAIN = "home_page_top_right";

    public static final String PORTAL_AD_SPLASH = "ad_splash";
    public static final String PORTAL_AD_HOME_BANNER = "home_page_ad_banner";
    public static final String PORTAL_AD_HOME_DIALOG = "home_page_ad_dialog";
    public static final String PORTAL_AD_FEEDBACK = "ad_feedback";
    public static final String PORTAL_AD_TRANS_TOPAPP = "trans_topapp";

    public static final String PORTAL_CLEAN_REGULAR = "clean_regular";
    public static final String PORTAL_ANTIVIRUS_REGULAR = "antivirus_regular";
    public static final String PORTAL_CUSTOMER_SERVICE = "customer_service";

    public static final String PORTAL_ME_SUB = "me_sub";

    public static final String PORTAL_QUIT_INTERCEPT = "quit_intercept";

    public static List<String> ALL_PORTALS = Arrays.asList(
            SubPortal.PORTAL_MAIN,
            SubPortal.PORTAL_AD_SPLASH,
            PORTAL_AD_HOME_BANNER,
            PORTAL_AD_HOME_DIALOG,
            PORTAL_CLEAN_REGULAR,
            PORTAL_AD_FEEDBACK,
            PORTAL_AD_TRANS_TOPAPP,
            PORTAL_ANTIVIRUS_REGULAR,
            PORTAL_CUSTOMER_SERVICE,
            PORTAL_QUIT_INTERCEPT
    );

    public static String DEFAULT_VALUE = "{\"open_iap\":false}";
}
