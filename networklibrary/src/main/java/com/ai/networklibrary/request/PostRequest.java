package com.ai.networklibrary.request;

import android.util.Log;

import com.ai.networklibrary.model.HttpMethod;
import com.ai.networklibrary.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hao on 2018/2/26.
 */

public class PostRequest<T> extends BodyRequest<T,PostRequest<T>> {
    public PostRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.post(requestBody).url(url).tag(tag).build();
    }
}
