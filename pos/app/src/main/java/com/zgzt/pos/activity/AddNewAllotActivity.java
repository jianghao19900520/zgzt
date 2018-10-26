package com.zgzt.pos.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.node.PayMangerNode;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.view.ShowPopupWindow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新增调拨单页面
 */
public class AddNewAllotActivity extends AppCompatActivity {

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
        getPayList();
    }

    private void getPayList() {

    }

}
