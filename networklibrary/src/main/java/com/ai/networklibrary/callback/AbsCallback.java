package com.ai.networklibrary.callback;


import com.ai.networklibrary.model.Progress;
import com.ai.networklibrary.model.Response;
import com.ai.networklibrary.request.base.Request;
import com.ai.networklibrary.utils.NetLogger;

/**
 * Created by hao on 2018/2/26.
 */

public abstract class AbsCallback<T> implements Callback<T> {
    @Override
    public void onStart(Request<T, ? extends Request> request) {
    }

    @Override
    public void onCacheSuccess(Response<T> response) {
    }

    @Override
    public void onError(Response<T> response) {
        NetLogger.printStackTrace(response.getException());
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void uploadProgress(Progress progress) {
    }

    @Override
    public void downloadProgress(Progress progress) {
    }
}
