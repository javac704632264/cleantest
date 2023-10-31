package com.ai.networklibrary.request;

import com.ai.networklibrary.model.HttpMethod;
import com.ai.networklibrary.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hao on 2018/2/26.
 */

public class OptionsRequest<T> extends BodyRequest<T,OptionsRequest<T>> {
    public OptionsRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.OPTIONS;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.method("OPTIONS", requestBody).url(url).tag(tag).build();
    }
}
