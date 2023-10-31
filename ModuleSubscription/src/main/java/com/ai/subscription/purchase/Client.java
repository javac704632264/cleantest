package com.ai.subscription.purchase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.ai.subscription.R;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.hepler.IAPManager;
import com.ai.subscription.util.PeroidUtils;
import com.ai.subscription.util.SubscribeSettings;
import com.ai.subscription.util.SubscribeStats;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.doodlecamera.base.core.utils.lang.ObjectStore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// wiki : https://developer.android.com/google/play/billing/integrate
public class Client implements BillingClientStateListener, PurchasesUpdatedListener {

    public static final String PRODUCT_ID_MONTH = "doodle_camera_03";
    public static final String PRODUCT_ID_YEAR = "doodle_camera_02";
    public static final String PRODUCT_ID_HALF_YEAR = "sailfish_sub_6month";
    public static final String PRODUCT_ID_3_MONTH = "sailfish_sub_3month";
    public static final String PRODUCT_ID_WEEK = "doodle_camera_01";

    public static final String PRODUCT_ID_ONE = "avatar_n";
    private static final int BILLING_CODE_SERVICE_DISCONNECT = -999;

    private final BillingClient billingClient;

    private int billingConnectionCode = -1000;

    private int retryCount = 0;

    private final ConnectedCallback connectedCallback;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            PurchaseManager.log("handleMessage() re  startConnection() ");

            billingClient.startConnection(Client.this);
            SubscribeStats.statsConnectStart();
        }
    };

    public Client(Context context, ConnectedCallback connectedCallback) {
        this.connectedCallback = connectedCallback;

        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(this);
        SubscribeStats.statsConnectStart();

    }


    public boolean isConnectionSuccess() {
        return billingConnectionCode == BillingClient.BillingResponseCode.OK;
    }

    public int getBillingConnectionCode() {
        return billingConnectionCode;
    }

    private long lastConnectionTime;

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        retryCount = 0;
        billingConnectionCode = billingResult.getResponseCode();
        PurchaseManager.log("  isFeatureSupported() ==>"+billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS));
        PurchaseManager.log("onBillingSetupFinished() connect to google play success , code = " + billingConnectionCode + "   debug_msg = " + billingResult.getDebugMessage());

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            long time = System.currentTimeMillis() - lastConnectionTime;

            // two times sucess callbak  must over 10 seconds,    avoid some devices repeat callback
            if (time > 10 * 1000) {
                SubscribeStats.statsConnectSucc();
                connectedCallback.onGpConnectSuccess();
                lastConnectionTime = time;
            }

        } else {
            SubscribeStats.statsConnectFail(billingConnectionCode, billingResult.getDebugMessage());
            connectedCallback.onGpConnectFail();

            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                retryConnectDepConfig();
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        SubscribeStats.statsConnectFail(-10000, "onBillingServiceDisconnected_callback");
        connectedCallback.onGpConnectFail();

        retryConnectDepConfig();

        billingConnectionCode = BILLING_CODE_SERVICE_DISCONNECT;
    }

    private void retryConnectDepConfig() {
        if (retryCount > ConfigStyle.getReconnectMaxCount())
            return;

        retryCount++;
        PurchaseManager.log("retryConnectDepConfig()  retryCount = " + retryCount);

        long delay_time = ConfigStyle.getReconnectDelayMill();
        retryConnection(delay_time);
    }

    public void retryConnection(long delay_time) {
        PurchaseManager.log("retryConnection()  delay_time = " + delay_time);
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1, delay_time);
    }

    private BuyCallback buyCallback;
    private String mCurrentProductId = "";

    // 发起购买
    public void buy(PurchaseManager.BuyParams buyParams, BillingFlowParams params) {

        this.buyCallback = buyParams.getBuyCallback();

        mCurrentProductId = buyParams.getProductId();

        BillingResult billingResult = billingClient.launchBillingFlow(buyParams.getActivity(), params);

        PurchaseManager.log("buy() code = " + billingResult.getResponseCode() + "  debug_msg = " + billingResult.getDebugMessage());

        int billingCode = billingResult.getResponseCode();

        if (billingCode != BillingClient.BillingResponseCode.OK) {

            if (this.buyCallback != null) {
                this.buyCallback.onBuyFail(mCurrentProductId, billingCode, billingResult.getDebugMessage());
            }

            this.buyCallback = null;
        }
    }

    public void consumeAsync(String purchaseToken, ConsumeResponseListener listener) {
        ConsumeParams.Builder builder = ConsumeParams.newBuilder();
        ConsumeParams build = builder.setPurchaseToken(purchaseToken).build();
        billingClient.consumeAsync(build, listener);
    }

    // 购买 成功 Or 失败 GP 回调通知给 我们
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {  // list 中 只会返回本次 购买商品，不会返回所以已购商品
        PurchaseManager.log("onPurchasesUpdated()  code = " + billingResult.getResponseCode() + "  debug_message = " + billingResult.getDebugMessage());

        int billingCode = billingResult.getResponseCode();
        if (billingCode == BillingClient.BillingResponseCode.OK) {

            if (buyCallback != null && list != null) {

                for (Purchase purchase : list) {

                    PurchaseManager.log("onPurchasesUpdated() purchase = " + purchase.toString() + " \n");

                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        PurchaseManager.log("onPurchasesUpdated()  success getOrderId()  = " + purchase.getOrderId() + " \n");

                        saveData(purchase);

                        String productid = "";
                        try {
                            productid = new JSONObject(purchase.getOriginalJson()).optString("productId", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        buyCallback.onBuySuccess(productid, purchase);
                        List<String> products = purchase.getProducts();
                        for (String pro: products){
                            if (pro.equals(Client.PRODUCT_ID_ONE)){
                                PurchaseManager.log("product==>"+pro);
                                consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
                                    @Override
                                    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {

                                    }
                                });
                                return;
                            }
                        }
                        PurchaseManager.log("acknowledgePurchase========>"+purchase.toString());
                        acknowledgePurchase(purchase);
                        break;
                    }
                }
            }

        } else if (billingCode == BillingClient.BillingResponseCode.USER_CANCELED) {

            if (buyCallback != null) {
                buyCallback.onBuyFail(mCurrentProductId, billingCode, ObjectStore.getContext().getResources().getString(R.string.purchase_user_cancel));
            }

        } else {

            if (buyCallback != null) {
                buyCallback.onBuyFail(mCurrentProductId, billingCode, billingResult.getDebugMessage());
            }
        }

        buyCallback = null;
    }

    public void saveData(Purchase purchase) {
        try {
            String productid = new JSONObject(purchase.getOriginalJson()).optString("productId", "");
            SubscribeSettings.setProductId(productid);

            PurchaseManager manager = IAPManager.getInstance().getPurchaseManager();
            if (manager != null) {
                ProductDetails details = manager.getProductDetailsMap().get(productid);
                SkuDetails skuDetails = manager.getSkuDetailsMap().get(productid);
                if (details != null){
                    String price = PeroidUtils.getPriceText(details);
                    if (!TextUtils.isEmpty(price))
                        SubscribeSettings.setProductPrice(price);
                }else if (skuDetails != null){
                    String price = PeroidUtils.getSkuPriceText(skuDetails);
                    if (!TextUtils.isEmpty(price))
                        SubscribeSettings.setProductPrice(price);
                }

            }

            long purchaseTime = new JSONObject(purchase.getOriginalJson()).optLong("purchaseTime", -1);
            SubscribeSettings.setSubscribeSuccTime(purchaseTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 此用户的 所有 已付费 集合。包括： 一次性付费 、订阅
    private final ArrayList<Purchase> mGPPurchases = new ArrayList<>();

    public ArrayList<Purchase> getGPPurchases() {
        return mGPPurchases;
    }

    // 与 Google Play 建立连接后，就可以 查询 用户已经购买的产品
    // 类型都是 订阅
    void queryPurchases(final PurchasesResultListener resultListener) {

        QueryPurchasesParams params =
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build();

        PurchasesResponseListener listener = (result, list) -> {  // list中 会返回所有已购买的商品
            PurchaseManager.log("queryPurchases()   onQueryPurchasesResponse()  code  = " + result.getResponseCode() + "  debug_msg = " + result.getDebugMessage()
                    + "  \n  list = " + list);

            mGPPurchases.clear();
            mGPPurchases.addAll(list);
            if (resultListener != null) {
                resultListener.onResult(mGPPurchases);
            }
        };

        billingClient.queryPurchasesAsync(params, listener);
    }

    void querySkyPurchases(final PurchasesResultListener resultListener){
        PurchasesResponseListener listener = (result, list) -> {  // list中 会返回所有已购买的商品
            PurchaseManager.log("queryPurchases()   onQueryPurchasesResponse()  code  = " + result.getResponseCode() + "  debug_msg = " + result.getDebugMessage()
                    + "  \n  list = " + list);

            mGPPurchases.clear();
            mGPPurchases.addAll(list);
            if (resultListener != null) {
                resultListener.onResult(mGPPurchases);
            }
        };

        billingClient.queryPurchasesAsync(BillingClient.ProductType.SUBS, listener);
    }

    // 查询 产品 详情
    void queryProductDetailsAsync(QueryProductDetailsParams detailsParams, ProductDetailsResponseListener listener) {
        billingClient.queryProductDetailsAsync(detailsParams, listener);
    }

    void querySkuDetailsAsync(SkuDetailsParams params, SkuDetailsResponseListener listener){
        billingClient.querySkuDetailsAsync(params,listener);
    }

    // 所有初始订阅购买交易 都需要确认订阅
    public void acknowledgePurchase(Purchase purchase) {
        if (purchase.isAcknowledged())
            return;

        PurchaseManager.log("acknowledgePurchase()  purchaseToken = " + purchase.getPurchaseToken() + "  \n");

        AcknowledgePurchaseParams build = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(build, billingResult ->
                PurchaseManager.log("onAcknowledgePurchaseResponse()  code =  " + billingResult.getResponseCode() + "  debug_msg = " + billingResult.getDebugMessage()));
    }

    public interface PurchasesResultListener {
        void onResult(ArrayList<Purchase> mGPPurchases);
    }
}
