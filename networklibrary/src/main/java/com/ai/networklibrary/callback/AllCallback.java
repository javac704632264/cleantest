package com.ai.networklibrary.callback;

import com.ai.networklibrary.model.Response;

public interface AllCallback<T> {
    /** 对返回数据进行操作的回调， UI线程 */
    void onSuccess(Response<T> response);
    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    void onError(Response<T> response);
}
