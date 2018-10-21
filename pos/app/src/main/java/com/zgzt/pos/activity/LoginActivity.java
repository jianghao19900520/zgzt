package com.zgzt.pos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.landicorp.android.eptapi.utils.SystemInfomation;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.http.UrlConfig;
import com.zgzt.pos.node.User;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.ToastUtils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private EditText login_cashier_input;//输入收银员账号
    private ImageView login_cashier_more_btn;//选择收银员
    private EditText login_password_input;//输入收银员密码
    private TextView login_btn;//登录

    private String[] users;
    private String cashier;//输入的账号
    private String password;//输入的密码

    private QMUITipDialog tipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
        initDialog(getString(R.string.login_hint));
    }

    private void initView() {
        login_cashier_input = findViewById(R.id.login_cashier_input);
        login_cashier_more_btn = findViewById(R.id.login_cashier_more_btn);
        login_password_input = findViewById(R.id.login_password_input);
        login_btn = findViewById(R.id.login_btn);
        login_cashier_input.setOnClickListener(this);
        login_cashier_more_btn.setOnClickListener(this);
        login_password_input.setOnClickListener(this);
        login_btn.setOnClickListener(this);
    }

    /**
     * 登录对话框
     */
    private void initDialog(String message) {
        tipDialog = new QMUITipDialog.Builder(LoginActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(message)
                .create();
        tipDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_shop_code_more_btn:
                break;
            case R.id.login_cashier_more_btn:
                new QMUIDialog.MenuDialogBuilder(this)
                        .addItems(users, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                login_cashier_input.setText(users[which]);
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.login_btn:
                if (checkedCashier() && checkedPassword()) {
                    getToken();
                }
                break;
        }
    }

    /**
     * 检测收银员输入
     *
     * @return false 输入有误，true 通过检测
     */
    private final boolean checkedCashier() {
        cashier = login_cashier_input.getText().toString().trim();
        if (TextUtils.isEmpty(cashier)) {
            ToastUtils.showShort(BaseApplication.mContext, "请先输入/选择收银员");
            return false;
        }
        return true;
    }

    /**
     * 检测收银员输入
     *
     * @return false 输入有误，true 通过检测
     */
    private final boolean checkedPassword() {
        password = login_password_input.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showShort(BaseApplication.mContext, "请先输入密码");
            return false;
        }
        return true;
    }

    /**
     * 获取token
     */
    private void getToken() {
        tipDialog.show();
        HttpApi.getToken(cashier, password, new HttpCallback() {
            @Override
            public void onResponse(String result) {
                JSONObject data = JSONObject.parseObject(result);
                int code = data.getInteger("code");
                if (code == 0) {
                    PreferencesUtil.getInstance(mContext).putString(Constant.TOKEN, data.getString("access_token"));
                    login();
                } else {
                    if ((null != tipDialog) && tipDialog.isShowing()) {
                        tipDialog.dismiss();
                    }
                    ToastUtils.showShort(BaseApplication.mContext, data.getString("message"));
                }
            }

            @Override
            public void onFailure(IOException e) {
                if ((null != tipDialog) && tipDialog.isShowing()) {
                    tipDialog.dismiss();
                }
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
        HttpApi.login(SystemInfomation.getDeviceInfo().getSerialNo(), new HttpCallback() {
            @Override
            public void onResponse(String result) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                int code = jsonObject.getInteger("code");
                if (code == 0) {
                    JSONObject object = jsonObject.getJSONObject("result");
                    PreferencesUtil.getInstance(mContext).putString(Constant.USER_ID, object.getString("userId"));
                    PreferencesUtil.getInstance(mContext).putString(Constant.LOGIN_NAME, object.getString("loginName"));
                    PreferencesUtil.getInstance(mContext).putString(Constant.WAREHOUSE_ID, object.getString("warehouseId"));
                    PreferencesUtil.getInstance(mContext).putString(Constant.WAREHOUSE_NAME, object.getString("warehouseName"));
                    goMainActivity();
                }else{
                    ToastUtils.showShort(BaseApplication.mContext, jsonObject.getString("message"));
                }
                if ((null != tipDialog) && tipDialog.isShowing()) {
                    tipDialog.dismiss();
                }
            }

            @Override
            public void onFailure(IOException e) {
                if ((null != tipDialog) && tipDialog.isShowing()) {
                    tipDialog.dismiss();
                }
            }
        });
    }

    /**
     * 进入主页
     */
    private final void goMainActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
