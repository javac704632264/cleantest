package com.ai.subscription.subs;

import java.util.HashMap;

public interface SubscribeStateChangeListener {
    String KEY_EXTRA_SUB_TIME_MILLS = "key_extra_sub_time_mills";

    /**
     * @param isVip true 已订阅 false 未订阅
     * @param extra 额外信息集合，比如：订阅成功的时间戳，单位ms
     */
    void subStateChange(boolean isVip, HashMap<String, String> extra);
}
