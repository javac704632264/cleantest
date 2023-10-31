package com.ai.subscription.hepler;

import static com.ai.subscription.subs.SubscribeStateChangeListener.KEY_EXTRA_SUB_TIME_MILLS;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ai.subscription.subs.SubscribeStateChangeListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.ai.subscription.R;
import com.ai.subscription.cache.DetailCacheManager;
import com.ai.subscription.config.ConfigStyle;
import com.ai.subscription.purchase.BuyCallback;
import com.ai.subscription.purchase.Client;
import com.ai.subscription.purchase.ConnectedCallback;
import com.ai.subscription.purchase.IQueryResult;
import com.ai.subscription.purchase.PurchaseManager;
import com.ai.subscription.stats.AdjustCollector;
import com.ai.subscription.stats.FirebaseCollector;
import com.ai.subscription.util.PeroidUtils;
import com.ai.subscription.util.SubscribeSettings;
import com.doodlecamera.base.core.log.Logger;
import com.doodlecamera.base.core.thread.TaskHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IAPManager {

    private final MutableLiveData<Boolean> mRemoveAdsLiveData;

    private final ArrayList<SubscribeStateChangeListener> mStateChangeListeners = new ArrayList<>();

    private PurchaseManager mPurchaseManager;

    private Context mContext;

    private static IAPManager mInstance;

    private IAPManager() {
        mRemoveAdsLiveData = new MutableLiveData<>();
    }

    public static IAPManager getInstance() {
        if (mInstance == null) {
            synchronized (IAPManager.class) {
                if (mInstance == null) {
                    mInstance = new IAPManager();
                }
            }
        }
        return mInstance;
    }

    public PurchaseManager getPurchaseManager() {
        return mPurchaseManager;
    }

    public List<String> getSubscribeProductList() {
        return new ArrayList<>(ConfigStyle.getAllProductID());
    }

    public void init(Context context) {
        this.mContext = context;

        DetailCacheManager.getInstance().removeProductDetailCache();
        boolean isNewVip = SubscribeSettings.getSubscribeState() == 1;
        if (isNewVip) {
            mRemoveAdsLiveData.setValue(true);
        } else {
            mRemoveAdsLiveData.setValue(false);
        }
        mPurchaseManager = new PurchaseManager.Builder(context).setSubsProductList(getSubscribeProductList()).setConnectedCallback(new ConnectedCallback() {
            boolean isUpdate = false;

            @Override
            public void onGpConnectSuccess() {
                if (!isUpdate) {
                    updateVipState(null);
                    isUpdate = true;
                }
            }

            @Override
            public void onGpConnectFail() {
                PurchaseManager.log("billing connect  fail.......");
            }
        }).build();
    }

    private void queryLocalVipState() {
        boolean isNewVip = SubscribeSettings.getSubscribeState() == 1;

        if (isNewVip) {
            mRemoveAdsLiveData.postValue(true);
        } else {
            mRemoveAdsLiveData.postValue(false);
        }
    }

    public void updateVipState(final IQueryResult iQueryResult) {
        TaskHelper.exec(new TaskHelper.Task() {

            List<Purchase> purchaseList;

            @Override
            public void execute() {
                mPurchaseManager.queryAllPurchases(new Client.PurchasesResultListener() {
                    @Override
                    public void onResult(ArrayList<Purchase> mGPPurchases) {
                        purchaseList = mGPPurchases;
                        TaskHelper.exec(new TaskHelper.UITask() {
                            @Override
                            public void callback(Exception e) {
                                if (purchaseList == null) {
                                    PurchaseManager.log(" not connection .......");
                                    queryNotSubscribe();
                                    return;
                                }

                                if (purchaseList.size() == 0) { // 没有查询到用户已付费

                                    if (isVIP()) {

                                        int queryCount = SubscribeSettings.getKeyQuerySubscribeCount();

                                        int maxDelayCount = ConfigStyle.getExtraUseMaxCount();
                                        if (queryCount >= maxDelayCount) {  // 最多给多少次 缓冲
                                            PurchaseManager.log("remove vip, over maxcount = " + maxDelayCount + "  queryCount = " + queryCount);
                                            queryNotSubscribe();
                                            removeVip();
                                        }

                                        PurchaseManager.log(" local buffer count = " + (queryCount + 1));

                                        SubscribeSettings.setKeyQuerySubscribeCount(queryCount + 1);

                                    } else {
                                        PurchaseManager.log("remove vip");
                                        queryNotSubscribe();
                                        removeVip();
                                    }

                                    return;
                                }

                                if (iQueryResult != null) iQueryResult.querySuccess();

                                // 查询到用户 已付费，重置 缓冲   ，以备后续 查询不到时用
                                SubscribeSettings.setKeyQuerySubscribeCount(0);

                                mPurchaseManager.saveData(purchaseList.get(0));

                                addVip();
                            }
                        });
                    }
                });
            }

            @Override
            public void callback(Exception e) {
            }

            private void queryNotSubscribe() {
                if (iQueryResult != null) iQueryResult.queryFail();
            }
        });
    }

    public void buy(final FragmentActivity activity, final String productId, final String portal, final BuyCallback buyCallback) {
        if (mPurchaseManager == null) return;
        AdjustCollector.onEvent(AdjustCollector.SUB_CLICK_TOKEN);
        Bundle params = new Bundle();
        params.putString("product_id", productId);
        FirebaseCollector.onEvent(FirebaseCollector.SUB_CLICK, params);
        PurchaseManager.BuyParams buyParams = new PurchaseManager.BuyParams(activity, productId)
                //.setCurrentProductId((Objects.requireNonNull(SudokuIapHelper.getInstance().getPurchaseManager().getSkuDetailsMap().get(skuName))).getProductId())
                .setBuyCallback(new BuyCallback() {
                    @Override
                    public void onBuySuccess(String productId, Purchase purchase) {
                        if (buyCallback != null) buyCallback.onBuySuccess(productId, purchase);
                        showSuccessDialog(activity);
                        updateVipState(null);

                        AdjustCollector.onEvent(AdjustCollector.SUB_SUCCESS_TOKEN);
                        Bundle params = new Bundle();
                        params.putLong(FirebaseAnalytics.Param.VALUE, PeroidUtils.getProductValue(productId));
                        params.putString("product_id", productId);
                        FirebaseCollector.onEvent(FirebaseCollector.SUB_SUCCESS, params);
                    }

                    @Override
                    public void onBuyFail(String productId, int errorCode, String reason) {
                        if (buyCallback != null)
                            buyCallback.onBuyFail(productId, errorCode, reason);

                        switch (errorCode) {
                            case BillingClient.BillingResponseCode.USER_CANCELED:
                                Toast.makeText(activity, R.string.purchase_user_cancel, Toast.LENGTH_SHORT).show();
                                break;

                            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                                Toast.makeText(activity, R.string.purchase_gp_service_not_available, Toast.LENGTH_SHORT).show();
                                break;

                            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                                Toast.makeText(activity, R.string.purchase_already_owned, Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(activity, R.string.purchase_fail, Toast.LENGTH_SHORT).show();
                                break;
                        }

                        //if (!SubGiveUpDialogFragment.GIVEUP_PORTAL.equals(portal)) {
                        showRetryBuyDialog(activity, productId);
                        //}

                    }
                });

        try {
            mPurchaseManager.buy(buyParams);
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
    }

    public void showRetryBuyDialog(FragmentActivity activity, String pid) {
//        SubFailRetryDialogFragment retryDialogFragment = new SubFailRetryDialogFragment();
//
//        Bundle bundle = new Bundle();
//        bundle.putString(SubFailRetryDialogFragment.RETRY_PID, pid);
//        retryDialogFragment.setArguments(bundle);
//
//        retryDialogFragment.setEnclosingActivity(activity);
//        retryDialogFragment.show();
    }

    public void showSuccessDialog(final FragmentActivity activity) {
//        SubSuccessDialogFragment fragment = new SubSuccessDialogFragment();
//        fragment.setEnclosingActivity(activity);
//        fragment.show();
    }

    private void addVip() {
        PurchaseManager.log(" addVip()");

        SubscribeSettings.setSubscribeState(1);
        mRemoveAdsLiveData.postValue(true);
        notifySubStateChangeListener(true);
    }

    private void removeVip() {
        SubscribeSettings.resetProduct();

        mRemoveAdsLiveData.postValue(false);
        notifySubStateChangeListener(false);
    }

    @NonNull
    public LiveData<Boolean> getRemoveAdsLiveData() {
        return mRemoveAdsLiveData;
    }

    public boolean isVIP() {
        if (mRemoveAdsLiveData.getValue() == null) {
            boolean isNewVip = SubscribeSettings.getSubscribeState() == 1;
            if (isNewVip) {
                mRemoveAdsLiveData.setValue(true);
            } else {
                mRemoveAdsLiveData.setValue(false);
            }
        }
        return mRemoveAdsLiveData.getValue() != null && mRemoveAdsLiveData.getValue();
    }

    public void addSubStateChangeListener(SubscribeStateChangeListener listener) {
        if (listener == null) return;

        mStateChangeListeners.add(listener);
    }

    public void removeSubStateChangeListener(SubscribeStateChangeListener listener) {
        if (listener == null) return;

        mStateChangeListeners.remove(listener);
    }

    private void notifySubStateChangeListener(boolean isVip) {
        for (SubscribeStateChangeListener listener : mStateChangeListeners) {
            HashMap<String, String> map = new HashMap<>();
            map.put(KEY_EXTRA_SUB_TIME_MILLS, SubscribeSettings.getSubscribeSuccTime() + "");
            listener.subStateChange(isVip, map);
        }
    }
}
