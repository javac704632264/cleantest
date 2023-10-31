package com.ai.networklibrary.request;

import com.ai.networklibrary.model.HttpMethod;
import com.ai.networklibrary.request.base.BodyRequest;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hao on 2018/2/26.
 */

public class DeleteRequest<T> extends BodyRequest<T,DeleteRequest<T>> {
    public DeleteRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = generateRequestBuilder(requestBody);
        return requestBuilder.delete(requestBody).url(url).tag(tag).build();
    }
}
