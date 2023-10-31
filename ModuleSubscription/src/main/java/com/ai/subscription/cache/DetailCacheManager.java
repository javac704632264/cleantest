package com.ai.subscription.cache;

import android.text.TextUtils;

import com.ai.subscription.manager.SubManager;
import com.android.billingclient.api.ProductDetails;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.util.SubscribeSettings;
import com.android.billingclient.api.SkuDetails;
import com.doodlecamera.base.core.log.Logger;
import com.doodlecamera.tools.core.utils.GsonUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/9/20
 * Time   :   3:38 下午
 */
public class DetailCacheManager {

    private static DetailCacheManager instance;

    private Map<String, ProductDetails> cacheDetailsMap = new HashMap<>();

    private Map<String, SkuDetails> cacheSkuDetailsMap = new HashMap<>();

    private DetailCacheManager() {
    }

    public static DetailCacheManager getInstance() {
        if (instance == null) {
            synchronized (DetailCacheManager.class) {
                if (instance == null)
                    instance = new DetailCacheManager();
            }
        }

        return instance;
    }

    public void saveDetailData2Local(List<ProductDetails> list) {
        String value = GsonUtils.models2Json(list);

        SubscribeSettings.setProductDetails(value);
        SubscribeSettings.setProDetailLastCacheTime(System.currentTimeMillis());

        PurchaseManager.log(" saveDetailData2Local() = " + value);
    }

    public void saveSkuDetailData2Local(List<SkuDetails> list){
        String value = GsonUtils.models2Json(list);

        SubscribeSettings.setProductDetails(value);
        SubscribeSettings.setProDetailLastCacheTime(System.currentTimeMillis());

        PurchaseManager.log(" saveSkuDetailData2Local() = " + value);
    }

    private List<ProductDetails> getDetailDataFromLocal() {
        String value = SubscribeSettings.getProductDetails();

        List<ProductDetails> cacheDetailList = GsonUtils.createModels(value, ProductDetails.class);

        PurchaseManager.log(" getDetailDataFromLocal() = " + cacheDetailList.toString());
        return cacheDetailList;
    }

    private List<SkuDetails> getSkuDetailDataFromLocal(){
        String value = SubscribeSettings.getProductDetails();

        List<SkuDetails> cacheDetailList = GsonUtils.createModels(value, SkuDetails.class);

        PurchaseManager.log(" getDetailDataFromLocal() = " + cacheDetailList.toString());
        return cacheDetailList;
    }

    public Map<String, ProductDetails> getProductDetailsMap() {
        if (cacheDetailsMap.size() == 0) {

            List<ProductDetails> list = getDetailDataFromLocal();

            for (ProductDetails productDetails : list) {

                cacheDetailsMap.put(productDetails.getProductId(), productDetails);
            }
        }

        PurchaseManager.log(" getProductDetailsMap() = " + cacheDetailsMap.toString());

        return cacheDetailsMap;
    }

    public Map<String,SkuDetails> getSkuDetailsMap(){
        if (cacheSkuDetailsMap.size() == 0){
            List<SkuDetails> list = getSkuDetailDataFromLocal();

            for (SkuDetails productDetails : list) {

                cacheSkuDetailsMap.put(productDetails.getSku(), productDetails);
            }
        }
        PurchaseManager.log(" getProductDetailsMap() = " + cacheSkuDetailsMap.toString());

        return cacheSkuDetailsMap;
    }

    public void removeProductDetailCache() {
        long detailExpiredDays = ConfigStyle.getProductDetailExpiredDays();

        PurchaseManager.log(" removeProductDetailCache() detailExpiredDays = " + detailExpiredDays);

        long lastCacheTime = SubscribeSettings.getProDetailLastCacheTime();
        long current = System.currentTimeMillis();

        if (lastCacheTime == -1)
            return;

        if (detailExpiredDays == 0 || (Math.abs((current - lastCacheTime)) >= detailExpiredDays * 24 * 60 * 60 * 1000)) {
            SubscribeSettings.setProductDetails("");
            SubscribeSettings.setProDetailLastCacheTime(-1);
            SubscribeSettings.setProductDetailsPrice("");

            PurchaseManager.log(" removeProductDetailCache() success");
        }
    }

    // cache price start ======

    public static final String PRICE_PERIOD = "_price_period";
    public static final String PRICE = "_price";
    public static final String PRICE_MODE = "_price_mode";

    public void savePricePeriod(String productId, String value) {
        try {
            String pricePeriod = SubscribeSettings.getProductDetailsPrice();
            JSONObject json;

            if (TextUtils.isEmpty(pricePeriod))
                json = new JSONObject();
            else
                json = new JSONObject(pricePeriod);

            json.put(productId + DetailCacheManager.PRICE_PERIOD, value);

            PurchaseManager.log("savePricePeriod()  productId = " + productId + "  pricePeriod = " + value);

            SubscribeSettings.setProductDetailsPrice(json.toString());
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
    }

    public void savePrice(String productId, String value) {
        try {
            String pricePeriod = SubscribeSettings.getProductDetailsPrice();
            JSONObject json;

            if (TextUtils.isEmpty(pricePeriod))
                json = new JSONObject();
            else
                json = new JSONObject(pricePeriod);

            json.put(productId + DetailCacheManager.PRICE, value);

            PurchaseManager.log("savePrice()  productId = " + productId + "  price = " + value);

            SubscribeSettings.setProductDetailsPrice(json.toString());

        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
    }

    public void savePriceMode(String productId, String value) {
        try {
            String pricePeriod = SubscribeSettings.getProductDetailsPrice();

            JSONObject json;

            if (TextUtils.isEmpty(pricePeriod))
                json = new JSONObject();
            else
                json = new JSONObject(pricePeriod);

            json.put(productId + DetailCacheManager.PRICE_MODE, value);

            PurchaseManager.log("savePriceMode()  productId = " + productId + "  mode = " + value);

            SubscribeSettings.setProductDetailsPrice(json.toString());

        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
    }

    public String getPricePeriod(String productId) {
        try {
            String pricePeriod = SubscribeSettings.getProductDetailsPrice();
            if (TextUtils.isEmpty(pricePeriod))
                return "";
            JSONObject json = new JSONObject(pricePeriod);
            return json.optString(productId + DetailCacheManager.PRICE_PERIOD, "");
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
        return "";
    }

    public String getPrice(String productId) {
        try {
//            String pricePeriod = SubscribeSettings.getProductDetailsPrice();
//            if (TextUtils.isEmpty(pricePeriod))
//                return "";
//            JSONObject json = new JSONObject(pricePeriod);
            return SubManager.Companion.getInstance().getPriceDiscountMap().get(productId);
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
        return "";
    }

    public String getPriceMode(String productId) {
        try {
            String pricePeriod = SubscribeSettings.getProductDetailsPrice();
            if (TextUtils.isEmpty(pricePeriod))
                return "";
            JSONObject json = new JSONObject(pricePeriod);
            return json.optString(productId + DetailCacheManager.PRICE_MODE, "");
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
        return "";
    }

    // cache price end ======
}
