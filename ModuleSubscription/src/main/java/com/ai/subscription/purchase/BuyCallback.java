package com.ai.subscription.purchase;

import com.android.billingclient.api.Purchase;

public interface BuyCallback {
    void onBuySuccess(String productId, Purchase purchase);

    void onBuyFail(String productId, int errorCode, String reason);
}
