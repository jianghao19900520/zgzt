package com.zgzt.pos.event;

import org.json.JSONObject;

/**
 * Created by zixing
 * Date 2018/5/21.
 * desc ：
 */

public class GoodsEvent {
    private JSONObject item;
    private int action;//1.新增 2.修改

    public GoodsEvent(JSONObject item, int action) {
        this.item = item;
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public JSONObject getItem() {
        return item;
    }

    public void setItem(JSONObject item) {
        this.item = item;
    }


}
