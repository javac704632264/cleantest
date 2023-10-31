package com.ai.networklibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ai.networklibrary.cookie.SerializableCookie;


/**
 * Created by hao on 2018/2/26.
 */

public class CookieManager extends BaseDao<SerializableCookie> {
    private static Context context;
    private volatile static CookieManager instance;

    public static CookieManager getInstance() {
        if (instance == null) {
            synchronized (CookieManager.class) {
                if (instance == null) {
                    instance = new CookieManager();
                }
            }
        }
        return instance;
    }

    private CookieManager() {
        super(new DBCacheHelper(context));
    }

    public static void init(Context ctx) {
        context = ctx;
    }

    @Override
    public SerializableCookie parseCursorToBean(Cursor cursor) {
        return SerializableCookie.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(SerializableCookie serializableCookie) {
        return SerializableCookie.getContentValues(serializableCookie);
    }

    @Override
    public String getTableName() {
        return DBCacheHelper.TABLE_COOKIE;
    }

    @Override
    public void unInit() {
    }
}
