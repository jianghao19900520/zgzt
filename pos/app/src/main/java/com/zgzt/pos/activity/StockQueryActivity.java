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
import android.widget.ListView;
import android.widget.TextView;

import com.landicorp.module.scanner.ScannerActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.fragment.PayMangerFragment;
import com.zgzt.pos.node.GoodsNode;
import com.zgzt.pos.utils.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存查询页面
 */
public class StockQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView title_back_btn;//标题栏返回键
    private TextView title_text;//标题
    private TextView title_right_text_image;//标题栏右侧图文键

    private EditText code_input_et;//输入框
    private SmartRefreshLayout smart_refresh_layout;//下拉刷新控件
    private ListView list_view;
    private LayoutInflater inflater;

    private String searchKey;
    private List<GoodsNode> goodsList;
    private StockQueryAdapter adapter;

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
        goodsList = new ArrayList<>();
        list_view.setAdapter(adapter);
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
        goodsList.add(new GoodsNode());
        goodsList.add(new GoodsNode());
        goodsList.add(new GoodsNode());
        goodsList.add(new GoodsNode());
        goodsList.add(new GoodsNode());
        goodsList.add(new GoodsNode());
        goodsList.add(new GoodsNode());
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
//                intent.putExtra("whId",whId);
//                intent.putExtra("whName",whName);
                startActivity(intent);
                break;
            case R.id.scan_btn:
                startActivity(new Intent(this, ScannerActivity.class));
                break;
            case R.id.search_btn:
                if (checkInput()) {
                    initData();
                }
                break;
        }
    }

    /**
     * 检测输入内容
     */
    private boolean checkInput() {
        searchKey = code_input_et.getText().toString().trim();
        if (TextUtils.isEmpty(searchKey)) {
            ToastUtils.showShort(BaseApplication.mContext, "请输入搜索内容");
            return false;
        }
        return true;
    }

    public class StockQueryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return goodsList.size();
        }

        @Override
        public Object getItem(int position) {
            return goodsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            StockQueryAdapter.ViewHolder holder = null;
//            String sss = (String) getItem(position);
            String sss = "sss";
            if (null == sss) return convertView;
            if (null != convertView) {
                holder = (StockQueryAdapter.ViewHolder) convertView.getTag();
            }
            if (null == holder) {
                holder = new StockQueryAdapter.ViewHolder();
                view = inflater.inflate(R.layout.item_pay_manger, null);
                view.setTag(holder);
                initItem(view, holder, sss);
            } else {
                view = convertView;
                holder = (StockQueryAdapter.ViewHolder) convertView.getTag();
                view.setTag(holder);
                updateItem(view, holder, sss);
            }
            return view;
        }

        private class ViewHolder {
            public TextView mTextView;
        }

        private void initItem(View view, StockQueryAdapter.ViewHolder holder, String sss) {
            holder.mTextView = (TextView) view.findViewById(R.id.item_day);
            updateItem(view, holder, sss);
        }

        private void updateItem(View view, StockQueryAdapter.ViewHolder holder, String sss) {
            holder.mTextView.setText(sss);
        }
    }
}
