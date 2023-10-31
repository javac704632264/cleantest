package com.ai.networklibrary.interceptor;


import androidx.annotation.IntDef;

import com.ai.networklibrary.BuildConfig;
import com.ai.networklibrary.utils.IOUtils;
import com.ai.networklibrary.utils.NetLogger;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

/**
 * Created by hao on 2018/2/26.
 */

public class HttpLoggingInterceptor implements Interceptor {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private volatile Level printLevel = Level.NONE;
    private int mPriority;

    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY        //所有数据全部打印
    }

    @IntDef({VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Priority {
    }

    public HttpLoggingInterceptor(String tag) {
        Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(0)
                .tag(tag)
                .build()));
    }

    public void setPrintLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        printLevel = level;
    }

    public void setPriority(@Priority int priority) {
        mPriority = priority;
    }

    private void log(String message, StringBuilder stringBuilder) {
        if (BuildConfig.DEBUG) {
            if (stringBuilder != null) {
                stringBuilder.append(message);
                stringBuilder.append("\n");
            }
        }
    }

    private void printLog(StringBuilder stringBuilder) {
        if (BuildConfig.DEBUG) {
            if (stringBuilder != null) {
                Logger.log(mPriority, "", stringBuilder.toString(), null);
            }
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        Request request = chain.request();
        if (printLevel == Level.NONE) {
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection(), stringBuilder);

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        String url = request.url().toString();
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- " + url, stringBuilder);
            log("<-- HTTP FAILED: " + e, stringBuilder);
            printLog(stringBuilder);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs, stringBuilder);
    }

    private void logForRequest(Request request, Connection connection, StringBuilder stringBuilder) throws IOException {
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            log(requestStartMessage, stringBuilder);

            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody.contentType() != null) {
                        log("\tContent-Type: " + requestBody.contentType(), stringBuilder);
                    }
                    if (requestBody.contentLength() != -1) {
                        log("\tContent-Length: " + requestBody.contentLength(), stringBuilder);
                    }
                }
                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        log("\t" + name + ": " + headers.value(i), stringBuilder);
                    }
                }

                log(" ", stringBuilder);
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody.contentType())) {
                        bodyToString(request, stringBuilder);
                    } else {
                        log("\tbody: maybe [binary body], omitted!", stringBuilder);
                    }
                }
            }
        } catch (Exception e) {
            NetLogger.printStackTrace(e);
        } finally {
            log("--> END " + request.method(), stringBuilder);
            printLog(stringBuilder);
            stringBuilder.setLength(0);
        }
    }

    private Response logForResponse(Response response, long tookMs, StringBuilder stringBuilder) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);

        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）", stringBuilder);
            if (logHeaders) {
                Headers headers = clone.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    log("\t" + headers.name(i) + ": " + headers.value(i), stringBuilder);
                }
                log(" ", stringBuilder);
                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (responseBody == null) return response;

                    if (isPlaintext(responseBody.contentType())) {
                        byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
                        MediaType contentType = responseBody.contentType();
                        String body = new String(bytes, getCharset(contentType));
                        log("\tbody:" + body, stringBuilder);
                        responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                        return response.newBuilder().body(responseBody).build();
                    } else {
                        log("\tbody: maybe [binary body], omitted!", stringBuilder);
                    }
                }
            }
        } catch (Exception e) {
            NetLogger.printStackTrace(e);
        } finally {
            log("<-- END HTTP", stringBuilder);
            printLog(stringBuilder);
        }
        return response;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request, StringBuilder stringBuilder) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            log("\tbody:" + buffer.readString(charset), stringBuilder);
        } catch (Exception e) {
            NetLogger.printStackTrace(e);
        }
    }
}
