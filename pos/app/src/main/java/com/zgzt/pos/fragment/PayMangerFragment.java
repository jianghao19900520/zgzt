package com.zgzt.pos.fragment;


/**
 * 支付管理的fragment
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zgzt.pos.R;
import com.zgzt.pos.activity.DayPayActivity;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.node.PayMangerItemNode;
import com.zgzt.pos.node.PayMangerNode;
import com.zgzt.pos.utils.ArithUtils;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.TimeUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private List<PayMangerItemNode> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        this.inflater = inflater;
        mView = inflater.inflate(R.layout.fragment_pay_manger, container, false);
        smartRefreshLayout = mView.findViewById(R.id.smartRefreshLayout);
        list_view = mView.findViewById(R.id.list_view);
        Bundle arguments = getArguments();
        mType = arguments.getInt("type");
        initData();
        getPayList(null);
        return mView;
    }

    public void initData() {
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setEnableLoadmore(false);
        list = new ArrayList<>();
        adapter = new PayMangerAdapter();
        list_view.setAdapter(adapter);
    }

    public void getPayList(final RefreshLayout refreshlayout) {
        String memberId = PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID);
        HttpApi.payList(memberId, 2, mType + 1, new HttpCallback<PayMangerNode>() {
            @Override
            public void onResponse(PayMangerNode result) {
                if (result.getResult() != null && result.getResult().getList() != null) {
                    list = new ArrayList<>();
                    list.addAll(result.getResult().getList());
                    adapter.notifyDataSetChanged();
                }
                if (refreshlayout != null) refreshlayout.finishRefresh();
            }

            @Override
            public void onFailure(IOException e) {
                if (refreshlayout != null) refreshlayout.finishRefresh();
            }
        });
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getPayList(refreshlayout);
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
            PayMangerItemNode node = (PayMangerItemNode) getItem(position);
            if (null == node) return convertView;
            if (null != convertView) {
                holder = (ViewHolder) convertView.getTag();
            }
            if (null == holder) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.item_pay_manger, null);
                view.setTag(holder);
                initItem(view, holder, node);
            } else {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
                view.setTag(holder);
                updateItem(view, holder, node);
            }
            return view;
        }

        private class ViewHolder {
            public TextView item_day;
            public TextView item_money;
            public RelativeLayout item_btn;
        }

        private void initItem(View view, ViewHolder holder, PayMangerItemNode node) {
            holder.item_day = view.findViewById(R.id.item_day);
            holder.item_money = view.findViewById(R.id.item_money);
            holder.item_btn = view.findViewById(R.id.item_btn);
            updateItem(view, holder, node);
        }

        private void updateItem(View view, ViewHolder holder, final PayMangerItemNode node) {
            holder.item_day.setText(TimeUtils.millis2String(node.getStatisticsTime(), new SimpleDateFormat("yyyy年MM月dd日")));
            holder.item_money.setText("￥" + ArithUtils.get2Decimal(String.valueOf(node.getOrderTotal())));
            holder.item_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DayPayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("node", node);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            });
        }
    }
}
