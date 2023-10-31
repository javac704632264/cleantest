package com.ai.networklibrary.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 异常信息处理工具类
 */
public class ExceptionUtils {

    /**
     * 获取异常信息
     * @param throwable
     * @return
     */
    public static String getExceptionMessage(Throwable throwable){
        if (throwable == null){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while(cause != null){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }
}
