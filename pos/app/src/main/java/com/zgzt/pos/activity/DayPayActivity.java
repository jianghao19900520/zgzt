package com.zgzt.pos.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zgzt.pos.R;
import com.zgzt.pos.base.CommAdapter;
import com.zgzt.pos.base.CommViewHolder;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.node.PayMangerItemNode;
import com.zgzt.pos.utils.ArithUtils;
import com.zgzt.pos.utils.DialogUtils;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.TimeUtils;
import com.zgzt.pos.view.ShowPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 支付明细页面
 */
public class DayPayActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView title_back_btn;
    private TextView title_text;
    private TextView header_tv;
    private TextView pay_type_tv;
    private TextView clerk_tv;
    private RelativeLayout select_pay_type_btn;
    private RelativeLayout select_clerk_btn;
    private ListView list_view;
    private List<JSONObject> dataList;
    private CommAdapter<String> adapter;
    private static int[] icons = new int[]{R.drawable.logo, R.drawable.cash_s, R.drawable.wechat_s,
            R.drawable.alipay_s, R.drawable.card_s};

    private LayoutInflater inflater;

    private List<String> payTypes;
    private List<String> clerks;
    private ShowPopupWindow payTypesPop;
    private ShowPopupWindow clerksPop;
    private PayMangerItemNode mPayMangerItemNode;
    private JSONArray shoppingGuides;
    private String shoppingGuideId;
    private String shoppingGuideName;

    private String createdTimeFrom;
    private String createdTimeTo;
    private String payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_pay);
        mContext = this;
        inflater = LayoutInflater.from(mContext);
        initView();
        initTitle();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_back_btn = findViewById(R.id.title_back_btn);
        title_text = findViewById(R.id.title_text);
        header_tv = findViewById(R.id.header_tv);
        pay_type_tv = findViewById(R.id.pay_type_tv);
        clerk_tv = findViewById(R.id.clerk_tv);
        select_pay_type_btn = findViewById(R.id.select_pay_type_btn);
        select_clerk_btn = findViewById(R.id.select_clerk_btn);
        list_view = findViewById(R.id.list_view);
        select_pay_type_btn.setOnClickListener(this);
        select_clerk_btn.setOnClickListener(this);
    }

    /**
     * 初始化标题栏
     */
    public void initTitle() {
        title_text.setText("支付明细");
        title_back_btn.setVisibility(View.VISIBLE);
        title_back_btn.setOnClickListener(this);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        mPayMangerItemNode = (PayMangerItemNode) bundle.getSerializable("node");
        String date = TimeUtils.millis2String(mPayMangerItemNode.getStatisticsTime(), new SimpleDateFormat("yyyy-MM-dd"));
        createdTimeFrom = date + " 00:00:00";
        createdTimeTo = date + " 23:59:59";
        header_tv.setText(TimeUtils.millis2String(mPayMangerItemNode.getStatisticsTime(), new SimpleDateFormat("yyyy年MM月dd日")));
    }

    /**
     * 初始化数据
     */
    public void initData() {
        payTypes = new ArrayList<>();
        clerks = new ArrayList<>();
        payTypes.add("全部付款方式");
        payTypes.add("现金支付");
        payTypes.add("微信支付");
        payTypes.add("支付宝支付");
        payTypes.add("银行卡支付");
        clerks.add("全部店员");
        inflater = LayoutInflater.from(this);
        dataList = new ArrayList<>();
        adapter = new CommAdapter<String>(this, dataList, R.layout.item_day_pay) {
            @Override
            public void convert(CommViewHolder holder, JSONObject item) {
                int position = holder.getPosition();
                View view = holder.getView(R.id.item_button_line);
                try {
                    String postTime = item.getString("postTime");
                    String time = postTime.split(" ")[1];
                    time = time.substring(0, time.length() - 3);
                    holder.setText(R.id.item_time, time);
                    holder.setText(R.id.item_money, "￥ " + ArithUtils.get2Decimal(item.getString("proceedsTotal")));
                    holder.setText(R.id.item_order_code, "订单编号：" + item.getString("orderCode"));
                    int payType = item.getInt("payType");//支付方式 0赊账 1资金支付 2微信支付 3支付宝支付 4购物卡支付 5积分支付
                    if (payType < 0 || payType > 4) payType = 0;
                    holder.setImageResource(R.id.item_pay_type, icons[payType]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (position == (dataList.size() - 1)) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        };
        View headView = inflater.inflate(R.layout.item_head_day_pay, null);
        TextView allMoneyTv = headView.findViewById(R.id.all_money_tv);
        allMoneyTv.setText(ArithUtils.get2Decimal(String.valueOf(mPayMangerItemNode.getOrderTotal())));
        list_view.addHeaderView(headView);
        list_view.setAdapter(adapter);
//        loadData(1,"",RequestMethod.GET);
//        loadData(2,getString(R.string.loading_hint),RequestMethod.POST);
        getClerkData();
        getPayDayDetailedList();
    }

    /**
     * 获取导购员列表
     */
    private void getClerkData() {
        HttpApi.getClerkData(PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID), new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    setShoppingGuideData(new JSONObject(String.valueOf(result)).getJSONArray("result"));
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
     * 获取每日支付明细列表
     */
    private void getPayDayDetailedList() {
        DialogUtils.getInstance().show(mContext);
        String storeId = PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID);
        HttpApi.getPayDayDetailedList(storeId, createdTimeFrom, createdTimeTo, shoppingGuideId, payType, new HttpCallback() {

            @Override
            public void onResponse(Object result) {
                try {
                    setDataTolist(new JSONObject(String.valueOf(result)).getJSONObject("result"));
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

    /**
     * 设置店员数据
     */
    private void setShoppingGuideData(JSONArray result) throws JSONException {
        int len = result.length();
        shoppingGuides = result;
        for (int i = 0; i < len; i++) {
            JSONObject object = result.getJSONObject(i);
            if (i == 0) {
                clerk_tv.setText(object.optString("name", "导购员"));
                shoppingGuideId = object.getString("id");
                shoppingGuideName = object.optString("name", "导购员");
            }
            clerks.add(object.optString("name", "导购员"));
        }
    }

    /**
     * 设置每日支付数据
     */
    private void setDataTolist(JSONObject result) throws JSONException {
        JSONArray array = result.getJSONArray("list");
        int len = array.length();
        dataList.clear();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            JSONArray orderPayments = json.getJSONArray("orderPayments");
            if (orderPayments.length() > 0) {
                dataList.add(orderPayments.getJSONObject(0));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_pay_type_btn:
                showPayTypes();
                break;
            case R.id.select_clerk_btn:
                showClerks();
                break;
            case R.id.title_back_btn:
                DayPayActivity.this.finish();
                break;
        }
    }

    /**
     * 展开支付方式
     */
    private void showPayTypes() {
        if (null != payTypesPop && payTypesPop.isShowing()) {
            payTypesPop.dismiss();
        } else {
            if (payTypesPop == null) {
                payTypesPop = new ShowPopupWindow()
                        .builder(mContext, select_pay_type_btn, R.layout.popup_window_layout)
                        .setOnPopupWindowItemClickListener(new ShowPopupWindow.OnPopupWindowItemClickListener() {
                            @Override
                            public void onItemClick(String item, int position) {
                                pay_type_tv.setText(item);
                                payTypesPop.dismiss();
                                payType = String.valueOf(position);
                            }
                        })
                        .setList(payTypes);
            }
            payTypesPop.show();
        }
    }

    /**
     * 展开全部店员
     */
    private void showClerks() {
        if (null != clerksPop && clerksPop.isShowing()) {
            clerksPop.dismiss();
        } else {
            if (clerksPop == null) {
                clerksPop = new ShowPopupWindow()
                        .builder(mContext, select_clerk_btn, R.layout.popup_window_layout)
                        .setOnPopupWindowItemClickListener(new ShowPopupWindow.OnPopupWindowItemClickListener() {
                            @Override
                            public void onItemClick(String item, int position) {
                                clerk_tv.setText(item);
                                clerksPop.dismiss();
                                if (position == 0) {
                                    shoppingGuideId = "";
                                } else {
                                    try {
                                        shoppingGuideId = shoppingGuides.getJSONObject(position - 1).getString("id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .setList(clerks);

            }
            clerksPop.show();
        }
    }
}
