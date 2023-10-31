package com.ai.subscription.purchase;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.ai.subscription.cache.DetailCacheManager;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.util.SubscribeStats;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.doodlecamera.base.core.log.Logger;
import com.doodlecamera.theme.lib.loader.SkinManager;
import com.doodlecamera.tools.core.utils.ui.SoftKeyboardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PurchaseManager {
    public static final String TAG = "PurchaseManager";

    private final Map<String, ProductDetails> productDetailsMap = new HashMap<>();

    private final Map<String, SkuDetails> skuDetailsMap = new HashMap<>();

    private Client client;

    private PurchaseManager() {
    }

    private void init(final Builder builder) {
        // 此构造方法内，我们自己有 发起 链接 GP 操作
        client = new Client(builder.context, new ConnectedCallback() {

            @Override
            public void onGpConnectSuccess() {

                try {
                    // 链接GP 成功后 ，异步查询 product 详情
                    innerUpdateProductDetails(builder.subsProductList);

                    if (builder.connectedCallback != null) {
                        builder.connectedCallback.onGpConnectSuccess();
                    }

                } catch (Exception e) {
                    Logger.e(PurchaseManager.TAG, e);
                }
            }

            @Override
            public void onGpConnectFail() {
                if (builder.connectedCallback != null) {
                    builder.connectedCallback.onGpConnectFail();
                }
            }
        });
    }

    public void innerUpdateProductDetails(List<String> subsProductList) {
        log("innerUpdateProductDetails() ");

        ProductDetailsResponseListener listener = (billingResult, list) ->
                log("innerUpdateProductDetails()  onProductDetailsResponse()   code = " + billingResult.getResponseCode() + "  debug_msg = " + billingResult.getDebugMessage() + "  list_size  = " );

        SkuDetailsResponseListener listener1 = (billingResult, list) ->
                log("innerUpdateSkuProductDetails()  onProductDetailsResponse()   code = " + billingResult.getResponseCode() + "  debug_msg = " + billingResult.getDebugMessage() + "  list_size  = " );

        if (subsProductList != null && subsProductList.size() > 0) {
            queryProductDetails(BillingClient.ProductType.SUBS, subsProductList, listener,listener1);
        }
    }

    public void innerInAppUpdateProductDetails(List<String> subsProductList){
        log("innerInAppUpdateProductDetails() ");

        ProductDetailsResponseListener listener = (billingResult, list) ->
                log("innerInAppUpdateProductDetails()  onProductDetailsResponse()   code = " + billingResult.getResponseCode() + "  debug_msg = " + billingResult.getDebugMessage() + "  list_size  = " + list.size());

        SkuDetailsResponseListener listener1 = (billingResult, list) ->
                log("innerInAppUpdateProductDetails()  onProductDetailsResponse()   code = " + billingResult.getResponseCode() + "  debug_msg = " + billingResult.getDebugMessage() + "  list_size  = " + list.size());

        if (subsProductList != null && subsProductList.size() > 0) {
            queryInAppDetail(subsProductList, listener,listener1);
        }
    }

    public boolean icConnectionSuccess() {
        return client.isConnectionSuccess();
    }

    public int getConnectionCode() {
        return client.getBillingConnectionCode();
    }

    public boolean requestProductDetailSucc() {
        return icConnectionSuccess() && (productDetailsMap.size() >= 1 || skuDetailsMap.size() >= 1);
    }

    ArrayList<Purchase> purchases = new ArrayList<>();
    /**
     * return null  query fail
     * return List  size==0   not have buy goods
     */
    @Nullable
    public List<Purchase> queryAllPurchases(Client.PurchasesResultListener resultListener) {
        if (!client.isConnectionSuccess()) {
            client.retryConnection(1000);
            return null;
        }

        client.queryPurchases(resultListener);

        purchases = client.getGPPurchases();

        log("queryAllPurchases() success purchase size:" + purchases.size());

        return purchases;
    }

    public void reConnect() {
        if (client != null)
            client.retryConnection(1000);
    }

    public ArrayList<Purchase> getPurchases() {
        return purchases;
    }

    public void buy(BuyParams buyParams) {
        log("buy()  buyParams = " + buyParams.toString());

        String pid = buyParams.productId;

        ProductDetails productDetails = productDetailsMap.get(pid);
        SkuDetails skuDetails = skuDetailsMap.get(pid);

        if (productDetails == null && skuDetails == null) {
            List<String> list = IAPManager.getInstance().getSubscribeProductList();
            if (!list.contains(pid))
                list.add(pid);

            innerUpdateProductDetails(list);

            SubscribeStats.statsGetProductDetailFail();
            if (buyParams.buyCallback != null) {
                buyParams.buyCallback.onBuyFail(buyParams.getProductId(), -1000000, "productDetails is null");
            }
            return;
        }

        if (productDetails != null) {
            log("buy()  productDetails = " + productDetails.toString());

            String offerToken = "";
            try {
                offerToken = productDetails.getSubscriptionOfferDetails().get(0).getOfferToken();

            } catch (Exception e) {
                e.printStackTrace();
            }

            log("buy()  offerToken = " + offerToken);

            if (TextUtils.isEmpty(offerToken)) {
                BillingFlowParams.ProductDetailsParams flowDetailList = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
//                        .setOfferToken(offerToken)  // offerToken 为空的话，billing库会直接抛NPE
                        .build();

                BillingFlowParams params = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(Collections.singletonList(flowDetailList))
                        .build();

                client.buy(buyParams, params);
                return;
            }

            BillingFlowParams.ProductDetailsParams flowDetailList = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)  // offerToken 为空的话，billing库会直接抛NPE
                    .build();

            BillingFlowParams params = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(Collections.singletonList(flowDetailList))
                    .build();

            client.buy(buyParams, params);
        }else if (skuDetails != null){
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            client.buy(buyParams, billingFlowParams);
        }


    }

    public void consumeAsync(String purchaseToken, ConsumeResponseListener listener) {
        client.consumeAsync(purchaseToken, listener);
    }

    public void saveData(Purchase purchase) {
        client.saveData(purchase);
    }

    public void queryProductDetails(@BillingClient.ProductType String skuType, List<String> productList, final ProductDetailsResponseListener skuDetailsResponseListener,SkuDetailsResponseListener skuResponseListener) {
        log("querySkuDetails()  skuType = " + skuType + " sku_list_size = " + productList.size());

        ArrayList<ProductDetails> skuDetails = new ArrayList<>();
        Set<Map.Entry<String, ProductDetails>> entries = productDetailsMap.entrySet();

        for (Map.Entry<String, ProductDetails> entry : entries) {
            for (String s : productList) {
                if (entry.getKey().equals(s)) {
                    skuDetails.add(entry.getValue());
                }
            }
        }

        ArrayList<SkuDetails> skuDetails1 = new ArrayList<>();
        Set<Map.Entry<String, SkuDetails>> entries1 = skuDetailsMap.entrySet();

        for (Map.Entry<String, SkuDetails> entry : entries1) {
            for (String s : productList) {
                if (entry.getKey().equals(s)) {
                    skuDetails1.add(entry.getValue());
                }
            }
        }

        if (skuDetails.size() == productList.size() && skuDetails1.size() == productList.size()) {
            log("queryProductDetails()  use memory cache");
            BillingResult billingResult = BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build();
            if (skuDetailsResponseListener != null) {
                skuDetailsResponseListener.onProductDetailsResponse(billingResult, skuDetails);
            }
            return;
        }

        List<String> productIDSet = new ArrayList<>(ConfigStyle.getAllProductID());
        List<QueryProductDetailsParams.Product> queryProductIdList = new ArrayList<>();

        for (String id : productIDSet) {
            log("queryProductIdList  id....."+id);
            QueryProductDetailsParams.Product query = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build();
            queryProductIdList.add(query);
        }

        if (queryProductIdList.size() == 0) {
            log("queryProductIdList  empty.....");
            return;
        }

        QueryProductDetailsParams queryDetailParams = QueryProductDetailsParams.newBuilder()
                .setProductList(queryProductIdList)
                .build();

        client.queryProductDetailsAsync(queryDetailParams, (result, list) -> {
            log("querySkuDetailsAsync() onProductDetailsResponse()  resultcode = " + result.getResponseCode()+" size = "+ list.size());
            if (result.getResponseCode() == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED){
                querySkuDetail(skuResponseListener);
                return;
            }

            if (skuDetailsResponseListener != null) {
                skuDetailsResponseListener.onProductDetailsResponse(result, list);
            }

            if (list.size() > 0)
                DetailCacheManager.getInstance().saveDetailData2Local(list);

            for (ProductDetails productDetails : list) {
                log("querySkuDetailsAsync() onProductDetailsResponse()  productDetails = " + productDetails.toString());
                productDetailsMap.put(productDetails.getProductId(), productDetails);
            }

            SubscribeStats.statsGetProductDetailSuccess(productDetailsMap.keySet(), list.toString());
            if (result.getResponseCode() != BillingClient.BillingResponseCode.OK || list.size() == 0) {
                retryProductDetail();
            }
        });
    }

    private void querySkuDetail(SkuDetailsResponseListener skuResponseListener){
        List<String> productIDSet = new ArrayList<>(ConfigStyle.getAllProductID());
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        SkuDetailsParams params1 = params.setSkusList(productIDSet).setType(BillingClient.SkuType.SUBS).build();
        client.querySkuDetailsAsync(params1, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                log("querySkuDetail() onSkuDetailsResponse()  resultcode = " + billingResult.getResponseCode());
                skuResponseListener.onSkuDetailsResponse(billingResult,list);
                if (list.size() > 0)
                    DetailCacheManager.getInstance().saveSkuDetailData2Local(list);

                for (SkuDetails productDetails : list) {
                    log("querySkuDetailsAsync() onProductDetailsResponse()  productDetails = " + productDetails.toString());
                    skuDetailsMap.put(productDetails.getSku(), productDetails);
                }

                SubscribeStats.statsGetProductDetailSuccess(skuDetailsMap.keySet(), list.toString());
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || list.size() == 0) {
                    retryProductDetail();
                }
            }
        });
    }

    public void queryInAppDetail(List<String> productList, final ProductDetailsResponseListener skuDetailsResponseListener,SkuDetailsResponseListener skuResponseListener){
        ArrayList<ProductDetails> skuDetails = new ArrayList<>();
        Set<Map.Entry<String, ProductDetails>> entries = productDetailsMap.entrySet();

        for (Map.Entry<String, ProductDetails> entry : entries) {
            for (String s : productList) {
                if (entry.getKey().equals(s)) {
                    skuDetails.add(entry.getValue());
                }
            }
        }

        ArrayList<SkuDetails> skuDetails1 = new ArrayList<>();
        Set<Map.Entry<String, SkuDetails>> entries1 = skuDetailsMap.entrySet();

        for (Map.Entry<String, SkuDetails> entry : entries1) {
            for (String s : productList) {
                if (entry.getKey().equals(s)) {
                    skuDetails1.add(entry.getValue());
                }
            }
        }

        if (skuDetails.size() == productList.size() && skuDetails1.size() == productList.size()) {
            log("queryInAppDetail()  use memory cache");
            BillingResult billingResult = BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build();
            if (skuDetailsResponseListener != null) {
                skuDetailsResponseListener.onProductDetailsResponse(billingResult, skuDetails);
            }
            return;
        }

        List<String> productIDSet = new ArrayList<>();
        productIDSet.add(Client.PRODUCT_ID_ONE);
        List<QueryProductDetailsParams.Product> queryProductIdList = new ArrayList<>();

        for (String id : productIDSet) {
            log("queryProductIdList  id....."+id);
            QueryProductDetailsParams.Product query = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build();
            queryProductIdList.add(query);
        }

        if (queryProductIdList.size() == 0) {
            log("queryProductIdList  empty.....");
            return;
        }

        QueryProductDetailsParams queryDetailParams = QueryProductDetailsParams.newBuilder()
                .setProductList(queryProductIdList)
                .build();

        client.queryProductDetailsAsync(queryDetailParams, (result, list) -> {
            log("queryInAppDetail() onProductDetailsResponse()  resultcode = " + result.getResponseCode());
            if (result.getResponseCode() == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED){
                querySkuInAppDetail(skuResponseListener);
                return;
            }

            if (skuDetailsResponseListener != null) {
                skuDetailsResponseListener.onProductDetailsResponse(result, list);
            }

            if (list.size() > 0)
                DetailCacheManager.getInstance().saveDetailData2Local(list);

            for (ProductDetails productDetails : list) {
                log("queryInAppDetail() onProductDetailsResponse()  productDetails = " + productDetails.toString());
                productDetailsMap.put(productDetails.getProductId(), productDetails);
            }

            SubscribeStats.statsGetProductDetailSuccess(productDetailsMap.keySet(), list.toString());
            if (result.getResponseCode() != BillingClient.BillingResponseCode.OK || list.size() == 0) {
                retryProductDetail();
            }
        });
    }

    private void querySkuInAppDetail(SkuDetailsResponseListener skuResponseListener){
        List<String> productIDSet = new ArrayList<>();
        productIDSet.add(Client.PRODUCT_ID_ONE);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        SkuDetailsParams params1 = params.setSkusList(productIDSet).setType(BillingClient.SkuType.INAPP).build();
        client.querySkuDetailsAsync(params1, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                log("querySkuInAppDetail() onSkuDetailsResponse()  resultcode = " + billingResult.getResponseCode());
                skuResponseListener.onSkuDetailsResponse(billingResult,list);
                if (list.size() > 0)
                    DetailCacheManager.getInstance().saveSkuDetailData2Local(list);

                for (SkuDetails productDetails : list) {
                    log("querySkuInAppDetail() onProductDetailsResponse()  productDetails = " + productDetails.toString());
                    skuDetailsMap.put(productDetails.getSku(), productDetails);
                }

                SubscribeStats.statsGetProductDetailSuccess(skuDetailsMap.keySet(), list.toString());
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || list.size() == 0) {
                    retryProductDetail();
                }
            }
        });
    }

    private int retryProductDetailCount = 0;

    private int retryInAppDetailCount = 0;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                innerUpdateProductDetails(IAPManager.getInstance().getSubscribeProductList());
                PurchaseManager.log("handleMessage()  retryProductDetail() ");
            }else if (msg.what == 101){
                innerInAppUpdateProductDetails(IAPManager.getInstance().getSubscribeProductList());
                PurchaseManager.log("handleMessage()  retryInAppProDetail() ");
            }
        }
    };

    private void retryProductDetail() {
        if (retryProductDetailCount > ConfigStyle.getReconnectMaxCount()) {
            handler.removeMessages(100);
            return;
        }

        retryProductDetailCount++;
        PurchaseManager.log("retryProductDetail()  retryCount = " + retryProductDetailCount);

        long delay_time = ConfigStyle.getReconnectDelayMill();

        handler.removeMessages(100);
        handler.sendEmptyMessageDelayed(100, delay_time);
    }

    private void retryInAppProDetail(){
        if (retryInAppDetailCount > ConfigStyle.getReconnectMaxCount()) {
            handler.removeMessages(101);
            return;
        }

        retryInAppDetailCount++;
        PurchaseManager.log("retryProductDetail()  retryCount = " + retryInAppDetailCount);

        long delay_time = ConfigStyle.getReconnectDelayMill();

        handler.removeMessages(101);
        handler.sendEmptyMessageDelayed(101, delay_time);
    }

    /**
     * 返回上一次查询到的价格（此方法不会发起任何IPC请求）
     */
    public Map<String, ProductDetails> getProductDetailsMap() {
        // todo testused
        if (productDetailsMap.size() == 0)
            return DetailCacheManager.getInstance().getProductDetailsMap();

        return productDetailsMap;
    }

    public Map<String,SkuDetails> getSkuDetailsMap(){
        if (skuDetailsMap.size() == 0)
            return DetailCacheManager.getInstance().getSkuDetailsMap();

        return skuDetailsMap;
    }

    public Currency getInAppPriceCurrency(String productId) {
        ProductDetails productDetails = getProductDetailsMap().get(productId);
        SkuDetails skuDetails = getSkuDetailsMap().get(productId);
        if (productDetails != null){
            if (productDetails.getProductId().equals(productId)) {
                try {
                    String currencyCode;
                    currencyCode = productDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
                    log("getPriceCurrency==productDetails==>"+currencyCode);
                    return Currency.getInstance(currencyCode);

                } catch (Exception e) {
                    //Logger.e(PurchaseManager.TAG, e);
                }

                return null;
            }
        }else if (skuDetails != null){
            if (skuDetails.getSku().equals(productId)) {

                try {
                    String currencyCode;
                    currencyCode = skuDetails.getPriceCurrencyCode();
                    log("getPriceCurrency==skuDetails==>"+currencyCode);
                    return Currency.getInstance(currencyCode);

                } catch (Exception e) {
                    //Logger.e(PurchaseManager.TAG, e);
                }

                return null;
            }
        }
        return null;
    }

    public static class Builder {
        private Context context;
        private List<String> subsProductList;
        private ConnectedCallback connectedCallback;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setSubsProductList(List<String> subsList) {
            this.subsProductList = subsList;
            return this;
        }

        public Builder setConnectedCallback(ConnectedCallback connectedCallback) {
            this.connectedCallback = connectedCallback;
            return this;
        }

        public PurchaseManager build() {
            PurchaseManager purchaseManager = new PurchaseManager();
            purchaseManager.init(this);
            return purchaseManager;
        }
    }

    public static class BuyParams {
        private Activity activity;
        private String productId;

        private String currentProductId;

        private BuyCallback buyCallback;

        public BuyParams(Activity activity, String productId) {
            this.activity = activity;
            this.productId = productId;
        }

        public Activity getActivity() {
            return activity;
        }

        public BuyCallback getBuyCallback() {
            return buyCallback;
        }

        public String getProductId() {
            return productId;
        }

        public BuyParams setBuyCallback(BuyCallback buyCallback) {
            this.buyCallback = buyCallback;
            return this;
        }

        @Override
        public String toString() {
            return "BuyParams{" +
                    "activity=" + activity +
                    ", sku='" + productId + '\'' +
                    ", currentProductId='" + currentProductId + '\'' +
                    ", buyCallback=" + buyCallback +
                    '}';
        }
    }

    public static void log(String msg) {
        Logger.d(TAG, msg + " \n");
    }
}
