package com.zgzt.pos.event;

/**
 * Created by zixing
 * Date 2018/5/30.
 * desc ：
 */

public class WarehouseEvent {
    String whId;
    String whName;

    public WarehouseEvent(String whId, String whName) {
        this.whId = whId;
        this.whName = whName;
    }

    public String getWhId() {
        return whId;
    }

    public void setWhId(String whId) {
        this.whId = whId;
    }

    public String getWhName() {
        return whName;
    }

    public void setWhName(String whName) {
        this.whName = whName;
    }
}
