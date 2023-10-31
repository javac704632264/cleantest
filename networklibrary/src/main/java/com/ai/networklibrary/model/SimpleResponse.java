package com.ai.networklibrary.model;

import java.io.Serializable;

public class SimpleResponse implements Serializable {
    private static final long serialVersionUID = -1685565634700086057L;

    public HttpResponse toHttpResponse(){
        HttpResponse httpResponse = new HttpResponse();
        return httpResponse;
    }
}
