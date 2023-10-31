package com.ai.networklibrary.request.base;

import com.ai.networklibrary.utils.HttpUtils;

import okhttp3.RequestBody;

/**
 * Created by hao on 2018/2/26.
 */

public abstract class NoBodyRequest<T, R extends NoBodyRequest> extends Request<T, R>  {
    private static final long serialVersionUID = 1200621102761691196L;

    public NoBodyRequest(String url) {
        super(url);
    }

    @Override
    public RequestBody generateRequestBody() {
        return null;
    }

    protected okhttp3.Request.Builder generateRequestBuilder(RequestBody requestBody) {
        url = HttpUtils.createUrlFromParams(baseUrl, params.urlParamsMap);
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        return HttpUtils.appendHeaders(requestBuilder, headers);
    }
}
