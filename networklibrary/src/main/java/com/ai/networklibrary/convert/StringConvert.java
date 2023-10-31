package com.ai.networklibrary.convert;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by hao on 2018/2/26.
 */

public class StringConvert implements Converter<String> {
    @Override
    public String convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) return null;
        return body.string();
    }
}
