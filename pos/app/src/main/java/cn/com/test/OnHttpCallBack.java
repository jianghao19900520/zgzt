package cn.com.test;

/**
 * Created by jason on 2016/9/13.
 */
public interface OnHttpCallBack<T> {

    void onSuccess(T t);

    void onFail(int httpCode, int statusCode, String msg);

}
