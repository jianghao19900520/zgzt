package com.zgzt.pos.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zixing
 * Date 2018/10/21.
 * desc ：
 */

public interface HttpCallback<T> {
    void onResponse(T result);
    void onFailure(IOException e);
}
