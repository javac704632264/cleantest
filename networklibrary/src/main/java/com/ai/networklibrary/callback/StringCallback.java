package com.ai.networklibrary.callback;
import com.ai.networklibrary.convert.StringConvert;
import com.ai.networklibrary.request.base.Request;

import okhttp3.Response;

/**
 * Created by hao on 2018/2/26.
 */

public abstract class StringCallback extends AbsCallback<String> {
    private StringConvert convert;

    public StringCallback() {
        convert = new StringConvert();
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
//        request.headers(STRING.COOK, CookieUtils.getCook());
    }

    @Override
    public String convertResponse(Response response) throws Throwable {
        String s = convert.convertResponse(response);
        response.close();
        return s;
    }
}
