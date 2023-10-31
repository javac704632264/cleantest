package com.ai.networklibrary.exception;

/**
 * Created by hao on 2018/2/26.
 */

public class NetHttpException extends Exception {
    private static final long serialVersionUID = -8641198158155821498L;

    public NetHttpException(String detailMessage) {
        super(detailMessage);
    }

    public static NetHttpException UNKNOWN() {
        return new NetHttpException("unknown exception!");
    }

    public static NetHttpException BREAKPOINT_NOT_EXIST() {
        return new NetHttpException("breakpoint file does not exist!");
    }

    public static NetHttpException BREAKPOINT_EXPIRED() {
        return new NetHttpException("breakpoint file has expired!");
    }
}
