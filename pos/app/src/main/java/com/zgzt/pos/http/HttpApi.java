package com.zgzt.pos.http;

import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.node.PayMangerNode;
import com.zgzt.pos.utils.LogUtils;
import com.zgzt.pos.utils.PreferencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

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
        FormBody.Builder builder = new FormBody.Builder();
        JSONObject json = new JSONObject();
        try {
            json.put("grant_type", "password");
            json.put("username", username);
            json.put("password", password);
            json.put("client_id", "5QEYi73nj5sOwoRTQMI5RjA6kX3u9imYAu84BAHVcRR0AunXhJ9x0RCH");
            json.put("client_secret", "zg900.COM");
            json.put("flag", "1");
            Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                builder.add(key, json.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = builder.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.TOKEN_URL)
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
        try {
            json.put("pointofsalesCode", pointofsalesCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.LOGIN_URL)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
        try {
            json.put("memberId", memberId);
            json.put("statisticsType", statisticsType);
            json.put("statisticsTimeType", statisticsTimeType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.PAY_INFO_LIST)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
     * 查询会员
     *
     * @param userinput
     */
    public static void getVipData(String operatorNo, String userinput, final HttpCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.SEARCH_VIP_LIST + operatorNo + "/" + userinput)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .get()
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
                final String data = result.replace("null", "\"\"");
                LogUtils.json(data);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(data);
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
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
        try {
            json.put("memberId", storeId);
            json.put("createdTimeFrom", createdTimeFrom);
            json.put("createdTimeTo", createdTimeTo);
            if (!TextUtils.isEmpty(shoppingGuideId)) json.put("shoppingGuideId", shoppingGuideId);
            if (!TextUtils.isEmpty(payType)) json.put("payType", payType);
            json.put("orderSource", 5);// 订单来源(1-pc订单,2-h5订单,3-app,4-b2c线下,5-pos机)
            json.put("isStoreOrder", 1);// 是否查询门店订单(0- 否, 1- 是)
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.DAY_PAY_DETAILED_LIST)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
     * 商品搜索
     *
     * @param pageIndex
     * @param pageSize
     * @param whId
     */
    public static void searchGood(int pageIndex, int pageSize, String whId, String searchKey, final HttpCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("pageIndex", pageIndex);
            json.put("pageSize", pageSize);
            json.put("whId", whId);
            json.put("sortType", 1);
            json.put("searchKey", searchKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.SEARCH_GOODS)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
     * 提交订单
     *
     * @param orderBelong        // 1-普通订单，2-赊账订单
     * @param confirmPosProducts // pos机订单商品
     * @param shoppingGuideId    // 导购Id
     * @param shoppingGuideName  // 导购名称
     * @param shoppingGuideId    // 导购Id
     * @param cashierId          // 收银Id
     * @param cashierName        // 收银名称
     * @param isBackFinance      // 是否返资金 0-是，1-否
     * @param isBackIntegral     // 是否返积分 0-是，1-否
     * @param memberId           // 用户id
     * @param operatorNo         // 运营商号
     */
    public static void confirmorder(String orderBelong, JSONArray confirmPosProducts, String shoppingGuideId, String shoppingGuideName,
                                    String cashierId, String cashierName, String isBackFinance, String isBackIntegral, String memberId, String operatorNo, final HttpCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderSource", 6);// 订单来源(1-pc订单,2-h5订单,3-app,4-线下,5-pos机, 6-新)
            json.put("orderType", 4);// 订单类型1运营商订单2代理商3微商城线下订单4微商城订单
            json.put("orderBelong", orderBelong);
            json.put("orderMicroShopType", 0);// 订单微店类型 0_交易订单 1_注册购买商品订单 2_会员续费订单 3_升级订单 4_积分商城订单
            json.put("confirmPosProducts", confirmPosProducts);
            json.put("shoppingGuideId", shoppingGuideId);
            json.put("shoppingGuideName", shoppingGuideName);
            json.put("cashierId", cashierId);
            json.put("cashierName", cashierName);
            json.put("isBackFinance", isBackFinance);
            json.put("isBackIntegral", isBackIntegral);
            json.put("memberId", memberId);
            json.put("orderDeliveryMode", 1);// 配送方式0送货上门1自取
            json.put("code", operatorNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.CONFIR_MORDER)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .post(requestBody)
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.json(json.toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
     * 获取商品详情
     */
    public static void getGoodsInfo(String productId, String memberId, final HttpCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UrlConfig.BASE_URL + UrlConfig.GOODS_INFO + "?productId=" + productId + "&memberId=" + memberId)
                .addHeader("token", PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN))
                .get()
                .build();
        LogUtils.json(request.url().toString());
        LogUtils.d("token-->" + PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.TOKEN));
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
