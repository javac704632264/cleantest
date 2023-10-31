package com.ai.networklibrary.helper;


import com.google.gson.stream.JsonReader;
import com.ai.networklibrary.gson.GsonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class JsonHelper<T> {
    private Type type;
    public JsonHelper(){
        Type genType = getClass().getGenericSuperclass();
        type = ((ParameterizedType) genType).getActualTypeArguments()[0];
    }

    public T getJsonObject(String msg){
        try {
            T bean = GsonUtils.getInstance().fromJson(msg,type);//JSON.parseObject(msg, type);
            return bean;
        }catch (Exception e){}
        return null;
    }

    public T getJsonObject(JsonReader jsonReader){
        try {
            T bean = GsonUtils.getInstance().fromJson(jsonReader,type);//JSON.parseObject(msg, type);
            return bean;
        }catch (Exception e){}
        return null;
    }
}
