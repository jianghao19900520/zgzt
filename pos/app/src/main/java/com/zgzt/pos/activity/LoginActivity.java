package com.zgzt.pos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.http.UrlConfig;
import com.zgzt.pos.utils.ToastUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;

    private EditText login_cashier_input;//输入收银员账号
    private ImageView login_cashier_more_btn;//选择收银员
    private EditText login_password_input;//输入收银员密码
    private TextView login_btn;//登录

    private String[] users;
    private String cashier;//输入的账号
    private String password;//输入的密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
    }

    private void initView(){
        login_cashier_input = findViewById(R.id.login_cashier_input);
        login_cashier_more_btn = findViewById(R.id.login_cashier_more_btn);
        login_password_input = findViewById(R.id.login_password_input);
        login_btn = findViewById(R.id.login_btn);
        login_cashier_input.setOnClickListener(this);
        login_cashier_more_btn.setOnClickListener(this);
        login_password_input.setOnClickListener(this);
        login_btn.setOnClickListener(this);
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
        String url = UrlConfig.BASE_URL + "oauth2/token?grant_type=password" +
                "&username=" + cashier +
                "&password=" + password +
                "&client_id=5QEYi73nj5sOwoRTQMI5RjA6kX3u9imYAu84BAHVcRR0AunXhJ9x0RCH" +
                "&client_secret=zg900.COM" +
                "&flag=1";
    }
}
