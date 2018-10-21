package com.zgzt.pos.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgzt.pos.R;

public class LoginActivity extends AppCompatActivity {

    private Context mContext;
    private EditText login_cashier_input;
    private ImageView login_cashier_more_btn;
    private EditText login_password_input;
    private TextView login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
    }

    private void initView(){
        login_cashier_input = findViewById(R.id.login_cashier_input);
    }
    
}
