package com.zgzt.pos.utils;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by zixing
 * Date 2018/10/21.
 * desc ：
 */

public class ToastUtils {

    public static void showShort(Application context, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showLong(Application context, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }

}
