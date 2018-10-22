package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIFloatLayout;
import com.zgzt.pos.R;

/**
 * 库存筛选页面
 */
public class StockFilterActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private LayoutInflater inflater;
    private String whId;
    private String whName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_filter);
        mContext = this;
        inflater = LayoutInflater.from(mContext);
        initView();
        initData();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private void initView(){
        findViewById(R.id.close_btn).setOnClickListener(this);
        findViewById(R.id.area_btn).setOnClickListener(this);
        findViewById(R.id.warehouse_btn).setOnClickListener(this);
        findViewById(R.id.affirm_btn).setOnClickListener(this);
    }

    private void initData(){
        Intent intent = getIntent();
        whId = intent.getStringExtra("whId");
        whName = intent.getStringExtra("whName");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_btn:
                finish();
                break;
            case R.id.area_btn:
                break;
            case R.id.warehouse_btn:
                break;
            case R.id.affirm_btn:
                //返回数据
                finish();
                break;
        }
    }
}

