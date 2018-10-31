package com.zgzt.pos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.base.CommAdapter;
import com.zgzt.pos.base.CommViewHolder;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.event.GoodsEvent;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.DialogUtils;
import com.zgzt.pos.utils.ToastUtils;
import com.zgzt.pos.utils.Utils;

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
 * 搜索页面
 */
public class SearchActivity extends AppCompatActivity implements OnRefreshLoadmoreListener {

    private Context mContext;
    TextView title_text;
    ImageView title_back_btn;
    EditText search_input_et;
    ListView list_view;
    SmartRefreshLayout refreshLayout;

    private String mType;// 类型：search 搜索商品 scan 扫描二维码

    private List<JSONObject> dataList;
    private CommAdapter<String> adapter;

    private String keywords;
    private int pageIndex = 0;
    private String whId;
    private String what;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
        finish();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        title_text = findViewById(R.id.title_text);
        title_back_btn = findViewById(R.id.title_back_btn);
        search_input_et = findViewById(R.id.search_input_et);
        list_view = findViewById(R.id.list_view);
        refreshLayout = findViewById(R.id.refreshLayout);
        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keywords = search_input_et.getText().toString().trim();
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                getSearchGoodsList(true);
            }
        });
    }

    /**
     * 初始化标题栏
     */
    public void initTitle() {
        title_text.setText("商品搜索");
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        if (intent.hasExtra("what")) {
            what = intent.getStringExtra("what");
        }
        if (intent.hasExtra("whId")) {
            whId = intent.getStringExtra("whId");
            if (TextUtils.isEmpty(whId)) {
                ToastUtils.showShort(BaseApplication.mContext, "");
            }
        } else {
            ToastUtils.showShort(BaseApplication.mContext, "请传入仓库id");
        }

        title_back_btn.setVisibility(View.VISIBLE);
        title_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
        if ("scan".equals(mType)) {
            keywords = intent.getStringExtra("code");
        }
    }

    /**
     * 初始化数据
     */
    public void initData() {
        refreshLayout.setOnRefreshLoadmoreListener(this);
        refreshLayout.setEnableLoadmore(false);
        dataList = new ArrayList<>();
        adapter = new CommAdapter<String>(this, dataList, R.layout.item_product) {
            @Override
            public void convert(CommViewHolder holder, JSONObject item) {
                try {
                    holder.setText(R.id.item_code_tv, item.getString("productCode"));
                    holder.setText(R.id.item_name_tv, item.getString("productName"));
                    holder.setImageByUrl(R.id.item_img, item.getString("imgUrl"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject itemData = (JSONObject) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchActivity.this, GoodsInfoActivity.class);
                intent.putExtra("data", itemData.toString());
                intent.putExtra("action", 1);
                if (TextUtils.isEmpty(what)) {
                    intent.putExtra("what", "pay");
                } else {
                    intent.putExtra("what", what);
                }
                startActivity(intent);
            }
        });
        if ("scan".equals(mType)) {
            getSearchGoodsList(true);
        }

        search_input_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //TODO:你自己的业务逻辑
                    if (Utils.isSoftInputShow(SearchActivity.this)) {
                        Utils.closeKeybord(search_input_et, SearchActivity.this);
                    }
                    keywords = search_input_et.getText().toString().trim();
                    getSearchGoodsList(true);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageIndex++;
        getSearchGoodsList(false);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageIndex = 0;
        getSearchGoodsList(false);
    }

    /**
     * 商品搜索
     */
    private void getSearchGoodsList(boolean show) {
        if(show){
            DialogUtils.getInstance().show(mContext);
        }
        HttpApi.searchGoods(pageIndex, Constant.PAGE_SIZE, whId, keywords, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                if (pageIndex == 0) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        handlerData(jsonObject.getJSONObject("result"));
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
                if (pageIndex == 0) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadmore();
                }
                DialogUtils.getInstance().dismiss();
            }
        });
    }

    /**
     * 处理搜索数据
     */
    private void handlerData(JSONObject data) throws JSONException {
        int rowCount = data.getInt("recordCount");
//        if (rowCount > 0) {
//            empty.setVisibility(View.GONE);
//        } else {
//            empty.setVisibility(View.VISIBLE);
//        }
        if (pageIndex == 0) {
            dataList.clear();
        }
        if ((pageIndex + 1) * Constant.PAGE_SIZE < rowCount) {
            refreshLayout.setEnableLoadmore(true);
        } else {
            refreshLayout.setEnableLoadmore(false);
        }
        JSONArray list = data.getJSONArray("list");
        int len = list.length();
        for (int i = 0; i < len; i++) {
            dataList.add(list.getJSONObject(i));
        }
        adapter.notifyDataSetChanged();
    }

}
