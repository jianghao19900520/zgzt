package cn.com.test;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class HttpAPi {
    private static HttpAPi mInstance = null;

    public synchronized static HttpAPi getInstance() {
        if (mInstance == null) {
            mInstance = new HttpAPi();
        }
        return mInstance;
    }

    public void requestRedTypeList(final OnHttpCallBack<Object> cb) {
        AjaxParams params = new AjaxParams();
        params.put("pointofsalesCode", "1801CA889956");
        FinalHttp fh = new FinalHttp();
//        System.out.println("http://www.yangfuhai.com?"+params.toString());
        fh.post("http://api.2025123.com.cn/business_xyml/services/pointofsales/pointofsaleslogin", params, new AjaxCallBack() {

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                System.out.println("test:onSuccess");
                System.out.println("test:onSuccess"+String.valueOf(o));
                cb.onSuccess(o);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                System.out.println("test:onFailure"+strMsg);
            }

        });
    }

}
