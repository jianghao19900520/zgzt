package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zgzt.pos.R;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.base.CommAdapter;
import com.zgzt.pos.base.CommViewHolder;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 调拨入/出库单
 */
public class StockAllotActivity extends AppCompatActivity implements OnRefreshLoadmoreListener {

    private Context mContext;
    ImageView title_back_btn;
    TextView title_text;
    ListView list_view;
    SmartRefreshLayout refreshLayout;

    private List<JSONObject> dataList;
    private CommAdapter<String> adapter;
    private Intent intent;
    private String type;
    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_allot);
        mContext = this;
        initView();
        initTitle();
        initData();
    }

    private void initView() {
        title_back_btn = findViewById(R.id.title_back_btn);
        title_text = findViewById(R.id.title_text);
        list_view = findViewById(R.id.list_view);
        refreshLayout = findViewById(R.id.refreshLayout);
    }

    public void initTitle() {
        intent = getIntent();
        type = intent.getStringExtra("type");
        title_back_btn.setVisibility(View.VISIBLE);
        if ("in".equals(type)) {
            title_text.setText("调拨入库单");
        } else if ("out".equals(type)) {
            title_text.setText("调拨出库单");
        }
    }

    public void initData() {
        refreshLayout.setOnRefreshLoadmoreListener(this);
        refreshLayout.setEnableLoadmore(false);

        dataList = new ArrayList<>();

        adapter = new CommAdapter<String>(this, dataList, R.layout.item_in_stock) {
            @Override
            public void convert(CommViewHolder holder, final JSONObject item) {
                View rootView = holder.getConvertView();
                try {
                    holder.setText(R.id.item_code, item.getString("reqCode"));
                    holder.setText(R.id.item_date, item.getString("createdTime"));
                    holder.setText(R.id.item_out, item.getString("reqWhFromName"));
                    holder.setText(R.id.item_in, item.getString("reqWhToName"));
                    int deliveryStatus = item.getInt("deliveryStatus");// 发出状态 1未发出2已发出
                    int cneeStatus = item.getInt("cneeStatus");// 验收状态 1未验收2已验收
                    int reqStatus = item.getInt("reqStatus");// 调拨单状态 1待审核2审核通过3审核不通过4作废
                    CheckBox outState = holder.getView(R.id.out_state);
                    CheckBox inState = holder.getView(R.id.in_state);
                    CheckBox auditState = holder.getView(R.id.audit_state);
                    final String itemId = item.getString("id");

                    if (deliveryStatus == 1) {
                        outState.setText("未出库");
                        outState.setChecked(false);
                    } else if (deliveryStatus == 2) {
                        outState.setText("已出库");
                        outState.setChecked(true);
                    }

                    if (cneeStatus == 1) {
                        inState.setText("未入库");
                        inState.setChecked(false);
                    } else if (cneeStatus == 2) {
                        inState.setText("已入库");
                        inState.setChecked(true);
                    }

                    if (reqStatus == 1) {
                        auditState.setChecked(false);
                        auditState.setText("审核中");
                    } else if (reqStatus == 2) {
                        auditState.setChecked(true);
                        auditState.setText("审核通过");
                    } else if (reqStatus == 3) {
                        auditState.setChecked(false);
                        auditState.setText("审核未通过");
                    } else if (reqStatus == 4) {
                        auditState.setChecked(false);
                        auditState.setText("作废");
                    }
                    rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = null;
                            if ("in".equals(type)) {
                                intent = new Intent(mContext, InStockDetailsActivity.class);
                            } else if ("out".equals(type)) {
                                intent = new Intent(mContext, OutStockDetailsActivity.class);
                            }
                            intent.putExtra("id", itemId);
                            intent.putExtra("data", item.toString());
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        list_view.setAdapter(adapter);
        plistbyWhreq();
    }

    private void plistbyWhreq() {
        int stockType = 0;
        if ("out".equals(type)) {
            // 调拨出库
            stockType = 2;
        } else if ("in".equals(type)) {
            // 调拨入库
            stockType = 1;
        }
        String userId = PreferencesUtil.getInstance(mContext).getString(Constant.USER_ID);
        HttpApi.plistbyWhreq(userId, pageIndex, Constant.PAGE_SIZE, stockType, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        setDataToList(jsonObject.getJSONObject("result"));
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

    private void setDataToList(JSONObject result) throws JSONException {
        int recordCount = result.getInt("recordCount");
        if (pageIndex == 0) {
            dataList.clear();
        }
        if ((pageIndex + 1) * Constant.PAGE_SIZE < recordCount) {
            refreshLayout.setEnableLoadmore(true);
        } else {
            refreshLayout.setEnableLoadmore(false);
        }
        JSONArray list = result.getJSONArray("list");
        int len = list.length();
        for (int i = 0; i < len; i++) {
            dataList.add(list.getJSONObject(i));
        }
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {

    }
}
