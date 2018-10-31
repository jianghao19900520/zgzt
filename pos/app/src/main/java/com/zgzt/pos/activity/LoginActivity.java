package com.zgzt.pos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.landicorp.android.eptapi.utils.SystemInfomation;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.DialogUtils;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.ToastUtils;

import java.io.IOException;

/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private EditText login_cashier_input;//输入收银员账号
    private ImageView login_cashier_more_btn;//选择收银员
    private EditText login_password_input;//输入收银员密码
    private TextView login_btn;//登录

    private String[] users;
    private String userStrs;
    private String cashier;//输入的账号
    private String password;//输入的密码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
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
     * 初始化数据
     */
    private void initData() {
        userStrs = PreferencesUtil.getInstance(BaseApplication.mContext).getString(Constant.USERS);
        if (userStrs.length() > 0) {
            login_cashier_more_btn.setVisibility(View.VISIBLE);
            users = userStrs.split(",");
            login_cashier_input.setText(users[0]);
        } else {
            login_cashier_more_btn.setVisibility(View.GONE);
        }
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
        DialogUtils.getInstance().show(mContext, getString(R.string.login_hint));
        HttpApi.getToken(cashier, password, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                JSONObject data = JSONObject.parseObject(String.valueOf(result));
                int code = data.getInteger("code");
                if (code == 0) {
                    PreferencesUtil.getInstance(mContext).putString(Constant.TOKEN, data.getString("access_token"));
                    login();
                } else {
                    DialogUtils.getInstance().dismiss();
                    ToastUtils.showShort(BaseApplication.mContext, data.getString("message"));
                }
            }

            @Override
            public void onFailure(IOException e) {
                DialogUtils.getInstance().dismiss();
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
        HttpApi.login(SystemInfomation.getDeviceInfo().getSerialNo(), new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                JSONObject jsonObject = JSONObject.parseObject(String.valueOf(result));
                int code = jsonObject.getInteger("code");
                if (code == 0) {
                    JSONObject object = jsonObject.getJSONObject("result");
                    if (object == null) {
                        ToastUtils.showShort(BaseApplication.mContext, jsonObject.getString("message"));
                    } else {
                        PreferencesUtil.getInstance(mContext).putString(Constant.USER_ID, object.getString("userId"));
                        PreferencesUtil.getInstance(mContext).putString(Constant.LOGIN_NAME, object.getString("loginName"));
                        PreferencesUtil.getInstance(mContext).putString(Constant.WAREHOUSE_ID, object.getString("warehouseId"));
                        PreferencesUtil.getInstance(mContext).putString(Constant.WAREHOUSE_NAME, object.getString("warehouseName"));
                        goMainActivity();
                    }
                } else {
                    ToastUtils.showShort(BaseApplication.mContext, jsonObject.getString("message"));
                }
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onFailure(IOException e) {
                DialogUtils.getInstance().dismiss();
            }
        });
    }

    /**
     * 进入主页
     */
    private final void goMainActivity() {
        saveUser();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    /**
     * 保存登录记录
     */
    private void saveUser() {
        if (userStrs.length() > 0) {
            String cacheStr = userStrs + ",";
            if (cacheStr.indexOf(cashier + ",") > -1) {
                cacheStr = cacheStr.replace(cashier + ",", "");
                if (cacheStr.endsWith(",")) {
                    cacheStr = cacheStr.substring(0, cacheStr.length() - 1);
                }
                PreferencesUtil.getInstance(BaseApplication.mContext).putString(Constant.USERS, cashier + "," + cacheStr);
            } else {
                PreferencesUtil.getInstance(BaseApplication.mContext).putString(Constant.USERS, cashier + "," + userStrs);
            }
        } else {
            PreferencesUtil.getInstance(BaseApplication.mContext).putString(Constant.USERS, cashier);
        }
    }
}
