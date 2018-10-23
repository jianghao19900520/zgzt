package com.zgzt.pos.http;

import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.node.PayMangerNode;
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

    public static Handler mHandler = new Handler();


    /**
     * 获取token
     *
     * @param username 用户名
     * @param password 密码
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
        LogUtils.json(requestBody.toString());
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
                final String result = response.body().string();
                LogUtils.json(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(result);
                    }
                });
            }
        });
    }

    /**
     * 登录
     *
     * @param pointofsalesCode 设备识别码
     */
    public static void login(String pointofsalesCode, final HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("pointofsalesCode", pointofsalesCode);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.LOGIN_URL)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
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
                final String result = response.body().string();
                LogUtils.json(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
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
     * 支付信息列表
     *
     * @param memberId           会员id
     * @param statisticsType     1-会员 2-门店
     * @param statisticsTimeType 1-本月,2-上月,3-自定义
     */
    public static void payList(String memberId, int statisticsType, int statisticsTimeType, final HttpCallback<PayMangerNode> callback) {
        JSONObject json = new JSONObject();
        json.put("memberId", memberId);
        json.put("statisticsType", statisticsType);
        json.put("statisticsTimeType", statisticsTimeType);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.PAY_LIST_URL)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
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
                final String result = response.body().string();
                LogUtils.json(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PayMangerNode node = JSON.parseObject(result, new TypeReference<PayMangerNode>() {
                            });
                            callback.onResponse(node);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取导购员列表
     *
     * @param warehouseId
     */
    public static void getClerkData(String warehouseId, final HttpCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.DAY_PAY_CLERK + warehouseId)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .get()
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
                final String result = response.body().string();
                LogUtils.json(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(result);
                    }
                });
            }
        });
    }

    /**
     * 支付明细
     *
     * @param storeId         门店id
     * @param createdTimeFrom 开始时间
     * @param createdTimeTo   结束时间
     * @param shoppingGuideId 导购id
     * @param payType         支付方式 0赊账 1资金支付 2微信支付 3支付宝支付 4购物卡支付 5积分支付
     */
    public static void getPayDayDetailedList(String storeId, String createdTimeFrom, String createdTimeTo, String shoppingGuideId, String payType, final HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("memberId", storeId);
        json.put("createdTimeFrom", createdTimeFrom);
        json.put("createdTimeTo", createdTimeTo);
        if (!TextUtils.isEmpty(shoppingGuideId)) json.put("shoppingGuideId", shoppingGuideId);
        if (!TextUtils.isEmpty(payType)) json.put("payType", payType);
        json.put("orderSource", 5);// 订单来源(1-pc订单,2-h5订单,3-app,4-b2c线下,5-pos机)
        json.put("isStoreOrder", 1);// 是否查询门店订单(0- 否, 1- 是)
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.DAY_PAY_DETAILED_LIST)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
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
                final String result = response.body().string();
                //LogUtils.json(result);//该接口返回数据太大，所以先不打印出来了
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(result);
                    }
                });
            }
        });
    }

    /**
     * 支付明细
     * @param pageIndex
     * @param pageSize
     * @param whId
     */
    public static void stocksearchlist(int pageIndex, int pageSize, String whId, final HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("pageIndex", pageIndex);
        json.put("pageSize", pageSize);
        json.put("whId", whId);
        json.put("sortType", 1);
        json.put("searchKey", "");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.SEARCH_STOCK_LIST)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
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
                final String result = response.body().string();
                LogUtils.json(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(result);
                    }
                });
            }
        });
    }

}
