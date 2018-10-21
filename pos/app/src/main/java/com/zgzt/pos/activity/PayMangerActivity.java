package com.zgzt.pos.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.zgzt.pos.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.fragment.PayMangerFragment;
import com.zgzt.pos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 支付管理页面
 */
public class PayMangerActivity extends AppCompatActivity {

    private Context mContext;
    private TextView title_text;
    private ImageView title_back_btn;
    private QMUITabSegment tabSegment;
    private ViewPager contentViewPager;
    private ContentPage mDestPage = ContentPage.Item1;
    private List<Fragment> pagerList;
    private FragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_manger);
        mContext = this;
        initView();
        initTitle();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_text = findViewById(R.id.title_text);
        title_back_btn = findViewById(R.id.title_back_btn);
        tabSegment = findViewById(R.id.tabSegment);
        contentViewPager = findViewById(R.id.contentViewPager);
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        title_text.setText(getString(R.string.app_name));
        title_text.setText("支付管理");
        title_back_btn.setVisibility(View.VISIBLE);
        title_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayMangerActivity.this.finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){
        pagerList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            PayMangerFragment month = new PayMangerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type",i);
            month.setArguments(bundle);
            pagerList.add(month);
        }

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pagerList.get(position);
            }

            @Override
            public int getCount() {
                return pagerList.size();
            }
        };

        initTabAndPager();
    }

    /**
     * TAB栏设置
     */
    private void initTabAndPager() {
        contentViewPager.setAdapter(mPagerAdapter);
        contentViewPager.setCurrentItem(mDestPage.getPosition(), false);

        tabSegment.setHasIndicator(true);
        tabSegment.setIndicatorPosition(false);
        tabSegment.setIndicatorWidthAdjustContent(true);
        tabSegment.addTab(new QMUITabSegment.Tab(" 本月 "));
        tabSegment.addTab(new QMUITabSegment.Tab(" 上月 "));

        tabSegment.setupWithViewPager(contentViewPager, false);
        tabSegment.setMode(QMUITabSegment.MODE_FIXED);
        tabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                tabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                tabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }

    public enum ContentPage {
        Item1(0),
        Item2(1);
        public static final int SIZE = 2;
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return Item1;
                case 1:
                    return Item2;
                default:
                    return Item1;
            }
        }

        public int getPosition() {
            return position;
        }
    }

}
