package com.ai.networklibrary.cache.policy;


import com.ai.networklibrary.cache.CacheEntity;
import com.ai.networklibrary.callback.Callback;
import com.ai.networklibrary.exception.CacheException;
import com.ai.networklibrary.model.Response;
import com.ai.networklibrary.request.base.Request;

import okhttp3.Call;

/**
 * Created by hao on 2018/2/26.
 */

public class DefaultCachePolicy<T> extends BaseCachePolicy<T> {
    public DefaultCachePolicy(Request<T, ? extends Request> request) {
        super(request);
    }

    @Override
    public void onSuccess(final Response<T> success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                globalCallback(success,true);
                mCallback.onSuccess(success);
                mCallback.onFinish();
            }
        });
    }

    @Override
    public void onError(final Response<T> error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                globalCallback(error,false);
                mCallback.onError(error);
                mCallback.onFinish();
            }
        });
    }

    @Override
    public boolean onAnalysisResponse(final Call call, final okhttp3.Response response) {
        if (response.code() != 304) return false;

        if (cacheEntity == null) {
            final Response<T> error = Response.error(true, call, response, CacheException.NON_AND_304(request.getCacheKey()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(error);
                    mCallback.onFinish();
                }
            });
        } else {
            final Response<T> success = Response.success(true, cacheEntity.getData(), call, response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onCacheSuccess(success);
                    mCallback.onFinish();
                }
            });
        }
        return true;
    }

    @Override
    public Response<T> requestSync(CacheEntity<T> cacheEntity) {
        try {
            prepareRawCall();
        } catch (Throwable throwable) {
            return Response.error(false, rawCall, null, throwable);
        }
        Response<T> response = requestNetworkSync();
        //HTTP cache protocol
        if (response.isSuccessful() && response.code() == 304) {
            if (cacheEntity == null) {
                response = Response.error(true, rawCall, response.getRawResponse(), CacheException.NON_AND_304(request.getCacheKey()));
            } else {
                response = Response.success(true, cacheEntity.getData(), rawCall, response.getRawResponse());
            }
        }
        return response;
    }

    @Override
    public void requestAsync(CacheEntity<T> cacheEntity, Callback<T> callback) {
        mCallback = callback;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onStart(request);

                try {
                    prepareRawCall();
                } catch (Throwable throwable) {
                    Response<T> error = Response.error(false, rawCall, null, throwable);
                    globalCallback(error,false);
                    mCallback.onError(error);
                    mCallback.onFinish();
                    return;
                }
                requestNetworkAsync();
            }
        });
    }
}
