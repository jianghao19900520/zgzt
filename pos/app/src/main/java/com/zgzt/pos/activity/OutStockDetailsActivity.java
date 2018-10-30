package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zgzt.pos.R;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.DialogUtils;
import com.zgzt.pos.utils.ToastUtils;
import com.zgzt.pos.view.ShowPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 出库单详情
 */
public class OutStockDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView title_back_btn;
    private TextView title_text;
    private TextView title_right_text;
    private TextView order_code;
    private TextView out_stock;
    private TextView in_stock;
    private TextView order_date;
    private TextView single_person;
    private TextView remarks_info;
    private TextView express;
    private EditText express_code_input;
    private LinearLayout goods_layout;
    private LinearLayout express_btn;

    private List<String> datas;
    private ShowPopupWindow showPopupWindow;
    private String mId;
    private JSONObject mData;
    private boolean mIsCanCliced = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_stock_details);
        mContext = this;
        initView();
        initTitle();
        initData();
    }

    private void initView() {
        title_back_btn = findViewById(R.id.title_back_btn);
        title_text = findViewById(R.id.title_text);
        title_right_text = findViewById(R.id.title_right_text);
        order_code = findViewById(R.id.order_code);
        out_stock = findViewById(R.id.out_stock);
        in_stock = findViewById(R.id.in_stock);
        order_date = findViewById(R.id.order_date);
        single_person = findViewById(R.id.single_person);
        remarks_info = findViewById(R.id.remarks_info);
        express = findViewById(R.id.express);
        express_code_input = findViewById(R.id.express_code_input);
        goods_layout = findViewById(R.id.goods_layout);
        express_btn = findViewById(R.id.express_btn);
        express_btn.setOnClickListener(this);

    }

    public void initTitle() {
        title_text.setText("出库单详情");
        title_back_btn.setVisibility(View.VISIBLE);
//        title_right_text.setVisibility(View.VISIBLE);
        title_right_text.setText("确认发货");
        title_right_text.setOnClickListener(this);
        title_back_btn.setOnClickListener(this);
    }

    private void initData() {
        datas = new ArrayList<>();
        datas.add("顺丰快递");
        datas.add("圆通快递");
        datas.add("申通快递");
        datas.add("邮政快递包裹");
        datas.add("EMS");
        datas.add("百世快递");
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        try {
            mData = new JSONObject(intent.getStringExtra("data"));
            order_code.setText(mData.getString("reqCode"));
            out_stock.setText(mData.getString("reqWhFromName"));
            in_stock.setText(mData.getString("reqWhToName"));
            order_date.setText(mData.getString("reqTime").split(" ")[0]);
            single_person.setText(mData.getString("reqToName"));
            String logisticsCompany = mData.getString("logisticsCompany");
            String logisticsCompanyCode = mData.getString("logisticsCompany");
            if (!"null".equals(logisticsCompany) && !"null".equals(logisticsCompanyCode)) {
                mIsCanCliced = false;
                express.setText(logisticsCompany);
                express_code_input.setText(logisticsCompanyCode);
                express_code_input.setInputType(InputType.TYPE_NULL); // 禁止输入（不弹出输入法）
            }
            remarks_info.setText(mData.getString("remark"));
            int deliveryStatus = mData.getInt("deliveryStatus");
            if (deliveryStatus == 1) {
                title_right_text.setVisibility(View.VISIBLE);
            } else if (deliveryStatus == 2) {
                title_right_text.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getInStockGoodsInfo();
    }

    /**
     * 获取入库单商品信息
     */
    private void getInStockGoodsInfo() {
        DialogUtils.getInstance().show(mContext);
        HttpApi.getStockInGoodsInfo(0, 1000, mId, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        addGoodsItem(jsonObject.getJSONObject("result").getJSONArray("list"));
                    } else {
                        ToastUtils.showShort(BaseApplication.mContext, jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onFailure(IOException e) {
                DialogUtils.getInstance().dismiss();
            }
        });
    }

    private void addGoodsItem(JSONArray array) throws JSONException {
        goods_layout.removeAllViews();
        int len = array.length();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < len; i++) {
            View itemView = inflater.inflate(R.layout.item_goods, null);
            JSONObject item = array.getJSONObject(i);

            ImageView itemIv = itemView.findViewById(R.id.item_img);

            TextView itemNameTV = itemView.findViewById(R.id.item_name);
            TextView itemSKUTV = itemView.findViewById(R.id.item_sku);
            TextView itemNumTV = itemView.findViewById(R.id.item_num);

            itemView.findViewById(R.id.item_del_btn).setVisibility(View.GONE);
            itemView.findViewById(R.id.item_edit_btn).setVisibility(View.GONE);
            itemView.findViewById(R.id.item_price_pay).setVisibility(View.GONE);
            itemView.findViewById(R.id.item_price).setVisibility(View.GONE);

            itemNameTV.setText(item.getJSONObject("proProduct").getString("productName"));

            itemNumTV.setText("X " + item.getString("skuQty"));

            if (!"null".equals(item.getString("proSku"))) {
                itemSKUTV.setText(item.getJSONObject("proSku").getString("property"));
                Glide.with(this)
                        .applyDefaultRequestOptions(BaseApplication.options110)
                        .load(item.getJSONObject("proSku").getString("skuImgUrl"))
                        .thumbnail(0.5f)
                        .into(itemIv);
            }
            goods_layout.addView(itemView);
        }
    }

    private void confirmGoods() {
        DialogUtils.getInstance().show(mContext, mContext.getString(R.string.submit_hint));
        HttpApi.confirmGoods(new JSONArray().put(mId), 1, 1, 1, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        ToastUtils.showShort(BaseApplication.mContext, "收货成功");
                        finish();
                    } else {
                        ToastUtils.showShort(BaseApplication.mContext, jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onFailure(IOException e) {
                DialogUtils.getInstance().dismiss();
            }
        });
    }

    private void showCallOutName() {
        if (null != showPopupWindow && showPopupWindow.isShowing()) {
            showPopupWindow.dismiss();
        } else {
            if (showPopupWindow == null) {
                showPopupWindow = new ShowPopupWindow()
                        .builder(OutStockDetailsActivity.this, express_btn, R.layout.popup_window_layout2)
                        .setOnPopupWindowItemClickListener(new ShowPopupWindow.OnPopupWindowItemClickListener() {
                            @Override
                            public void onItemClick(String item, int position) {
                                express.setText(item);
                                showPopupWindow.dismiss();
                            }
                        })
                        .setList(datas);
            }
            showPopupWindow.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_right_text:
                confirmGoods();
                break;
            case R.id.express_btn:
                if (mIsCanCliced) {
                    showCallOutName();
                }
                break;
            case R.id.title_back_btn:
                OutStockDetailsActivity.this.finish();
                break;
        }
    }
}
