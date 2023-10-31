package com.ai.subscription.hepler;

import android.text.TextUtils;

import com.android.billingclient.api.ProductDetails;
import com.ai.subscription.cache.DetailCacheManager;
import com.ai.subscription.util.PeroidUtils;
import com.android.billingclient.api.SkuDetails;

import java.util.Currency;
import java.util.List;

public class SubscribeBean {
    private String current;

    private List<ProductDetails> productDetailsList;

    private List<SkuDetails> productSkuDetailsList;

    public SubscribeBean() {
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public List<ProductDetails> getProductDetailsList() {
        return productDetailsList;
    }

    public void setProductDetailsList(List<ProductDetails> productDetailsList) {
        this.productDetailsList = productDetailsList;
    }

    public void setProductSkuDetailsList(List<SkuDetails> productSkuDetailsList){
        this.productSkuDetailsList = productSkuDetailsList;
    }

    //  $2.99/Year
    public String getPriceDollarPeriod(String productId) {
        if (productDetailsList == null)
            return "";

        for (ProductDetails detail : productDetailsList) {
            if (detail.getProductId().equals(productId)) {
                String value = PeroidUtils.getPriceText(detail);
                DetailCacheManager.getInstance().savePricePeriod(productId, value);
                return value;
            }
        }
        return "";
    }

    // 为空 则从缓存中 获取
    public String getPriceDollarPeriodWrapper(String productId) {
        String value = getPriceDollarPeriod(productId);
        return TextUtils.isEmpty(value) ? DetailCacheManager.getInstance().getPricePeriod(productId) : value;
    }

    //  $2.99
    public String getPriceDollar(String productId) {
        if (productDetailsList != null && productDetailsList.size() > 0){
            for (ProductDetails detail : productDetailsList) {
                if (detail.getProductId().equals(productId)) {

                    try {
                        List<ProductDetails.PricingPhase> list = detail.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList();

                        if (list.get(0).getPriceAmountMicros() != 0) {
                            long microPrice = list.get(0).getPriceAmountMicros();
                            float exChangeRate = 1000000.0F;
                            float value = microPrice/exChangeRate;
                            DetailCacheManager.getInstance().savePrice(productId,String.valueOf(value));
                            return String.valueOf(value);
                        }

                        long microPrice = list.get(1).getPriceAmountMicros();
                        float exChangeRate = 1000000.0F;
                        float value = microPrice/exChangeRate;
                        DetailCacheManager.getInstance().savePrice(productId, String.valueOf(value));
                        return String.valueOf(value);

                    } catch (Exception e) {
                        //Logger.e(PurchaseManager.TAG, e);
                    }

                    return "";
                }
            }
        }else if (productSkuDetailsList != null){
            for (SkuDetails detail : productSkuDetailsList) {
                if (detail.getSku().equals(productId)) {

                    try {
                        long microPrice =  detail.getPriceAmountMicros();
                        float exChangeRate = 1000000.0F;
                        float value = microPrice/exChangeRate;
                        DetailCacheManager.getInstance().savePrice(productId, String.valueOf(value));
                        return String.valueOf(value);

                    } catch (Exception e) {
                        //Logger.e(PurchaseManager.TAG, e);
                    }

                    return "";
                }
            }
        }
//        if (productDetailsList == null)
//            return "";


        return "";
    }

    public double getPriceAmount(String productId) {
        if (productDetailsList == null)
            return -1;

        for (ProductDetails detail : productDetailsList) {
            if (detail.getProductId().equals(productId)) {
                try {
                    List<ProductDetails.PricingPhase> list = detail.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList();
                    long priceAmountMicros = list.get(0).getPriceAmountMicros();
                    if (priceAmountMicros == 0) {
                        priceAmountMicros = list.get(1).getPriceAmountMicros();
                    }

                    return priceAmountMicros > 0 ? priceAmountMicros / (100 * 10000D) : -1;

                } catch (Exception e) {
                    //Logger.e(PurchaseManager.TAG, e);
                }

                return -1;
            }
        }
        return -1;
    }

    public Currency getPriceCurrency(String productId) {
        if (productDetailsList != null){
            for (ProductDetails detail : productDetailsList) {
                if (detail.getProductId().equals(productId)) {

                    try {
                        String currencyCode;
                        List<ProductDetails.PricingPhase> list = detail.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList();
                        long priceAmountMicros = list.get(0).getPriceAmountMicros();
                        if (priceAmountMicros != 0) {
                            currencyCode = list.get(0).getPriceCurrencyCode();
                        } else {
                            currencyCode = list.get(1).getPriceCurrencyCode();
                        }

                        return Currency.getInstance(currencyCode);

                    } catch (Exception e) {
                        //Logger.e(PurchaseManager.TAG, e);
                    }

                    return null;
                }
            }
        }else if (productSkuDetailsList != null){
            for (SkuDetails detail : productSkuDetailsList) {
                if (detail.getSku().equals(productId)) {

                    try {
                        String currencyCode;
                        currencyCode = detail.getPriceCurrencyCode();
                        return Currency.getInstance(currencyCode);

                    } catch (Exception e) {
                        //Logger.e(PurchaseManager.TAG, e);
                    }

                    return null;
                }
            }
        }

//        if (productDetailsList == null)
//            return null;


        return null;
    }

    public String getPriceSymbol(String productId) {
        Currency priceCurrency = getPriceCurrency(productId);
        if (priceCurrency == null) {
            return "";
        }

        return priceCurrency.getSymbol();
    }


    public String getPriceDollarWrapper(String productId) {
        String value = getPriceDollar(productId);
        return TextUtils.isEmpty(value) ? DetailCacheManager.getInstance().getPrice(productId) : value;
    }

    // P1Y
    public String getPeriodMode(String productId) {
        if (productDetailsList != null){
            for (ProductDetails detail : productDetailsList) {
                if (detail.getProductId().equals(productId)) {

                    try {
                        List<ProductDetails.PricingPhase> list = detail.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList();

                        if (list.get(0).getPriceAmountMicros() != 0) {

                            String value = list.get(0).getBillingPeriod();
                            DetailCacheManager.getInstance().savePriceMode(productId, value);

                            return value;
                        }

                        String value = list.get(1).getBillingPeriod();
                        DetailCacheManager.getInstance().savePriceMode(productId, value);

                        return value;
                    } catch (Exception e) {
                        //Logger.e(PurchaseManager.TAG, e);
                    }

                    return "";
                }
            }
        }else if (productSkuDetailsList != null){
            for (SkuDetails detail : productSkuDetailsList) {
                if (detail.getSku().equals(productId)) {

                    try {
                        String value = detail.getSubscriptionPeriod();
                        DetailCacheManager.getInstance().savePriceMode(productId, value);

                        return value;
                    } catch (Exception e) {
                        //Logger.e(PurchaseManager.TAG, e);
                    }

                    return "";
                }
            }
        }
//        if (productDetailsList == null)
//            return "";


        return "";
    }

    public String getPeriodModeWrapper(String productId) {
        String value = getPeriodMode(productId);
        return TextUtils.isEmpty(value) ? DetailCacheManager.getInstance().getPriceMode(productId) : value;
    }

}
