package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgzt.pos.R;
import com.zgzt.pos.view.ScaleRelativeLayout;

/**
 * 商品调拨页面
 */
public class GoodsAllocationActivity extends AppCompatActivity implements ScaleRelativeLayout.OnClickListener {

    private Context mContext;
    ImageView title_back_btn;
    TextView title_text;
    ScaleRelativeLayout add_stock_btn;
    ScaleRelativeLayout out_stock_btn;
    ScaleRelativeLayout in_stock_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_allocation);
        mContext = this;
        initView();
        initTitle();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_back_btn = findViewById(R.id.title_back_btn);
        title_text = findViewById(R.id.title_text);
        add_stock_btn = findViewById(R.id.add_stock_btn);
        out_stock_btn = findViewById(R.id.out_stock_btn);
        in_stock_btn = findViewById(R.id.in_stock_btn);
        add_stock_btn.setOnClickListener(this);
        out_stock_btn.setOnClickListener(this);
        in_stock_btn.setOnClickListener(this);
    }

    /**
     * 初始化标题栏
     */
    public void initTitle() {
        title_back_btn.setVisibility(View.VISIBLE);
        title_text.setText("商品调拨");
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.add_stock_btn:
                intent = new Intent(this, AddNewAllotActivity.class);
                break;
            case R.id.out_stock_btn:
                intent = new Intent(this, StockAllotActivity.class);
                intent.putExtra("type", "out");
                break;
            case R.id.in_stock_btn:
                intent = new Intent(this, StockAllotActivity.class);
                intent.putExtra("type", "in");
                break;
            default:
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
