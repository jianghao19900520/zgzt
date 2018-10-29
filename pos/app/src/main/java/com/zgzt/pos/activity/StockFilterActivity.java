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
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.event.WarehouseEvent;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 库存筛选页面
 */
public class StockFilterActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    TextView warehouse;
    ImageView warehouse_img;
    QMUIFloatLayout warehouse_layout;

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
        initTitle();
        initData();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private void initView(){
        warehouse = findViewById(R.id.warehouse);
        warehouse_img = findViewById(R.id.warehouse_img);
        warehouse_layout = findViewById(R.id.warehouse_layout);
        findViewById(R.id.close_btn).setOnClickListener(this);
        findViewById(R.id.area_btn).setOnClickListener(this);
        findViewById(R.id.warehouse_btn).setOnClickListener(this);
        findViewById(R.id.affirm_btn).setOnClickListener(this);
    }

    public void initTitle() {
        Intent intent = getIntent();
        whId = intent.getStringExtra("whId");
        whName = intent.getStringExtra("whName");
    }

    private void initData(){
        Intent intent = getIntent();
        whId = intent.getStringExtra("whId");
        whName = intent.getStringExtra("whName");
        getSearchStockList();
    }

    private void getSearchStockList() {
        HttpApi.getSearchStockList(0, 1000, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0){
                        setWarehouseData(jsonObject.getJSONObject("result"));
                    }else {
                        ToastUtils.showShort(BaseApplication.mContext, jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IOException e) {

            }
        });
    }

    private void setWarehouseData(JSONObject result) throws JSONException {
        JSONArray array = result.getJSONArray("list");
        int len = array.length();
        warehouse_layout.removeAllViews();
        warehouse_layout.setTag(R.id.id_ck_checked_id,"");
        warehouse_layout.setTag(R.id.id_ck_checked_index,-1);
        warehouse_layout.setTag(R.id.id_ck_checked_value,"");
        for (int i = 0; i < len; i++) {
            final JSONObject item = array.getJSONObject(i);
            TextView valueTv  = (TextView) inflater.inflate(R.layout.item_warehouse_value,warehouse_layout,false);
            valueTv.setText(item.getString("whName"));

            final int index = i;
//            if (whId.equals(item.getString("id"))){
//                warehouse_layout.setTag(R.id.id_ck_checked_id,item.getString("id"));
//                warehouse_layout.setTag(R.id.id_ck_checked_index,i);
//                warehouse_layout.setTag(R.id.id_ck_checked_value,item.getString("whName"));
//                valueTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_checked));
//                valueTv.setTextColor(getResources().getColor(R.color.white));
//            }

            valueTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView newTv = (TextView) v;
                    int oldCheckIndex = (int) warehouse_layout.getTag(R.id.id_ck_checked_index);

                    if (oldCheckIndex > -1){
                        TextView oldCheckTv = (TextView) warehouse_layout.getChildAt(oldCheckIndex);
                        oldCheckTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_unchecked));
                        oldCheckTv.setTextColor(getResources().getColor(R.color.color_33));
                    }
                    newTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_checked));
                    newTv.setTextColor(getResources().getColor(R.color.white));
                    try {
                        warehouse.setText(item.getString("whName"));
                        warehouse_layout.setTag(R.id.id_ck_checked_id,item.getString("id"));
                        warehouse_layout.setTag(R.id.id_ck_checked_index,index);
                        warehouse_layout.setTag(R.id.id_ck_checked_value,item.getString("whName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            warehouse_layout.addView(valueTv);
        }
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
                doAffirm();
                break;
        }
    }

    private void doAffirm() {
        EventBus.getDefault().post(new WarehouseEvent((String)warehouse_layout.getTag(R.id.id_ck_checked_id),(String)warehouse_layout.getTag(R.id.id_ck_checked_value)));
        finish();
    }

}

