package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.landicorp.module.scanner.ScannerActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zgzt.pos.R;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.event.GoodsEvent;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.node.PayMangerNode;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.TimeUtils;
import com.zgzt.pos.utils.ToastUtils;
import com.zgzt.pos.view.ShowPopupWindow;
import com.zgzt.pos.view.TimeSelector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新增调拨单页面
 */
public class AddNewAllotActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    ImageView title_back_btn;
    TextView title_text;
    TextView title_right_text;
    TextView call_out_name;
    TextView call_in_name;
    TextView bills_date;
    TextView document_maker;
    EditText remarks_info_input;
    TextView code_input_et;
    LinearLayout goods_item_layout;
    LinearLayout call_out_name_extend_btn;

    private List<String> datas;
    private ShowPopupWindow showPopupWindow;

    private List<JSONObject> outWarehouses;
    private JSONObject outWarehouse;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private List<JSONObject> goodsData; //商品信息列表
    private LayoutInflater inflater;
    private int editIndex = -1;
    private JSONArray inRequisitionLineList;
    private String remark;
    private String reqTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_allot);
        mContext = this;
        initView();
        initTitle();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addGoods(GoodsEvent item) {
        int action = item.getAction();
        if (action == 1) {
            goodsData.add(item.getItem());
            addGoodsItem(item.getItem());
        } else if (action == 2) {
            try {
                update(item.getItem().getString("purchaseNum"));
                editIndex = -1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        title_back_btn = findViewById(R.id.title_back_btn);
        title_text = findViewById(R.id.title_text);
        title_right_text = findViewById(R.id.title_right_text);
        call_out_name = findViewById(R.id.call_out_name);
        call_in_name = findViewById(R.id.call_in_name);
        bills_date = findViewById(R.id.bills_date);
        document_maker = findViewById(R.id.document_maker);
        remarks_info_input = findViewById(R.id.remarks_info_input);
        code_input_et = findViewById(R.id.code_input_et);
        goods_item_layout = findViewById(R.id.goods_item_layout);
        call_out_name_extend_btn = findViewById(R.id.call_out_name_extend_btn);
        title_right_text.setOnClickListener(this);
        call_out_name_extend_btn.setOnClickListener(this);
        code_input_et.setOnClickListener(this);
        findViewById(R.id.bills_date_extend_btn).setOnClickListener(this);
        findViewById(R.id.scan_btn).setOnClickListener(this);
    }

    public void initTitle() {
        title_text.setText("新增调拨单");
        title_right_text.setText("提交");
        title_right_text.setVisibility(View.VISIBLE);
        title_back_btn.setVisibility(View.VISIBLE);
    }

    public void initData() {
        inflater = LayoutInflater.from(this);
        document_maker.setText(PreferencesUtil.getInstance(mContext).getString(Constant.LOGIN_NAME));
        call_in_name.setText(PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_NAME));
        datas = new ArrayList<>();
        goodsData = new ArrayList<>();
        getSearchStockList();
    }

    private void getSearchStockList() {
        HttpApi.getSearchStockList(0, 1000, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        setDataToWarehouses(jsonObject.getJSONObject("result").getJSONArray("list"));
                    } else {
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

    /**
     * 设置库存数据
     */
    private void setDataToWarehouses(JSONArray array) throws JSONException {
        int len = array.length();
        outWarehouses = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            JSONObject item = array.getJSONObject(i);
            //2tgp2871ji9
            String loginWHId = PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID);
            String whId = item.getString("id");
            if (!loginWHId.equals(whId)) {
                if (i == 0) {
                    call_out_name.setText(item.getString("whName"));
                    outWarehouse = item;
                }
                datas.add(item.getString("whName"));
                outWarehouses.add(item);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call_out_name_extend_btn:
                showCallOutName();
                break;
            case R.id.bills_date_extend_btn:
                showDate();
                break;
            case R.id.scan_btn:
                startActivity(new Intent(this, ScannerActivity.class));
                break;
            case R.id.title_right_text:
                new QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("温馨提示")
                        .setMessage("是否提交该调拨单？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "提交", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                try {
                                    doSubmit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
                        .create(mCurrentDialogStyle).show();
                break;
            case R.id.code_input_et:
                doSearchGoods();
                break;
        }
    }

    /**
     * 搜索页面
     */
    private void doSearchGoods() {
        if (checkCK()) {
            try {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("type", "search");
                intent.putExtra("what", "DiaoBo");
                intent.putExtra("whId", PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示出库
     */
    private void showCallOutName() {
        if (datas.size() > 0) {
            if (null != showPopupWindow && showPopupWindow.isShowing()) {
                showPopupWindow.dismiss();
            } else {
                if (showPopupWindow == null) {
                    showPopupWindow = new ShowPopupWindow()
                            .builder(AddNewAllotActivity.this, call_out_name_extend_btn, R.layout.popup_window_layout2)
                            .setOnPopupWindowItemClickListener(new ShowPopupWindow.OnPopupWindowItemClickListener() {
                                @Override
                                public void onItemClick(String item, int position) {
                                    call_out_name.setText(item);
                                    outWarehouse = outWarehouses.get(position);
                                    showPopupWindow.dismiss();
                                }
                            })
                            .setList(datas);
                }
                showPopupWindow.show();
            }
        } else {
            ToastUtils.showShort(BaseApplication.mContext, "无有效的调拨仓库");
        }
    }

    /**
     * 显示日期
     */
    private void showDate() {
        String createTimeBegin = "2017-09-15 00:01";
        String createTimeEnd = TimeUtils.getNowString();
        TimeSelector timeSel = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                String[] split = time.split(" ");
                bills_date.setText(split[0]);
            }
        }, createTimeBegin, createTimeEnd);
        timeSel.setMode(TimeSelector.MODE.YMD);
        timeSel.setTitle("选择日期");
        timeSel.show();
    }

    /**
     * 检查仓库
     */
    private boolean checkCK() {
        if (null == outWarehouse) {
            ToastUtils.showShort(BaseApplication.mContext, "请先选择调拨仓库");
            return false;
        }
        return true;
    }

    /**
     * 检查日期
     */
    private boolean checkDate() {
        reqTime = bills_date.getText().toString();
        if (TextUtils.isEmpty(reqTime)) {
            ToastUtils.showShort(BaseApplication.mContext, "请选择日期");
            return false;
        }
        return true;
    }

    private void doSubmit() throws JSONException {
        if (checkCK() && checkDate()) {
            remark = remarks_info_input.getText().toString().trim();
            inRequisitionLineList = new JSONArray();
            int size = goodsData.size();
            for (int i = 0; i < size; i++) {
                JSONObject data = new JSONObject();
                JSONObject item = goodsData.get(i);
                data.put("productId", item.getString("productId"));
                data.put("brandId", item.getString("brandId"));
                data.put("classId", item.getString("classId"));
                data.put("packId", item.getString("packId"));
                data.put("productCid", item.getString("productCid"));
                data.put("productCode", item.getString("productCode"));
                data.put("productName", item.getString("productName"));
                data.put("remark", remark);
                data.put("skuId", item.getString("skuId"));
                data.put("skuCode", item.getString("skuCode"));
                data.put("skuQty", item.getString("purchaseNum"));
                inRequisitionLineList.put(data);
            }
            confirmwhreq();
        }
    }

    /**
     * 提交新增调拨单
     */
    private void confirmwhreq() {
        try {
            HttpApi.confirmwhreq(inRequisitionLineList, remark, reqTime + " 00:00:00",
                    outWarehouse.getString("id"), outWarehouse.getString("whName"),
                    PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID), PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_NAME),
                    new HttpCallback() {
                        @Override
                        public void onResponse(Object result) {
                            try {
                                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                int code = jsonObject.getInt("code");
                                if (code == 0) {
                                    ToastUtils.showShort(BaseApplication.mContext, "新增成功");
                                    finish();

                                } else {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(String purchaseNum) throws JSONException {
        if (editIndex > -1) {
            goodsData.get(editIndex).put("purchaseNum", purchaseNum);
            TextView numTv = goods_item_layout.getChildAt(editIndex).findViewById(R.id.item_num);
            numTv.setText("X " + purchaseNum);
        }
    }

    private void addGoodsItem(final JSONObject item) {
        final View itemRoot = inflater.inflate(R.layout.item_goods, null);
        ImageView itemImgIv = itemRoot.findViewById(R.id.item_img);          // 商品图片
        TextView itemNameTv = itemRoot.findViewById(R.id.item_name);         // 商品名称
        TextView itemSkuTv = itemRoot.findViewById(R.id.item_sku);           // 商品SKU
        TextView itemPricePayTv = itemRoot.findViewById(R.id.item_price_pay);// 实付款
        TextView itemPriceTv = itemRoot.findViewById(R.id.item_price);       // 商品价格
        TextView itemNumTv = itemRoot.findViewById(R.id.item_num);           // 商品数量
        ImageView itemEditBtn = itemRoot.findViewById(R.id.item_edit_btn);    // 编辑按钮
        TextView itemDelBtn = itemRoot.findViewById(R.id.item_del_btn);      // 删除按钮
        itemPriceTv.setVisibility(View.GONE);
        itemPricePayTv.setVisibility(View.GONE);
        final int itemNum = item.optInt("purchaseNum", 1);//商品件数
        final int index = goods_item_layout.getChildCount();
        try {
            goodsData.get(index).put("purchaseNum", itemNum);
            goodsData.get(index).put("productId", item.optString("productId"));
            goodsData.get(index).put("brandId", item.optString("brandId"));
            goodsData.get(index).put("classId", item.optString("classId"));
            goodsData.get(index).put("packId", item.optString("packId"));
            goodsData.get(index).put("productCid", item.optString("productCid"));
            goodsData.get(index).put("productCode", item.optString("productCode"));
            goodsData.get(index).put("productName", item.optString("productName"));
            goodsData.get(index).put("skuId", item.optString("skuId"));
            goodsData.get(index).put("skuCode", item.optString("skuCode"));
            goodsData.get(index).put("skuQty", item.optString("purchaseNum"));
            goodsData.get(index).put("remark", remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        itemEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(AddNewAllotActivity.this, GoodsInfoActivity.class);
                    intent.putExtra("data", item.toString());
                    intent.putExtra("num", goodsData.get(index).getInt("purchaseNum"));
                    intent.putExtra("action", 2);
                    startActivity(intent);
                    editIndex = index;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Glide.with(this)
                    .applyDefaultRequestOptions(BaseApplication.options110)
                    .load(item.getString("skuImgUrl"))
                    .thumbnail(0.5f)
                    .into(itemImgIv);
            itemSkuTv.setText(item.getString("property"));
            itemNameTv.setText(item.getString("productName"));
            itemNumTv.setText("X" + itemNum);

            itemDelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goods_item_layout.removeView(itemRoot);
                    goodsData.remove(item);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        goods_item_layout.addView(itemRoot);
    }

}
