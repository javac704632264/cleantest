package com.ai.networklibrary.convert;

import okhttp3.Response;

/**
 * Created by hao on 2018/2/26.
 */

public interface Converter<T> {
    /**
     * 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     *
     * @param response 需要转换的对象
     * @return 转换后的结果
     * @throws Exception 转换过程发生的异常
     */
    T convertResponse(Response response) throws Throwable;
}
