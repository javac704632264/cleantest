package com.ai.subscription.config

import com.ai.subscription.util.AssetsUtils
import com.doodlecamera.base.core.ccf.CloudConfig
import com.doodlecamera.base.core.utils.lang.ObjectStore

object CloudConfigUtils {
    const val CLOUD_CONFIG_SUB_ID = "cloud_config_sub_id"
    const val CLOUD_CONFIG_ORIGIN_PRICE = "cloud_config_origin_price"
    const val CLOUD_CONFIG_SUB_SWITCH = "cloud_config_iap_switch"
    const val CLOUD_CONFIG_RECONNECT_COUNT = "cloud_config_reconnect_count"
    const val CLOUD_CONFIG_ENTER_SUB_PAGE = "cloud_config_enter_sub_page"
    const val CLOUD_CONFIG_PRICE_DISCOUNT = "cloud_config_price_discount"
    const val CLOUD_CONFIG_EXIT = "cloud_config_exit"

    fun getConfigSubId(): String?{
        return CloudConfig.getStringConfig(ObjectStore.getContext(),CLOUD_CONFIG_SUB_ID,AssetsUtils.getFromAssets("subid.json"))
    }

    fun getOriginPrice(): String?{
        return CloudConfig.getStringConfig(ObjectStore.getContext(), CLOUD_CONFIG_ORIGIN_PRICE,AssetsUtils.getFromAssets("origin_price.json"))
    }

    fun isSubOpen(): Boolean{
        return CloudConfig.getBooleanConfig(ObjectStore.getContext(), CLOUD_CONFIG_SUB_SWITCH,true)
    }

    fun getConnectCount(): Int{
        return CloudConfig.getIntConfig(ObjectStore.getContext(), CLOUD_CONFIG_RECONNECT_COUNT,2)
    }

    fun isEnterSubPage(): Boolean{
        return CloudConfig.getBooleanConfig(ObjectStore.getContext(), CLOUD_CONFIG_ENTER_SUB_PAGE,true)
    }

    fun getPriceDiscount(): String?{
        return CloudConfig.getStringConfig(ObjectStore.getContext(), CLOUD_CONFIG_PRICE_DISCOUNT,AssetsUtils.getFromAssets("price_discount.json"))
    }

    fun getExitConfig(): String?{
        return CloudConfig.getStringConfig(ObjectStore.getContext(), CLOUD_CONFIG_EXIT,AssetsUtils.getFromAssets("exit_config.json"))
    }
}