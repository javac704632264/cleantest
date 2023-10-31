package com.ai.networklibrary.adapter;

/**
 * Created by hao on 2018/2/26.
 */

public interface CallAdapter<T,R> {
    /** call执行的代理方法 */
    R adapt(Call<T> call, AdapterParam param);
}
