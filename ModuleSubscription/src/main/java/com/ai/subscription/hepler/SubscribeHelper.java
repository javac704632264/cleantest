package com.ai.subscription.hepler;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ai.subscription.purchase.Client;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.ai.subscription.cache.IFetchDetail;
import com.ai.subscription.purchase.PurchaseManager;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.doodlecamera.base.core.log.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SubscribeHelper {

    private List<ProductDetails> productDetailsList;

    private List<SkuDetails> productSkuDetailsList;

    private SubscribeBean subscribeBean;

    private MutableLiveData<SubscribeBean> liveData;

    public SubscribeHelper(IFetchDetail iFetchDetail) {
        try {
            liveData = new MutableLiveData<>();
            initPrice(iFetchDetail);
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }
    }

    private void initPrice(final IFetchDetail iFetchDetail) {
        subscribeBean = new SubscribeBean();

        PurchaseManager purchaseManager = IAPManager.getInstance().getPurchaseManager();

        if (purchaseManager == null)
            return;

        ArrayList<ProductDetails> details = getProductDetails(purchaseManager);

        ArrayList<SkuDetails> skuDetails = getSkuDetails(purchaseManager);

        if (details.size() > 0) {
            if (TextUtils.isEmpty(details.get(0).getProductId()) && !TextUtils.isEmpty(skuDetails.get(0).getSku())){
                updateSkuBean(skuDetails);
            }else{
                updateBean(details);
            }
        } else {
            tryFetchDetail(iFetchDetail);
        }
    }

    @NonNull
    public ArrayList<ProductDetails> getProductDetails(PurchaseManager purchaseManager) {
        Map<String, ProductDetails> skuDetailsMap = purchaseManager.getProductDetailsMap();
        Collection<ProductDetails> values = skuDetailsMap.values();
        return new ArrayList<>(values);
    }

    public ArrayList<SkuDetails> getSkuDetails(PurchaseManager purchaseManager){
        Map<String, SkuDetails> skuDetailsMap = purchaseManager.getSkuDetailsMap();
        Collection<SkuDetails> values = skuDetailsMap.values();
        return new ArrayList<>(values);
    }

    public void tryFetchDetail(final IFetchDetail iFetchDetail) {
        PurchaseManager purchaseManager = IAPManager.getInstance().getPurchaseManager();
        if (purchaseManager == null)
            return;

        if (iFetchDetail != null) {
            iFetchDetail.fetchStart();
        }

        List<String> subscribeProductList = IAPManager.getInstance().getSubscribeProductList();

        purchaseManager.queryProductDetails(BillingClient.ProductType.SUBS, subscribeProductList, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || list == null) {

                    if (iFetchDetail != null) {
                        iFetchDetail.fetchResult(false);
                    }

                    liveData.postValue(subscribeBean);
                    return;
                }

                if (iFetchDetail != null) {
                    iFetchDetail.fetchResult(true);
                }

                productDetailsList = list;
                SubscribeHelper.this.updateBean(productDetailsList);
            }
        }, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || list == null) {

                    if (iFetchDetail != null) {
                        iFetchDetail.fetchResult(false);
                    }

                    liveData.postValue(subscribeBean);
                    return;
                }

                if (iFetchDetail != null) {
                    iFetchDetail.fetchResult(true);
                }

                productSkuDetailsList = list;
                SubscribeHelper.this.updateSkuBean(productSkuDetailsList);
            }
        });
    }

    void updateBean(List<ProductDetails> details) {
        subscribeBean.setProductDetailsList(details);

        liveData.postValue(subscribeBean);
    }

    void updateSkuBean(List<SkuDetails> details){
        subscribeBean.setProductSkuDetailsList(details);

        liveData.postValue(subscribeBean);
    }

    public LiveData<SubscribeBean> getLiveData() {
        return liveData;
    }
}
