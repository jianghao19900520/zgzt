package com.zgzt.pos.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zixing
 * Date 2018/3/8.
 * desc ：
 */

public abstract class CommAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<JSONObject> mData = null;
    protected final int mItemLayoutId;
    private int checkedPosition = -1;

    /**
     * @param mContext 上下文对象
     * @param mData 数据源
     * @param itemLayoutId listview对应的Item
     */
    public CommAdapter(Context mContext, List<JSONObject> mData, int itemLayoutId) {
        super();
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mData = mData;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void setCheckedPosition(int checkedPosition){
        this.checkedPosition = checkedPosition;
    }

    public int getCheckedPosition(){
        return checkedPosition;
    }
    @Override
    public JSONObject getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommViewHolder viewHolder = CommViewHolder.get(mContext, convertView, parent, mItemLayoutId,position);

        convert(viewHolder, getItem(position));

        return viewHolder.getConvertView();
    }

    public abstract void convert(CommViewHolder holder, JSONObject item);
}
