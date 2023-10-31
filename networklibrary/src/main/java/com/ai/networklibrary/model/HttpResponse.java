package com.ai.networklibrary.model;

import java.io.Serializable;

public class HttpResponse<T> implements Serializable {
    private static final long serialVersionUID = 373351010020740820L;

    public int result;
    public String desc;
    public T data;
    public String code;

    @Override
    public String toString() {
        return "HttpResponse{" +
                "result=" + result +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}';
    }
}
