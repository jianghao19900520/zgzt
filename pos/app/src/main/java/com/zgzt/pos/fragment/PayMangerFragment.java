package com.zgzt.pos.fragment;


/**
 * 支付管理的fragment
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.node.User;
import com.zgzt.pos.utils.PreferencesUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 支付管理的fragment
 */
public class PayMangerFragment extends Fragment implements OnRefreshListener {

    private Context mContext;
    private LayoutInflater inflater;
    private View mView;
    private SmartRefreshLayout smartRefreshLayout;
    private ListView list_view;
    private int mType;
    private PayMangerAdapter adapter;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.fragment_pay_manger, container, false);
        smartRefreshLayout = mView.findViewById(R.id.smartRefreshLayout);
        list_view = mView.findViewById(R.id.list_view);
        Bundle arguments = getArguments();
        mType = arguments.getInt("type");
        initData();
        getPayList();
        return mView;
    }

    public void initData() {
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setEnableLoadmore(false);
        list = new ArrayList<>();
        adapter = new PayMangerAdapter();
        list_view.setAdapter(adapter);
    }

    public void getPayList() {
        String userId = PreferencesUtil.getInstance(mContext).getString(Constant.USER_ID);
        HttpApi.payList(userId, "1", String.valueOf(mType), new HttpCallback() {
            @Override
            public void onResponse(String result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println("onFailure");
            }
        });
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {

    }

    public class PayMangerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            String sss = (String) getItem(position);
            if (null == sss) return convertView;
            if (null != convertView) {
                holder = (ViewHolder) convertView.getTag();
            }
            if (null == holder) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_pay_manger, null);
                view.setTag(holder);
                initItem(view, holder, sss);
            } else {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
                view.setTag(holder);
                updateItem(view, holder, sss);
            }
            return view;
        }

        private class ViewHolder {
            public TextView mTextView;
        }

        private void initItem(View view, ViewHolder holder, String sss) {
            holder.mTextView = (TextView) view.findViewById(R.id.item_day);
            updateItem(view, holder, sss);
        }

        private void updateItem(View view, ViewHolder holder, String sss) {
            holder.mTextView.setText(sss);
            //            try {
//                    holder.setText(R.id.item_day, TimeUtils.millis2String(item.getLong("statisticsTime"),new SimpleDateFormat("yyyy年MM月dd日")));
//                    holder.setText(R.id.item_money,"￥" + Arith.get2Decimal(item.getString("orderTotal")));
//                    holder.getView(R.id.item_btn).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getActivity(),DayPayActivity.class);
//                            intent.putExtra("data",item.toString());
//                            startActivity(intent);
//                        }
//                    });
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
        }
    }
}
