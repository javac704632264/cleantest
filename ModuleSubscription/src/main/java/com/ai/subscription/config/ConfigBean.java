package com.ai.subscription.config;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/7/13
 * Time   :   3:56 下午
 */
@Keep
public class ConfigBean {

    public List<ProductIdConfig> mProductConfigList = new ArrayList<>();

    public ConfigBean() {
    }

    public void addProduct(ProductIdConfig  product) {
        mProductConfigList.add(product);
    }

    public static class ProductIdConfig {
        public String productId = "";
        public int trial_day = 0;
        public String discount = "";
        public boolean isDefault = false;
        public String promotionInformation = "";

        public ProductIdConfig() {
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public void setTrial_day(int trial_day) {
            this.trial_day = trial_day;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }

        public String getPromotionInformation() {
            return promotionInformation;
        }

        public void setPromotionInformation(String promotionInformation) {
            this.promotionInformation = promotionInformation;
        }
    }
}
