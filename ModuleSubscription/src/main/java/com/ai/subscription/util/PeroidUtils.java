package com.ai.subscription.util;

import static com.ai.subscription.purchase.Client.PRODUCT_ID_3_MONTH;
import static com.ai.subscription.purchase.Client.PRODUCT_ID_HALF_YEAR;
import static com.ai.subscription.purchase.Client.PRODUCT_ID_MONTH;
import static com.ai.subscription.purchase.Client.PRODUCT_ID_WEEK;
import static com.ai.subscription.purchase.Client.PRODUCT_ID_YEAR;

import com.android.billingclient.api.ProductDetails;
import com.ai.subscription.R;
import com.ai.subscription.purchase.PurchaseManager;
import com.android.billingclient.api.SkuDetails;
import com.doodlecamera.base.core.log.Logger;
import com.doodlecamera.base.core.utils.lang.ObjectStore;

import java.util.List;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/19
 * Time   :   4:19 下午
 */
public class PeroidUtils {

    public static String getPeriod(String billingPeriod) {

        switch (billingPeriod) {
            case "P1M":
                String monthText = ObjectStore.getContext().getResources().getString(R.string.string_month);
                return monthText;

            case "P1Y":
                String yearText = ObjectStore.getContext().getResources().getString(R.string.string_year);
                return  yearText;

            case "P6M":
                String halfYearText = ObjectStore.getContext().getResources().getString(R.string.sub_half_year);
                return halfYearText;

            case "P3M":
                String threeMonthText = ObjectStore.getContext().getResources().getString(R.string.sub_three_month);
                return threeMonthText;

            case "P1W":
                String weekText = ObjectStore.getContext().getResources().getString(R.string.string_week);
                return weekText;

            default:
                return "";
        }
    }

    public static String getPeriodForId(String productId) {
        switch (productId) {
            case PRODUCT_ID_MONTH:
                String monthText = ObjectStore.getContext().getResources().getString(R.string.string_month);
                return monthText;
            case PRODUCT_ID_3_MONTH:
                String threeMonthText = ObjectStore.getContext().getResources().getString(R.string.sub_month_periodd);
                return "3 "+ threeMonthText;
            case PRODUCT_ID_YEAR:
                String yearText = ObjectStore.getContext().getResources().getString(R.string.string_year);
                return yearText;
            case PRODUCT_ID_HALF_YEAR:
                return ObjectStore.getContext().getResources().getString(R.string.sub_half_year);
            case PRODUCT_ID_WEEK:
                String weekText = ObjectStore.getContext().getResources().getString(R.string.string_week);
                return weekText;
            default:
                return "--";
        }
    }

    public static int getProductMonths(String billingPeriod) {
        switch (billingPeriod) {
            case "P1M":
                return 1;

            case "P1Y":
                return 12;

            case "P6M":
                return 6;

            case "P3M":
                return 3;
            default:
                return -1;
        }
    }

    public static String getPeriod2(String billingPeriod) {

        switch (billingPeriod) {
            case "P1M":
                String monthText = ObjectStore.getContext().getResources().getString(R.string.sub_month_periodd);
                return monthText;

            case "P1Y":
                String yearText = ObjectStore.getContext().getResources().getString(R.string.sub_year_peroidd);
                return yearText;

            case "P6M":
                String halfYearText = ObjectStore.getContext().getResources().getString(R.string.sub_half_year);
                return halfYearText;

            case "P3M":
                String threeMonthText = ObjectStore.getContext().getResources().getString(R.string.sub_three_month);
                return threeMonthText;

            case "P1W":
                String weekText = ObjectStore.getContext().getResources().getString(R.string.sub_week);
                return weekText;

            default:
                return "";
        }
    }

    //  $2.99/Year
    public static String getPriceText(ProductDetails detail) {
        if (detail == null) return "";
        try {
            List<ProductDetails.PricingPhase> list = detail.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList();
            if (list.get(0).getPriceAmountMicros() != 0) {
                String formatPrice = list.get(0).getFormattedPrice();
                String billingPeriod = list.get(0).getBillingPeriod();
                String periodText = PeroidUtils.getPeriod2(billingPeriod);
                return formatPrice + "/" + periodText;
            }
            // 含 免费试用期,则用第二个条目处理
            String formatPrice = list.get(1).getFormattedPrice();
            String billingPeriod = list.get(1).getBillingPeriod();
            String periodText = PeroidUtils.getPeriod2(billingPeriod);

            return formatPrice + "/" + periodText;
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }

        return "";
    }

    public static String getSkuPriceText(SkuDetails details){
        if (details == null) return "";
        try {
            // 含 免费试用期,则用第二个条目处理
            String formatPrice = details.getPrice();//list.get(1).getFormattedPrice();
            String billingPeriod = details.getSubscriptionPeriod();//list.get(1).getBillingPeriod();
            String periodText = PeroidUtils.getPeriod2(billingPeriod);

            return formatPrice + "/" + periodText;
        } catch (Exception e) {
            Logger.e(PurchaseManager.TAG, e);
        }

        return "";
    }


    public static int getProductValue(String productId) {
        switch (productId) {
            case PRODUCT_ID_YEAR:
                return 50;
            case PRODUCT_ID_HALF_YEAR:
                return 40;
            case PRODUCT_ID_3_MONTH:
                return 30;
            case PRODUCT_ID_MONTH:
                return 20;
            default:
                return 10;
        }
    }
}
