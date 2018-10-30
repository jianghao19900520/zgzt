package com.zgzt.pos.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 入库单详情
 */
public class InStockDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView title_back_btn;
    private TextView title_text;
    private TextView title_right_text;
    private TextView order_code;
    private TextView out_stock;
    private TextView in_stock;
    private TextView order_date;
    private TextView single_person;
    private TextView express_name;
    private TextView express_code;
    private TextView remarks_info;
    private LinearLayout goods_layout;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_stock_details);
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
        express_name = findViewById(R.id.express_name);
        express_code = findViewById(R.id.express_code);
        remarks_info = findViewById(R.id.remarks_info);
        goods_layout = findViewById(R.id.goods_layout);
        express_code.setOnClickListener(this);
    }

    public void initTitle() {
        title_text.setText("入库单详情");
        title_back_btn.setVisibility(View.VISIBLE);
        title_right_text.setText("确认收货");
        title_right_text.setOnClickListener(this);
        title_back_btn.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        express_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        express_code.getPaint().setAntiAlias(true);//抗锯齿
        getStockInBillInfo();
        getStockInGoodsInfo();
    }

    private void getStockInBillInfo() {
        DialogUtils.getInstance().show(mContext);
        HttpApi.getStockInBillInfo(mId, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        setData(jsonObject.getJSONObject("result"));
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

    private void setData(JSONObject result) throws JSONException {
        order_code.setText(result.getString("reqCode"));
        out_stock.setText(result.getString("reqWhFromName"));
        in_stock.setText(result.getString("reqWhToName"));
        order_date.setText(result.getString("reqTime").split(" ")[0]);
        single_person.setText(result.getString("reqToName"));
        String logisticsCompany = result.getString("logisticsCompany");
        String logisticsCompanyCode = result.getString("logisticsCompany");
        if (!"null".equals(logisticsCompany) && !"null".equals(logisticsCompanyCode)) {
            express_name.setText(result.getString("logisticsCompany"));
            express_code.setText(result.getString("logisticsCompanyCode"));
        }
        remarks_info.setText(result.getString("remark"));
        int deliveryStatus = result.getInt("deliveryStatus");
        if (deliveryStatus == 1) {
            title_right_text.setVisibility(View.GONE);
        } else if (deliveryStatus == 2) {
            title_right_text.setVisibility(View.VISIBLE);
        }
    }

    private void getStockInGoodsInfo() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_right_text:
                new QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("温馨提示")
                        .setMessage("请确认已经收到货物，否则会造成财产损失？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "收货", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                confirmGoods();
                                dialog.dismiss();
                            }
                        })
                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                break;
            case R.id.express_code:
                setDataToClipboard();
                break;
            case R.id.title_back_btn:
                InStockDetailsActivity.this.finish();
                break;
        }
    }

    private void confirmGoods() {
        DialogUtils.getInstance().show(mContext, mContext.getString(R.string.submit_hint));
        HttpApi.confirmGoods(new JSONArray().put(mId), 2, 0, 0, new HttpCallback() {
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

    private void setDataToClipboard() {
        String expressCodeStr = express_code.getText().toString().trim();
        if (!TextUtils.isEmpty(expressCodeStr)) {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", expressCodeStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            ToastUtils.showShort(BaseApplication.mContext, "内容已复制");
        }

    }
}
