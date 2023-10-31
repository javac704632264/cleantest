package com.ai.subscription.cache;

/**
 * Author :   wutianlong@ushareit.com
 * Date   :   2022/9/21
 * Time   :   4:11 下午
 */
public interface IFetchDetail {
    void fetchStart();
    void fetchResult(boolean suc);
}
