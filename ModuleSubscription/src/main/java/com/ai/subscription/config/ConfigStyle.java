package com.ai.subscription.config;

import static com.ai.subscription.config.BasicsKeys.KEY_PRODUCT_ID_CONFIG;

import android.text.TextUtils;

import com.ai.subscription.component.SubPortal;
import com.ai.subscription.purchase.Client;
import com.doodlecamera.base.core.ccf.CloudConfig;
import com.doodlecamera.base.core.utils.lang.ObjectStore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/13
 * Time   :   3:48 下午
 */

public class ConfigStyle {

    public static final String SINGLE = "single";
    public static final String MULTI_BTN = "multi_btn";
    public static final String MULTI_NO_BTN = "multi_no_btn";

    public static ConfigBean getProductConfig(String portal) {
        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);

        try {
            JSONObject json = new JSONObject(config);
            Iterator iterator = json.keys();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();

                if (portal.equals(key)) {

                    ConfigBean configBean = new ConfigBean();

                    JSONObject productJsonObj = Objects.requireNonNull(json.optJSONObject(portal)).optJSONObject("sub_id");

                    Iterator<String> sIterator = productJsonObj.keys();
                    while (sIterator.hasNext()) {
                        String productid = sIterator.next();

                        JSONObject subvalue = productJsonObj.getJSONObject(productid);

                        ConfigBean.ProductIdConfig product = new ConfigBean.ProductIdConfig();
                        product.setProductId(productid);
                        product.setDiscount(subvalue.optString("discount", ""));
                        product.setTrial_day(subvalue.optInt("trial_day", 0));
                        product.setDefault(subvalue.optBoolean("isDefault", false));
                        product.setPromotionInformation(subvalue.optString("promotion_information", ""));
                        configBean.addProduct(product);
                    }

                    return configBean;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getPromotionInformation(String portal, String productId) {
        ConfigBean bean = getProductConfig(portal);
        if (bean != null && bean.mProductConfigList != null) {
            for (ConfigBean.ProductIdConfig productIdConfig : bean.mProductConfigList) {
                if (productId.equals(productIdConfig.productId)) {
                    return productIdConfig.getPromotionInformation();
                }
            }
        }

        return "";
    }

    public static String getDiscount(String portal, String productId) {
        ConfigBean bean = getProductConfig(portal);
        if (bean != null && bean.mProductConfigList != null) {
            for (ConfigBean.ProductIdConfig productIdConfig : bean.mProductConfigList) {
                if (productId.equals(productIdConfig.productId)) {
                    return productIdConfig.discount;
                }
            }
        }

        return "";
    }

    public static int getTrialDay(String portal, String productId) {
        ConfigBean bean = getProductConfig(portal);
        if (bean != null && bean.mProductConfigList != null) {

            for (ConfigBean.ProductIdConfig productIdConfig : bean.mProductConfigList) {
                if (productId.equals(productIdConfig.productId)) {
                    return productIdConfig.trial_day;
                }
            }
        }

        return 0;
    }

    public static String getDefaultProductID(String portal) {
        ConfigBean bean = getProductConfig(portal);
        if (bean != null && bean.mProductConfigList != null) {

            for (ConfigBean.ProductIdConfig productIdConfig : bean.mProductConfigList) {
                if (productIdConfig.isDefault)
                    return productIdConfig.productId;
            }
        }

        return Client.PRODUCT_ID_YEAR;
    }

    public static List<String> getTop2ProductID(String portal) {
        ArrayList<String> productSet = new ArrayList<>();
        ConfigBean bean = getProductConfig(portal);
        if (bean != null && bean.mProductConfigList != null) {

            for (ConfigBean.ProductIdConfig productIdConfig : bean.mProductConfigList) {
                if (productSet.size() >= 3) {
                    break;
                }

                productSet.add(productIdConfig.productId);
            }
        }

        return productSet;
    }

    public static String getFirstProductID(String portal) {

        ConfigBean bean = getProductConfig(portal);
        if (bean != null && bean.mProductConfigList != null && bean.mProductConfigList.size() >= 1) {
            return bean.mProductConfigList.get(0).productId;
        }

        return Client.PRODUCT_ID_YEAR;
    }

    public static Set<String> getAllProductID() {

        HashSet<String> productSet = new HashSet<>();

        for (String portal : SubPortal.ALL_PORTALS) {
            ConfigBean bean = getProductConfig(portal);

            if (bean == null)
                continue;

            for (ConfigBean.ProductIdConfig config : bean.mProductConfigList) {
                if (!TextUtils.isEmpty(config.productId))
                    productSet.add(config.productId);
            }
        }

        if (productSet.isEmpty()) {
            productSet.add(Client.PRODUCT_ID_WEEK);
            productSet.add(Client.PRODUCT_ID_MONTH);
            productSet.add(Client.PRODUCT_ID_YEAR);
            productSet.add(Client.PRODUCT_ID_ONE);
        }

        return productSet;
    }

    public static boolean openIAP() {
//        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);
//        Logger.d("TAG", "sssssssss openIAP: " + config);
        try {
            return CloudConfigUtils.INSTANCE.isSubOpen();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean openEnterVIPPage() {
//        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);
//        Logger.d("TAG", "sssssssss openIAP: " + config);
        try {
            return CloudConfigUtils.INSTANCE.isEnterSubPage();//new JSONObject(config).optBoolean("enter_vip", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 重连延迟多少ms 重试
    public static int getReconnectDelayMill() {
        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);

        try {
            return new JSONObject(config).optInt("iap_reconnect_delay", 30 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 30 * 1000;
    }

    // 重连最大次数 重试
    public static int getReconnectMaxCount() {
//        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);
        try {
            return CloudConfigUtils.INSTANCE.getConnectCount();//new JSONObject(config).optInt("iap_reconnect_max_count", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 2;
    }

    // 从GP 查询没有 已订阅 商品后，最大允许使用的session次数
    public static int getExtraUseMaxCount() {
        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);

        try {
            return new JSONObject(config).optInt("iap_extra_use_max_count", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 2;
    }

    public static String getSubPrivacyUrl() {
        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);

        try {
            return new JSONObject(config).optString("sub_privacy_terms", BasicsKeys.PRIVACY_TERMS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BasicsKeys.PRIVACY_TERMS;
    }

    public static int getProductDetailExpiredDays() {
        String config = CloudConfig.getStringConfig(ObjectStore.getContext(), KEY_PRODUCT_ID_CONFIG, SubPortal.DEFAULT_VALUE);

        try {
            return new JSONObject(config).optInt("detail_expire_day", 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 30;
    }
}
