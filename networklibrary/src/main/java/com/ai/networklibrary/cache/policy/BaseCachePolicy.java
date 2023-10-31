package com.ai.networklibrary.cache.policy;

import android.graphics.Bitmap;
import android.util.Log;

import com.ai.networklibrary.NetHttp;
import com.ai.networklibrary.cache.CacheEntity;
import com.ai.networklibrary.cache.CacheMode;
import com.ai.networklibrary.callback.Callback;
import com.ai.networklibrary.db.CacheManager;
import com.ai.networklibrary.exception.HttpException;
import com.ai.networklibrary.model.Response;
import com.ai.networklibrary.request.base.Request;
import com.ai.networklibrary.utils.HeaderParser;
import com.ai.networklibrary.utils.HttpUtils;
import com.ai.networklibrary.utils.NetLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Headers;

/**
 * Created by hao on 2018/2/26.
 */

public class BaseCachePolicy<T> implements CachePolicy<T> {

    protected Request<T, ? extends Request> request;
    protected volatile boolean canceled;
    protected volatile int currentRetryCount = 0;
    protected boolean executed;
    protected Call rawCall;
    protected Callback<T> mCallback;
    protected CacheEntity<T> cacheEntity;

    public BaseCachePolicy(Request<T, ? extends Request> request) {
        this.request = request;
    }


    @Override
    public void onSuccess(Response<T> success) {

    }

    @Override
    public void onError(Response<T> error) {

    }

    @Override
    public boolean onAnalysisResponse(Call call, okhttp3.Response response) {
        return false;
    }

    @Override
    public CacheEntity<T> prepareCache() {
        //check the config of cache
        if (request.getCacheKey() == null) {
            request.cacheKey(HttpUtils.createUrlFromParams(request.getBaseUrl(), request.getParams().urlParamsMap));
        }
        if (request.getCacheMode() == null) {
            request.cacheMode(CacheMode.NO_CACHE);
        }

        CacheMode cacheMode = request.getCacheMode();
        if (cacheMode != CacheMode.NO_CACHE) {
            //noinspection unchecked
            cacheEntity = (CacheEntity<T>) CacheManager.getInstance().get(request.getCacheKey());
            HeaderParser.addCacheHeaders(request, cacheEntity, cacheMode);
            if (cacheEntity != null && cacheEntity.checkExpire(cacheMode, request.getCacheTime(), System.currentTimeMillis())) {
                cacheEntity.setExpire(true);
            }
        }

        if (cacheEntity == null || cacheEntity.isExpire() || cacheEntity.getData() == null || cacheEntity.getResponseHeaders() == null) {
//            if (NetUtils.isNetWorkAvailable(ContextUtils.getAppContext())){
                cacheEntity = null;
//            }
        }
        return cacheEntity;
    }

    @Override
    public Call prepareRawCall() throws Throwable {
        if (executed) throw HttpException.COMMON("Already executed!");
        executed = true;
        rawCall = request.getRawCall();
        if (canceled) rawCall.cancel();
        return rawCall;
    }

    protected Response<T> requestNetworkSync() {
        try {
            okhttp3.Response response = rawCall.execute();
            int responseCode = response.code();

            //network error
            if (responseCode == 404 || responseCode >= 500) {
                return Response.error(false, rawCall, response, HttpException.NET_ERROR(responseCode));
            }

            T body = request.getConverter().convertResponse(response);
            //save cache when request is successful
            NetLogger.e("body="+body);
            saveCache(response.headers(), body);
            return Response.success(false, body, rawCall, response);
        } catch (Throwable throwable) {
            if (throwable instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                currentRetryCount++;
                rawCall = request.getRawCall();
                if (canceled) {
                    rawCall.cancel();
                } else {
                    requestNetworkSync();
                }
            }
            return Response.error(false, rawCall, null, throwable);
        }
    }

    protected void requestNetworkAsync() {
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TestActivity","onFailure======>");
                if (currentRetryCount < request.getRetryCount()) {
                    //retry when timeout
                    currentRetryCount++;
                    rawCall = request.getRawCall();
                    if (canceled) {
                        rawCall.cancel();
                    } else {
                        rawCall.enqueue(this);
                    }
                } else {
                    if (!call.isCanceled()) {
                        Response<T> error = Response.error(false, call, null, e);
                        globalCallback(error,false);
                        onError(error);
                    }
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                int responseCode = response.code();
//                Log.e("TestActivity","onResponse======>"+response.body().string());

                //network error
                if (responseCode != 200){
                    if (currentRetryCount < request.getRetryCount()) {
                        //retry when timeout
                        currentRetryCount++;
                        rawCall = request.getRawCall();
                        if (canceled) {
                            rawCall.cancel();
                        } else {
                            rawCall.enqueue(this);
                        }
                    }else {
                        Response<T> error = Response.error(false, call, response, HttpException.NET_ERROR(responseCode));
                        globalCallback(error,false);
                        onError(error);
                    }
                    return;
                }
//                if (responseCode == 404 || responseCode >= 500) {
//                    Response<T> error = Response.error(false, call, response, HttpException.NET_ERROR());
//                    onError(error);
//                    return;
//                }

                if (onAnalysisResponse(call, response)) return;

                try {
                    T body = request.getConverter().convertResponse(response);
                    if (body != null){
                        //save cache when request is successful
                        saveCache(response.headers(), body);
                        Response<T> success = Response.success(false, body, call, response);
                        globalCallback(success,true);
                        onSuccess(success);
                    }else {
                        Response<T> error = Response.error(false, call, response, HttpException.NET_ERROR(responseCode));
                        globalCallback(error,false);
                        onError(error);
                    }
                } catch (Throwable throwable) {
                    StringBuffer sb = new StringBuffer();
                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    Throwable cause = throwable.getCause();
                    while(cause != null){
                        cause.printStackTrace(printWriter);
                        cause = cause.getCause();
                    }
                    printWriter.close();
                    String result = writer.toString();
                    sb.append(result);
                    Log.e("TestActivity",sb.toString());
                    Response<T> error = Response.error(false, call, response, throwable);
                    globalCallback(error,false);
                    onError(error);
                }
            }
        });
    }

    /**
     * 请求成功后根据缓存模式，更新缓存数据
     *
     * @param headers 响应头
     * @param data    响应数据
     */
    private void saveCache(Headers headers, T data) {
        if (request.getCacheMode() == CacheMode.NO_CACHE) return;    //不需要缓存,直接返回
        if (data instanceof Bitmap) return;             //Bitmap没有实现Serializable,不能缓存

        CacheEntity<T> cache = HeaderParser.createCacheEntity(headers, data, request.getCacheMode(), request.getCacheKey());
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存
            CacheManager.getInstance().remove(request.getCacheKey());
        } else {
            //缓存命中，更新缓存
            CacheManager.getInstance().replace(request.getCacheKey(), cache);
        }
    }

    protected void runOnUiThread(Runnable run) {
        NetHttp.getInstance().getDelivery().post(run);
    }

    @Override
    public Response<T> requestSync(CacheEntity<T> cacheEntity) {
        return null;
    }

    @Override
    public void requestAsync(CacheEntity<T> cacheEntity, Callback<T> callback) {

    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
        if (rawCall != null) {
            rawCall.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        if (canceled) return true;
        synchronized (this) {
            return rawCall != null && rawCall.isCanceled();
        }
    }

    public void globalCallback(Response<T> response , boolean isSuccess){
        if (request != null && request.getAllCallback() != null){
            if (isSuccess){
                request.getAllCallback().onSuccess(response);
            }else {
                request.getAllCallback().onError(response);
            }
        }
    }
}
