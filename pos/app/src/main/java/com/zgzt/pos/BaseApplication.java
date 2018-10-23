package com.zgzt.pos;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bill99.smartpos.sdk.api.BillPayment;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ExceptionUtils.MyErrorHandler;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.zgzt.pos.utils.DynamicTimeFormat;

public class BaseApplication extends Application {
    public static BaseApplication mContext;
    //glide默认配置
    public static RequestOptions options110 = new RequestOptions().centerInside()
            .dontAnimate()
            .override(400, 400)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.bg_1)
            .error(R.drawable.bg_1);

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
        initRefresh();
    }

    /**
     * 初始化错误日志收集
     */
    private void initExceptionLog() {
        MyErrorHandler me = MyErrorHandler.getInstance();
        me.init();
    }

    /**
     * 初始化支付SDK
     */
    private void initPaySDK() {
        //设置支付SDK调试模式，建议开发版本设为true, 发布版本设为false。默认为false
        BillPayment.startUp(this, "sandbox");
        BillPayment.setDebugMode(false);
        BillPayment.setChannelType("spos");
    }

    /**
     * 初始化下拉刷新框架
     */
    private void initRefresh() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.primaryColor, R.color.color_33);//全局设置主题颜色
                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s")).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

}
