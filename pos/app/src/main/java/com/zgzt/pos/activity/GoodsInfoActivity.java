package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIFloatLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.event.GoodsEvent;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.ArithUtils;
import com.zgzt.pos.utils.DialogUtils;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoodsInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView title_back_btn;
    private TextView title_text;
    private ImageView goods_img;
    private ImageView goods_price_modify_btn;
    private TextView goods_name;
    private TextView goods_code;
    private LinearLayout sku_layout;
    private LinearLayout btn_layout;
    private LinearLayout discount_layout;
    private TextView goods_price;
    private TextView discount_tv;
    private TextView goods_num;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private int num = 1;
    private JSONObject mData;
    private JSONObject mAttrMap;

    private LayoutInflater inflater;

    private String attrValId = "";// 选中SKU item 的 id
    private JSONArray skuList;// 不同sku组合的商品信息列表
    private JSONObject checkSUKItem;// 选中SKU商品信息
    private JSONArray b2cPriceList;
    private String price;
    private String discountPrice;
    private String productName;
    private String what;
    private int action = 1;//1.新增 2.修改
    private String editPrice;
    private String goodsPriceOld;
    private String color = "";
    private String size = "";
    private List<TextView> selectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        mContext = this;
        initView();
        initTitle();
        initData();
    }

    private void initView() {
        title_back_btn = findViewById(R.id.title_back_btn);
        title_text = findViewById(R.id.title_text);
        goods_img = findViewById(R.id.goods_img);
        goods_price_modify_btn = findViewById(R.id.goods_price_modify_btn);
        goods_name = findViewById(R.id.goods_name);
        goods_code = findViewById(R.id.goods_code);
        sku_layout = findViewById(R.id.sku_layout);
        btn_layout = findViewById(R.id.btn_layout);
        discount_layout = findViewById(R.id.discount_layout);
        goods_price = findViewById(R.id.goods_price);
        discount_tv = findViewById(R.id.discount_tv);
        goods_num = findViewById(R.id.goods_num);
        goods_price_modify_btn.setOnClickListener(this);
        findViewById(R.id.goods_num_reduce_btn).setOnClickListener(this);
        findViewById(R.id.goods_num_plus_btn).setOnClickListener(this);
        findViewById(R.id.goods_cancel_btn).setOnClickListener(this);
        findViewById(R.id.goods_add_btn).setOnClickListener(this);
        findViewById(R.id.discount__modify_btn).setOnClickListener(this);
    }

    public void initTitle() {
        title_back_btn.setVisibility(View.VISIBLE);
        title_back_btn.setOnClickListener(this);
        title_text.setText("商品信息");
        Intent intent = getIntent();
        what = intent.getStringExtra("what");
        selectText = new ArrayList<>();
        size = intent.getStringExtra("size");
        color = intent.getStringExtra("color");
        action = intent.getIntExtra("action", 1);
        if (action == 2) {
            // 如果是修改 则需要拿到 原来的商品信息
            editPrice = intent.getStringExtra("price");
            num = intent.getIntExtra("num", 1);
        }
        try {
            mData = new JSONObject(intent.getStringExtra("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initData() {
        inflater = LayoutInflater.from(this);
        setItemData(mData);
        getGoodsInfo();
        if ("DiaoBo".equals(what)) {
            goods_price_modify_btn.setVisibility(View.GONE);
        } else {
            goods_price_modify_btn.setVisibility(View.VISIBLE);
        }
        goods_num.setText(num + "");
    }

    private void getGoodsInfo() {
        try {
            DialogUtils.getInstance().show(mContext);
            HttpApi.getGoodsInfo(mData.getString("productId"), PreferencesUtil.getInstance(mContext).getString(Constant.USER_ID), new HttpCallback() {
                @Override
                public void onResponse(Object result) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            handlerDetailsData(jsonObject.getJSONObject("result"));
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
            mData.getString("productId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setItemData(JSONObject data) {
        try {
            String imgUrl = "";
            if (data.has("imgUrl")) {
                imgUrl = data.getString("imgUrl");
            } else if (data.has("skuImgUrl")) {
                imgUrl = data.getString("skuImgUrl");
            }
            productName = data.getString("productName");
            goods_name.setText(productName);

            Glide.with(this)
                    .applyDefaultRequestOptions(BaseApplication.options110)
                    .load(imgUrl)
                    .thumbnail(0.5f)
                    .into(goods_img);
            goods_code.setText("商品编码：" + data.getString("productCode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handlerDetailsData(JSONObject result) throws JSONException {
        mAttrMap = result.getJSONObject("attrMap");
        skuList = result.getJSONArray("skuList");
        b2cPriceList = result.getJSONArray("b2cPriceList");
        if (TextUtils.isEmpty(attrValId)) {
            int len = skuList.length();
            for (int i = 0; i < len; i++) {
                JSONObject item = skuList.getJSONObject(i);
                int isMain = item.getInt("isMain");
                if (isMain == 1) {
                    attrValId = item.getString("attrValId");
                    checkSUKItem = item;

                    checkSUKItem.put("purchaseNum", num);
                    checkSUKItem.put("productName", productName);
                    setPrice();
                    break;
                }
            }
        } else {
            getCheckedItem();
        }
        setSkuData();
        if (!TextUtils.isEmpty(editPrice)) {
            setNewPrice(editPrice);
        }
    }

    private void setPrice() throws JSONException {
        int stockQty = checkSUKItem.getInt("stockQty");
        if (stockQty > 0) {
            btn_layout.setVisibility(View.VISIBLE);
        } else {
            btn_layout.setVisibility(View.GONE);
        }
        int len = b2cPriceList.length();
        for (int i = 0; i < len; i++) {
            JSONObject itemPrice = b2cPriceList.getJSONObject(i);
            String skuID = checkSUKItem.getString("id");
            if (skuID.equals(itemPrice.getString("skuId"))) {
                price = ArithUtils.get2Decimal(itemPrice.getString("price_1"));
                discountPrice = price;
                goodsPriceOld = price;
                checkSUKItem.put("price", price);
                checkSUKItem.put("discountPrice", discountPrice);
                goods_price.setText(price);
                discount_layout.setVisibility(View.GONE);

                Glide.with(this)
                        .applyDefaultRequestOptions(BaseApplication.options110)
                        .load(checkSUKItem.getString("skuImgUrl"))
                        .thumbnail(0.5f)
                        .into(goods_img);
            }
        }
    }

    private void setSkuData() throws JSONException {
        selectText.clear();
        Iterator<String> keys = mAttrMap.keys();
        sku_layout.removeAllViews();
        while (keys.hasNext()) {
            String key = keys.next();
            View itemView = inflater.inflate(R.layout.item_sku_layout, null);
            final TextView skuKeyTv = itemView.findViewById(R.id.item_sku_name);
            skuKeyTv.setText(key + ":");

            final QMUIFloatLayout floatLayout = itemView.findViewById(R.id.item_sku_layout);
            floatLayout.removeAllViews();
            JSONArray attrValNameS = mAttrMap.getJSONArray(key);
            int len = attrValNameS.length();
            for (int i = 0; i < len; i++) {
                final TextView skuValueTv = (TextView) inflater.inflate(R.layout.item_sku_value, floatLayout, false);
                skuValueTv.setTag(i);
                final JSONObject valueItem = attrValNameS.getJSONObject(i);
                if (valueItem.toString().contains("attrValName")) {
                    skuValueTv.setText(valueItem.getString("attrValName"));
                }
                String itemAttrValId = valueItem.getString("attrValId");
                if (attrValId.indexOf(itemAttrValId) > -1) {
                    skuValueTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_checked));
                    skuValueTv.setTextColor(getResources().getColor(R.color.white));
                    skuKeyTv.setTag(R.id.id_sku_checked_index, i);
                    skuKeyTv.setTag(R.id.id_sku_checked_id, valueItem.getString("attrValId"));
                    skuKeyTv.setTag(R.id.id_sku_checked_value, valueItem.getString("attrValName"));
                } else {
                    skuValueTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_unchecked));
                    skuValueTv.setTextColor(getResources().getColor(R.color.color_33));
                }
                skuValueTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView newTv = (TextView) v;
                        int newIndex = (int) v.getTag();
                        int oldIndex = (int) skuKeyTv.getTag(R.id.id_sku_checked_index);
                        if (newIndex != oldIndex) {
                            TextView oldTv = (TextView) floatLayout.getChildAt(oldIndex);
                            oldTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_unchecked));
                            oldTv.setTextColor(getResources().getColor(R.color.color_33));

                            newTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.sku_bg_checked));
                            newTv.setTextColor(getResources().getColor(R.color.white));
                            try {
                                String oldId = (String) skuKeyTv.getTag(R.id.id_sku_checked_id);
                                String oldValue = (String) skuKeyTv.getTag(R.id.id_sku_checked_value);

                                String newId = valueItem.getString("attrValId");
                                String newValue = valueItem.getString("attrValName");
                                attrValId = attrValId.replace(oldId, newId);
                                getCheckedItem();

                                skuKeyTv.setTag(R.id.id_sku_checked_index, newIndex);
                                skuKeyTv.setTag(R.id.id_sku_checked_id, newId);
                                skuKeyTv.setTag(R.id.id_sku_checked_value, newValue);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                selectText.add(skuValueTv);
                floatLayout.addView(skuValueTv);
            }
            sku_layout.addView(itemView);
        }

        for (TextView textView : selectText) {
            if (textView.getText().equals(color) || textView.getText().equals(size))
                textView.performClick();
        }
    }

    private void getCheckedItem() throws JSONException {
        String[] attrs = attrValId.split(",");
        int len = skuList.length();
        for (int i = 0; i < len; i++) {
            JSONObject item = skuList.getJSONObject(i);
            String itemAttrValId = item.getString("attrValId");
            if (attrs.length == 1) {
                if (itemAttrValId.indexOf(attrs[0]) > -1) {
                    attrValId = item.getString("attrValId");
                    checkSUKItem = item;
                    checkSUKItem.put("purchaseNum", num);
                    checkSUKItem.put("productName", productName);
                    setPrice();
                    break;
                }
            } else if (attrs.length == 2) {
                if (itemAttrValId.indexOf(attrs[0]) > -1 && itemAttrValId.indexOf(attrs[1]) > -1) {
                    attrValId = item.getString("attrValId");
                    checkSUKItem = item;
                    checkSUKItem.put("purchaseNum", num);
                    checkSUKItem.put("productName", productName);
                    setPrice();
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discount__modify_btn:
                modifyDiscount();
                break;
            case R.id.goods_price_modify_btn:
                modifyPrice();
                break;
            case R.id.goods_num_reduce_btn:
                doReduce();
                break;
            case R.id.goods_num_plus_btn:
                doPlus();
                break;
            case R.id.goods_cancel_btn:
                finish();
                break;
            case R.id.goods_add_btn:
                EventBus.getDefault().post(new GoodsEvent(checkSUKItem, action));
                finish();
                break;
            case R.id.title_back_btn:
                GoodsInfoActivity.this.finish();
                break;
        }
    }

    /**
     * 修改折扣
     */
    private void modifyDiscount() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(GoodsInfoActivity.this);
        try {
            final String price = checkSUKItem.getString("price");
            builder.setTitle("修改折扣")
                    .setPlaceholder("请输入折扣,例如：八五折输入8.5")
                    .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            CharSequence text = builder.getEditText().getText();
                            if (text != null && text.length() > 0) {
                                discount_tv.setText(text.toString() + " 折");

                                dialog.dismiss();
                                discountPrice = ArithUtils.div(ArithUtils.mul(price, text.toString()), "10");
                                goods_price.setText(discountPrice);
                                try {
                                    checkSUKItem.put("discountPrice", discountPrice);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                discount_layout.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(GoodsInfoActivity.this, "请输入修改后的价格", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .create(mCurrentDialogStyle).show();
            final EditText editText = builder.getEditText();
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().contains(".")) {
                        if (s.length() - 1 - s.toString().indexOf(".") > 1) {
                            s = s.toString().subSequence(0,
                                    s.toString().indexOf(".") + 2);
                            editText.setText(s);
                            editText.setSelection(s.length());
                        }
                    }
                    if (s.toString().trim().substring(0).equals(".")) {
                        s = "0" + s;
                        editText.setText(s);
                        editText.setSelection(1);
                    }

                    if (s.toString().startsWith("0")
                            && s.toString().trim().length() > 1) {
                        if (!s.toString().substring(1, 2).equals(".")) {
                            editText.setText(s.subSequence(0, 1));
                            editText.setSelection(1);
                            return;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改价格
     */
    private void modifyPrice() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(GoodsInfoActivity.this);
        builder.setTitle("修改价格")
                .setPlaceholder("原价：" + price)
                .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            setNewPrice(text.toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(GoodsInfoActivity.this, "请输入修改后的价格", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
        final EditText editText = builder.getEditText();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setNewPrice(String newPrice) {

        discountPrice = ArithUtils.get2Decimal(newPrice);
        goods_price.setText(discountPrice);
        try {
            checkSUKItem.put("discountPrice", discountPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String discount = ArithUtils.mul(ArithUtils.div(newPrice, goodsPriceOld), "10", 1);
        if (discount.endsWith(".0")) {
            discount = discount.substring(0, discount.length() - 2);
        }
        discount_tv.setText(discount + " 折");
        discount_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 加
     */
    private void doPlus() {
        try {
            if (num < checkSUKItem.getInt("stockQty")) {
                num++;
                goods_num.setText(num + "");
                checkSUKItem.put("purchaseNum", num);
            } else {
                ToastUtils.showShort(BaseApplication.mContext, "库存不足");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 减
     */
    private void doReduce() {
        if (num > 1) {
            num--;
            goods_num.setText(num + "");
            try {
                checkSUKItem.put("purchaseNum", num);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.showShort(BaseApplication.mContext, "至少一件");
        }
    }
}
