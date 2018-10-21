package com.zgzt.pos.http;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSONObject;
import com.landicorp.android.eptapi.utils.SystemInfomation;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.utils.LogUtils;
import com.zgzt.pos.utils.PreferencesUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zixing
 * Date 2018/10/21.
 * desc ：
 */

public class HttpApi {

    public static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 获取token
     */
    public static void getToken(String username, String password, final HttpCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .add("client_id", "5QEYi73nj5sOwoRTQMI5RjA6kX3u9imYAu84BAHVcRR0AunXhJ9x0RCH")
                .add("client_secret", "zg900.COM")
                .add("flag", "1")
                .build();
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.TOKEN_URL)
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = response.body().string();
                            LogUtils.json(result);
                            callback.onResponse(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 登录
     */
    public static void login(String pointofsalesCode, final HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("pointofsalesCode", pointofsalesCode);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.LOGIN)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = response.body().string();
                            LogUtils.json(result);
                            callback.onResponse(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}
