package com.ai.subscription.component;

/**
 * create time: 2023/2/14
 * Descrite:
 */
public interface ISubFragment {
    void setNeedShowLoading(boolean show);

    void dismissLoadingDialog();

    void showRetryFetchDeshowtailView(boolean show);

    String getProductIdsStr();
}
