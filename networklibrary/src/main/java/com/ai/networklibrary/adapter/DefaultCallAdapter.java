package com.ai.networklibrary.adapter;

/**
 * Created by hao on 2018/2/26.
 */

public class DefaultCallAdapter<T> implements CallAdapter<T,Call<T>> {
    @Override
    public Call<T> adapt(Call<T> call, AdapterParam param) {
        return call;
    }
}
