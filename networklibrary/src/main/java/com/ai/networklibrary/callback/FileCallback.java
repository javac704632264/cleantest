package com.ai.networklibrary.callback;

import com.ai.networklibrary.convert.FileConvert;

import java.io.File;

import okhttp3.Response;

/**
 * Created by hao on 2018/2/26.
 */

public abstract class FileCallback extends AbsCallback<File>{
    private FileConvert convert;    //文件转换类

    public FileCallback() {
        this(null);
    }

    public FileCallback(String destFileName) {
        this(null, destFileName);
    }

    public FileCallback(String destFileDir, String destFileName) {
        convert = new FileConvert(destFileDir, destFileName);
        convert.setCallback(this);
    }

    @Override
    public File convertResponse(Response response) throws Throwable {
        File file = convert.convertResponse(response);
        response.close();
        return file;
    }
}
