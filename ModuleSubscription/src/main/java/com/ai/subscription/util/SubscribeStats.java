package com.ai.subscription.util;

import android.text.TextUtils;

import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.purchase.PurchaseManager;
import com.doodlecamera.base.core.stats.Stats;
import com.doodlecamera.base.core.utils.lang.ObjectStore;

import java.util.HashMap;
import java.util.Set;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/13
 * Time   :   3:15 下午
 */
public class SubscribeStats {

    // 整体订阅事件事件名就用两个，sub_click和sub_show，用pve_cur来区分位置，然后用参数来区分状态。
    // 点击订阅按钮先报一个点击事件，连接成功之后和有支付结果之后再报一次这个事件和pve，但需要增加一个“结果”参数来区分状态
    public static final String EVENT_NAME_SHOW = "sub_show";
    public static final String EVENT_NAME_CLICK = "sub_click";
    public static final String EVENT_NAME_CONNECT = "connect_google";


    //参数
    public static final String KEY_SUB_STATE = "is_sub";
    public static final String VALUE_SUB_STATE_SUCCESS = "true";
    public static final String VALUE_SUB_STATE_NONE = "false";

    public static final String KEY_REQUEST_SUCCESS = "is_load_success";
    public static final String VALUE_REQUEST_SUCCESS = "true";
    public static final String VALUE_REQUEST_FAIL = "false";

    public static final String KEY_SUB_PRODUCTID = "sub_id";
    public static final String KEY_SUB_PRODUCTID_DETAIL = "sub_product_detail";
    public static final String KEY_SUB_ORDER_ID = "sub_order_id";
    public static final String KEY_SUB_TIMESTAMP = "sub_order_timestamp";
    public static final String KEY_SUB_PURCHASE_JSON = "purchase_json";
    public static final String KEY_SUB_IN_FAIL_RETRY_DIALOG = "is_fail_dialog";

    public static final String KEY_SUB_RESULT = "result";
    public static final String VALUE_SUB_PAY_SUCCESS = "google_pay_suc";
    public static final String VALUE_SUB_PAY_FAIL = "google_pay_fail";
    public static final String VALUE_SUB_CONN_SUCCESS = "connect/suc";
    public static final String VALUE_SUB_CONN_FAIL = "connect/fail";

    public static final String VALUE_SUB_CONN_CONNECT = "connect";
    public static final String VALUE_SUB_CLICK = "click";

    public static final String KEY_QUIT_DIALOG = "is_quit_intercept";

    public static final String KEY_CONN_CODE = "code";
    public static final String KEY_CONN_MSG = "message";
    public static final String KEY_PAY_FAIL_MSG = "message";
    public static final String KEY_PAY_FAIL_ERROR_CODE = "error_code";

    private static boolean statsConnect = true;

    private static boolean statsConnectSuccessful = true;

    public static void showStats(String mPortal, String productIDS) {
        try {
            if (TextUtils.isEmpty(mPortal))
                return;

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
            params.put("portal", mPortal);
            params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_NONE);

            PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
            boolean reqSucc = manager != null && manager.requestProductDetailSucc();

            params.put(SubscribeStats.KEY_REQUEST_SUCCESS, reqSucc ? SubscribeStats.VALUE_REQUEST_SUCCESS : SubscribeStats.VALUE_REQUEST_FAIL);

            if (productIDS.endsWith(",")) {
                productIDS = productIDS.substring(0, productIDS.length() - 1);
            }

            params.put(SubscribeStats.KEY_SUB_PRODUCTID, productIDS);

            params.put("pve_cur", "/sub_guide");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 退出拦截 弹窗展示
    public static void showStatsForQuitIntercept(String portal, String mProductId) {
        try {
            if (TextUtils.isEmpty(portal))
                return;

            HashMap<String, String> params = new HashMap<>();//StatsUtils.generateCommonParams();

            params.put("portal", portal);
            params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_NONE);

            params.put(SubscribeStats.KEY_SUB_PRODUCTID, mProductId);

            params.put("pve_cur", "/sub_guide/close_btn/quit_intercept");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 点击关闭页面
    public static void statsBackClick(String portal, String productIDS) {
        try {
            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/close_btn");
            params.put("portal", portal);
            params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_NONE);

            if (productIDS.endsWith(","))
                productIDS = productIDS.substring(0, productIDS.length() - 1);
            params.put(SubscribeStats.KEY_SUB_PRODUCTID, productIDS);

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 点击订阅按钮（跳转支付）
    public static void clickStats(String portal, String productIDS, boolean quit_dialog, boolean inFailRetryDilalog) {
        try {
            if (TextUtils.isEmpty(portal))
                return;

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
            params.put("pve_cur", "/sub_guide/plan/sub_btn");

            params.put("portal", portal);
            params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_NONE);

            PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
            boolean reqSucc = manager != null && manager.requestProductDetailSucc();

            params.put(SubscribeStats.KEY_REQUEST_SUCCESS, reqSucc ? SubscribeStats.VALUE_REQUEST_SUCCESS : SubscribeStats.VALUE_REQUEST_FAIL);

            params.put(SubscribeStats.KEY_QUIT_DIALOG, quit_dialog + "");

            if (productIDS.endsWith(","))
                productIDS = productIDS.substring(0, productIDS.length() - 1);

            params.put(SubscribeStats.KEY_SUB_PRODUCTID, productIDS);
            params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_CLICK);

            params.put(SubscribeStats.KEY_SUB_IN_FAIL_RETRY_DIALOG, inFailRetryDilalog + "");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void statsConnectStart() {
        try {
            if (statsConnect) {
                HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
                params.put("pve_cur", "/connect_google/connect");

                params.put("portal", "app_start");

                params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);

                params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_CONN_CONNECT);

                Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CONNECT, params);
                statsConnect = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 连接谷歌 成功
    public static void statsConnectSucc() {
        try {
            if (statsConnectSuccessful) {
                HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
                params.put("pve_cur", "/connect_google/connect");

                params.put("portal", "app_start");

                params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);

                params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_CONN_SUCCESS);

                Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CONNECT, params);
                statsConnectSuccessful = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 连接谷歌 失败
    public static void statsConnectFail(int code, String message) {
//        try {
//            HashMap<String, String> params = StatsUtils.INSTANCE.generateCommonParams();
//            params.put("pve_cur", "/connect_google/connect");
//
//            params.put("portal", "app_start");
//
//            params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);
//
//            params.put(SubscribeStats.KEY_QUIT_DIALOG, false + "");
//            params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_CONN_FAIL);
//
//            params.put(SubscribeStats.KEY_CONN_CODE, code + "");
//            params.put(SubscribeStats.KEY_CONN_MSG, message);
//
//            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CONNECT, params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // 获取商品详情成功
    public static void statsGetProductDetailSuccess(Set<String> productids, String productDetail) {
        try {
            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
            params.put("pve_cur", "/connect_google/sub_id_info");

            params.put("portal", "app_start");

            params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);

            params.put(SubscribeStats.KEY_QUIT_DIALOG, false + "");
            params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_CONN_SUCCESS);

            StringBuilder builder = new StringBuilder();

            for (String id : productids) {
                builder.append(id);
                builder.append(",");
            }

            String value = builder.toString();
            if (value.endsWith(","))
                value = value.substring(0, value.length() - 1);

            params.put(SubscribeStats.KEY_SUB_PRODUCTID, value);

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CONNECT, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取商品详情 失败, 点击购买按钮时 会上报，由于获取失败 gp 没有接口回调给我
    public static void statsGetProductDetailFail() {
        try {
            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
            params.put("pve_cur", "/connect_google/sub_id_info");
            params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);
            params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_CONN_FAIL);

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CONNECT, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 支付成功
    public static void statsPaySucc(String portal, boolean quit_dialog, String payProductid, String orderId, String originJson, boolean inFailRetryDilalog) {
        try {
            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
            params.put("pve_cur", "/sub_guide/plan/sub_btn");

            params.put("portal", portal);
            params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_SUCCESS);

            params.put(SubscribeStats.KEY_QUIT_DIALOG, quit_dialog + "");
            if (payProductid.endsWith(","))
                payProductid = payProductid.substring(0, payProductid.length() - 1);
            params.put(SubscribeStats.KEY_SUB_PRODUCTID, payProductid);
            params.put(SubscribeStats.KEY_SUB_ORDER_ID, orderId);
            params.put(SubscribeStats.KEY_SUB_TIMESTAMP, System.currentTimeMillis() + "");
            params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_PAY_SUCCESS);
            params.put(SubscribeStats.KEY_SUB_PURCHASE_JSON, originJson);
            params.put(SubscribeStats.KEY_SUB_IN_FAIL_RETRY_DIALOG, inFailRetryDilalog + "");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 支付失败
    public static void statsPayFail(String portal, boolean quit_dialog, String payProductid, String failMsg, int errorCode, boolean inFailRetryDilalog) {
        try {
            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();
            params.put("pve_cur", "/sub_guide/plan/sub_btn");

            params.put("portal", portal);
            params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_NONE);

            params.put(SubscribeStats.KEY_QUIT_DIALOG, quit_dialog + "");

            if (payProductid.endsWith(","))
                payProductid = payProductid.substring(0, payProductid.length() - 1);

            params.put(SubscribeStats.KEY_SUB_PRODUCTID, payProductid);
            params.put(SubscribeStats.KEY_PAY_FAIL_MSG, failMsg);
            params.put(SubscribeStats.KEY_PAY_FAIL_ERROR_CODE, errorCode + "");
            params.put(SubscribeStats.KEY_SUB_RESULT, VALUE_SUB_PAY_FAIL);
            params.put(SubscribeStats.KEY_SUB_IN_FAIL_RETRY_DIALOG, inFailRetryDilalog + "");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 重试支付 弹窗展示
    public static void showStatsForRetry(String portal, String style, String mProductId) {
        try {
            if (TextUtils.isEmpty(portal))
                return;

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("portal", portal);
            //params.put(SubscribeStats.KEY_SUB_STATE, SubscribeStats.VALUE_SUB_STATE_NONE);

            params.put(SubscribeStats.KEY_SUB_PRODUCTID, mProductId);

            params.put("pve_cur", "/sub_guide/pay_fail_dialog");

            PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
            boolean reqSucc = manager != null && manager.requestProductDetailSucc();
            params.put(SubscribeStats.KEY_REQUEST_SUCCESS, reqSucc ? SubscribeStats.VALUE_REQUEST_SUCCESS : SubscribeStats.VALUE_REQUEST_FAIL);

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showStatsSupport() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);

            params.put("pve_cur", "/sub_guide/support");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clickStatsSupport() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put(SubscribeStats.KEY_SUB_STATE, IAPManager.getInstance().isVIP() ? SubscribeStats.VALUE_SUB_STATE_SUCCESS : SubscribeStats.VALUE_SUB_STATE_NONE);

            params.put("pve_cur", "/sub_guide/support");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 支付时 无网提示 弹窗展示
    public static void showStatsPayNoNet() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/plan/sub_btn/no_network_popup");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 点击支付时 无网提示 弹窗点击
    public static void clickStatsPayNoNet(boolean connect) {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/plan/sub_btn/no_network_popup");
            params.put("result", connect ? "connect" : "retry");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 顶部 无网提示 红色横条 展示
    public static void showStatsBannerNoNet() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/no_network_alert");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 顶部 无网提示 红色横条 点击
    public static void clickStatsBannerNoNet() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/no_network_alert");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 顶部 重试 红色横条 展示
    public static void showStatsBannerRetry() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/load_fail_alert");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 顶部 重试 红色横条 点击
    public static void clickStatsBannerRetry() {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide/load_fail_alert");

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_CLICK, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 进入页面开始load及load结果
    public static void showStatsEnterLoading(String state) {
        try {

            HashMap<String, String> params = new HashMap<>();//StatsUtils.INSTANCE.generateCommonParams();

            params.put("pve_cur", "/sub_guide");
            params.put(KEY_REQUEST_SUCCESS, state);  // loading 系统自动重试不重复上报，用户主动重试时再次上报   /true/false

            Stats.onEvent(ObjectStore.getContext(), EVENT_NAME_SHOW, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
