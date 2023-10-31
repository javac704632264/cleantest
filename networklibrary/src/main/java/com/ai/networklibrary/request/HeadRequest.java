package com.ai.networklibrary.request;

import com.ai.networklibrary.model.HttpMethod;
import com.ai.networklibrary.request.base.NoBodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hao on 2018/2/26.
 */

public class HeadRequest<T> extends NoBodyRequest<T,HeadRequest<T>> {
    public HeadRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.HEAD;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.head().url(url).tag(tag).build();
    }
}
