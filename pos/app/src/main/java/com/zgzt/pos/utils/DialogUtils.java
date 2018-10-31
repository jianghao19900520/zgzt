package com.zgzt.pos.utils;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.zgzt.pos.R;
import com.zgzt.pos.activity.LoginActivity;
import com.zgzt.pos.base.BaseApplication;

/**
 * Created by zixing
 * Date 2018/10/30.
 * desc ：
 */

public class DialogUtils {

    private static DialogUtils mInstance;
    private static QMUITipDialog tipDialog;

    private DialogUtils() {

    }

    public static DialogUtils getInstance() {
        if (null == mInstance) {
            synchronized (DialogUtils.class) {
                if (null == mInstance) {
                    mInstance = new DialogUtils();
                }
            }
        }
        return mInstance;
    }

    public void show(Context context) {
        dismiss();
        tipDialog = null;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(context.getString(R.string.loading_hint))
                .create();
        tipDialog.setCancelable(false);
        tipDialog.show();
    }

    public void show(Context context, String tipWord) {
        dismiss();
        tipDialog = null;
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(tipWord)
                .create();
        tipDialog.setCancelable(true);
        tipDialog.show();
    }

    public void dismiss() {
        if ((null != tipDialog) && tipDialog.isShowing()) {
            tipDialog.dismiss();
        }
    }

}
