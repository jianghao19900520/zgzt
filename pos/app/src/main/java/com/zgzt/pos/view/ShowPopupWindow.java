package com.zgzt.pos.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zgzt.pos.R;

import java.util.List;

/**
 * Created by zixing
 * Date 2018/3/7.
 * desc ：
 */

public class ShowPopupWindow {
    private PopupWindow popupWindow;
    private Context mContext;
    private View anchor;
    private OnPopupWindowItemClickListener mOnPopupWindowItemClickListener;
    private List<String> mItemData;
    private RadioGroup popupWindowGroup;
    private LayoutInflater inflater;
    private boolean isShowing = false;


    public ShowPopupWindow builder(Context mContext, View anchor, int resource) {
        this.mContext = mContext;
        this.anchor = anchor;
        popupWindow = new PopupWindow(mContext);
        inflater = LayoutInflater.from(mContext);
        View popView = inflater.inflate(resource, null);
        popupWindowGroup = popView.findViewById(R.id.popup_window_group);
        popupWindow.setContentView(popView);
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.pop_bg));
        popupWindow.setWidth(anchor.getMeasuredWidth());
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setFocusable(false);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                handler.sendEmptyMessageDelayed(0,200);
            }
        });
        return this;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isShowing = false;
        }
    };

    public ShowPopupWindow show() {
        if (null != popupWindow && !isShowing)  {
            popupWindow.showAsDropDown(anchor, 0, -10);
            isShowing = true;
        }
        return this;
    }

    public ShowPopupWindow setList(List<String> items) {
        this.mItemData = items;
        popupWindowGroup.removeAllViews();
        int len = mItemData.size();
        for (int i = 0; i < len; i++) {
            final int position = i;
            final String item = mItemData.get(i);
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.popup_window_item, popupWindowGroup,false);
            rb.setText(item);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnPopupWindowItemClickListener) {
                        mOnPopupWindowItemClickListener.onItemClick(item, position);
                    }
                }
            });
            popupWindowGroup.addView(rb);
        }
        return this;
    }

    public ShowPopupWindow setOnPopupWindowItemClickListener(OnPopupWindowItemClickListener onPopupWindowItemClickListener) {
        this.mOnPopupWindowItemClickListener = onPopupWindowItemClickListener;
        return this;
    }

    public boolean isShowing(){
        return isShowing;
    }
    public void dismiss() {
        if (null != popupWindow && isShowing) {
            popupWindow.dismiss();
            isShowing = false;
        }
    }

    public interface OnPopupWindowItemClickListener {
        void onItemClick(String item, int position);
    }

}
