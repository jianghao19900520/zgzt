package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.landicorp.module.scanner.ScannerActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.node.GoodsNode;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 库存查询页面
 */
public class StockQueryActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshLoadmoreListener {

    private Context mContext;
    private ImageView title_back_btn;//标题栏返回键
    private TextView title_text;//标题
    private TextView title_right_text_image;//标题栏右侧图文键

    private EditText code_input_et;//输入框
    private SmartRefreshLayout smart_refresh_layout;//下拉刷新控件
    private ListView list_view;
    private LayoutInflater inflater;

    private String searchKey;
    private List<JSONObject> listData;
    private StockQueryAdapter adapter;
    private int pageIndex = 0;
    private String whId;
    private String whName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_query);
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
        title_right_text_image = findViewById(R.id.title_right_text_image);
        code_input_et = findViewById(R.id.code_input_et);
        findViewById(R.id.scan_btn).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);
        title_back_btn.setOnClickListener(this);
        title_right_text_image.setOnClickListener(this);
        smart_refresh_layout = findViewById(R.id.smart_refresh_layout);
        list_view = findViewById(R.id.list_view);
        adapter = new StockQueryAdapter();
        listData = new ArrayList<>();
        list_view.setAdapter(adapter);
        smart_refresh_layout.setEnableLoadmore(false);
        smart_refresh_layout.setOnRefreshLoadmoreListener(this);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageIndex++;
        getSearchStock();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageIndex = 0;
        getSearchStock();
    }

    /**
     * 初始化标题栏
     */
    public void initTitle() {
        title_text.setText("库存查询");
        title_back_btn.setVisibility(View.VISIBLE);
        title_right_text_image.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化数据
     */
    public void initData() {
        whId = PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID);
        whName = PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_NAME);
    }

    private void getSearchStock() {
        HttpApi.stocksearchlist(pageIndex, Constant.PAGE_SIZE, whId, searchKey, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                if (pageIndex == 0) {
                    smart_refresh_layout.finishRefresh();
                } else {
                    smart_refresh_layout.finishLoadmore();
                }
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        setDataTolist(jsonObject.getJSONObject("result"));
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
     * 配置数据
     */
    private void setDataTolist(JSONObject result) throws JSONException {
        int recordCount = result.getInt("recordCount");
        if ((pageIndex + 1) * Constant.PAGE_SIZE < recordCount) {
            smart_refresh_layout.setEnableLoadmore(true);
        } else {
            smart_refresh_layout.setEnableLoadmore(false);
        }
        if (pageIndex == 0) {
            listData.clear();
        }
        JSONArray list = result.getJSONArray("list");
        int len = list.length();
        for (int i = 0; i < len; i++) {
            listData.add(list.getJSONObject(i));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back_btn:
                finish();
                break;
            case R.id.title_right_text_image:
                Intent intent = new Intent(this, StockFilterActivity.class);
                intent.putExtra("whId", whId);
                intent.putExtra("whName", whName);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.scan_btn:
                startActivity(new Intent(this, ScannerActivity.class));
                break;
            case R.id.search_btn:
                searchKey = code_input_et.getText().toString().trim();
                pageIndex = 0;
                getSearchStock();
                break;
        }
    }

    public class StockQueryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            StockQueryAdapter.ViewHolder holder = null;
            JSONObject object = (JSONObject) getItem(position);
            if (null == object) return convertView;
            if (null != convertView) {
                holder = (StockQueryAdapter.ViewHolder) convertView.getTag();
            }
            if (null == holder) {
                holder = new StockQueryAdapter.ViewHolder();
                view = inflater.inflate(R.layout.item_stock_query, null);
                view.setTag(holder);
                initItem(view, holder, object);
            } else {
                view = convertView;
                holder = (StockQueryAdapter.ViewHolder) convertView.getTag();
                view.setTag(holder);
                updateItem(view, holder, object);
            }
            return view;
        }

        private class ViewHolder {
            public ImageView item_img;
            public TextView item_code;
            public TextView item_name;
            public TextView item_stockVolume;
        }

        private void initItem(View view, StockQueryAdapter.ViewHolder holder, JSONObject object) {
            holder.item_img = view.findViewById(R.id.item_img);
            holder.item_code = view.findViewById(R.id.item_code);
            holder.item_name = view.findViewById(R.id.item_name);
            holder.item_stockVolume = view.findViewById(R.id.item_stockVolume);
            updateItem(view, holder, object);
        }

        private void updateItem(View view, StockQueryAdapter.ViewHolder holder, JSONObject item) {
            try {
                Glide.with(mContext).setDefaultRequestOptions(BaseApplication.options110)
                        .load(item.getString("imgUrl"))
                        .thumbnail(0.5f)
                        .into((holder.item_img));
                holder.item_code.setText(item.getString("productCode"));
                holder.item_name.setText(item.getString("productName"));
                holder.item_stockVolume.setText(item.getString("stockVolume") + "件");
                JSONArray array = item.getJSONArray("outWhSkuStockSearches");
                LinearLayout skuLayout = view.findViewById(R.id.sku_layout);
                skuLayout.removeAllViews();
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    JSONObject itemData = array.getJSONObject(i);
                    View stock_sku = inflater.inflate(R.layout.item_stock_sku, null);
                    TextView skuName = stock_sku.findViewById(R.id.sku_name);
                    TextView skuNum = stock_sku.findViewById(R.id.sku_num);
                    skuName.setText(itemData.getString("skuProperty"));
                    skuNum.setText(itemData.getString("stockVolume") + "件");
                    skuLayout.addView(stock_sku);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
