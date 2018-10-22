package com.zgzt.pos;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bill99.smartpos.sdk.api.BillPayment;
import com.example.ExceptionUtils.MyErrorHandler;

public class BaseApplication extends Application {
    public static BaseApplication mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initExceptionLog();
        initPaySDK();
    }

    /**
     * 初始化错误日志收集
     */
    private void initExceptionLog(){
        MyErrorHandler me = MyErrorHandler.getInstance();
        me.init();
    }

    /**
     * 初始化支付SDK
     */
    private void initPaySDK(){
        //设置支付SDK调试模式，建议开发版本设为true, 发布版本设为false。默认为false
        BillPayment.startUp(this,"sandbox");
        BillPayment.setDebugMode(false);
        BillPayment.setChannelType("spos");
    }

}
