package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.utils.ToastUtils;
import com.zgzt.pos.view.ScaleRelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 主页
 */
public class HomeActivity extends AppCompatActivity implements ScaleRelativeLayout.OnClickListener {

    private Context mContext;
    private Handler mHandler = new Handler();
    private TextView title_text;//标题
    private ImageView title_right_image;//标题栏右侧图标
    private RelativeLayout cashier_desk_btn;//收银台
    private ScaleRelativeLayout online_order_pay_btn;//线上订单支付
    private ScaleRelativeLayout pay_manager_btn;//支付管理
    private ScaleRelativeLayout stock_query_btn;//库存查询
    private ScaleRelativeLayout goods_allocation_btn;//商品调拨
    private boolean isExit = false;//双击退出APP
    private QMUIListPopup mListPopup;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        initView();
        initTitle();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_text = findViewById(R.id.title_text);
        title_right_image = findViewById(R.id.title_right_image);
        cashier_desk_btn = findViewById(R.id.cashier_desk_btn);
        online_order_pay_btn = findViewById(R.id.online_order_pay_btn);
        pay_manager_btn = findViewById(R.id.pay_manager_btn);
        stock_query_btn = findViewById(R.id.stock_query_btn);
        goods_allocation_btn = findViewById(R.id.goods_allocation_btn);
        cashier_desk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,CashierDeskActivity.class));
            }
        });
        online_order_pay_btn.setOnClickListener(this);
        pay_manager_btn.setOnClickListener(this);
        stock_query_btn.setOnClickListener(this);
        goods_allocation_btn.setOnClickListener(this);
    }

    /**
     * 初始化标题栏
     */
    private void initTitle(){
        title_text.setText(getString(R.string.app_name));
        title_right_image.setVisibility(View.GONE);
        title_right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListPopup == null) {

                    String[] listItems = new String[]{
                            "设置",
                            "现金支付",
                            "退出"
                    };
                    final List<String> data = new ArrayList<>();

                    Collections.addAll(data, listItems);

                    ArrayAdapter adapter = new ArrayAdapter<>(mContext, R.layout.simple_list_item, data);

                    mListPopup = new QMUIListPopup(mContext, QMUIPopup.DIRECTION_NONE, adapter);
                    mListPopup.create(QMUIDisplayHelper.dp2px(mContext, 150), QMUIDisplayHelper.dp2px(mContext, 200), new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ToastUtils.showShort(BaseApplication.mContext, data.get(i));
                            mListPopup.dismiss();
                        }
                    });

                }
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_RIGHT);
                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                mListPopup.setPopupTopBottomMinMargin(20);
                mListPopup.show(v);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.online_order_pay_btn:
                ToastUtils.showShort(BaseApplication.mContext, "功能暂未开放，敬请期待");
//                intent = new Intent();
//                intent.setComponent(new ComponentName("com.landicorp.android.shouyinbao","com.landicorp.android.shouyinbao.MainActivity"));
//                intent.putExtra("transName", "扫码支付");
//                startActivityForResult(intent, 0);

//                intent = new Intent(this,ReadCardActivity.class);
//                intent = new Intent(this,PrinterActivity.class);
                break;
            case R.id.pay_manager_btn:
                intent = new Intent(this,PayMangerActivity.class);
                break;
            case R.id.stock_query_btn:
                intent = new Intent(this,StockQueryActivity.class);
                break;
            case R.id.goods_allocation_btn:
                intent = new Intent(this, GoodsAllocationActivity.class);
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    /**
     * 退出APP
     */
    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtils.showShort(BaseApplication.mContext, "再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            System.exit(0);
        }
    }
}
