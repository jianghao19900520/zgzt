package com.zgzt.pos.node;

import java.io.Serializable;

/**
 * Created by zixing
 * Date 2018/10/22.
 * desc ：
 */

public class PayMangerItemNode implements Serializable{

    private String id;
    private int rowNumber;
    private long createdTime;
    private Object createdId;
    private Object createdName;
    private Object updatedTime;
    private Object updatedId;
    private Object updatedName;
    private int statisticsType;
    private String memberId;
    private double orderTotal;
    private int orderCount;
    private double orderFinanceRebate;
    private double orderIntegralRebate;
    private long statisticsTime;
    private int isDel;
    private Object sortNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public Object getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Object createdId) {
        this.createdId = createdId;
    }

    public Object getCreatedName() {
        return createdName;
    }

    public void setCreatedName(Object createdName) {
        this.createdName = createdName;
    }

    public Object getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Object updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Object getUpdatedId() {
        return updatedId;
    }

    public void setUpdatedId(Object updatedId) {
        this.updatedId = updatedId;
    }

    public Object getUpdatedName() {
        return updatedName;
    }

    public void setUpdatedName(Object updatedName) {
        this.updatedName = updatedName;
    }

    public int getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(int statisticsType) {
        this.statisticsType = statisticsType;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public double getOrderFinanceRebate() {
        return orderFinanceRebate;
    }

    public void setOrderFinanceRebate(double orderFinanceRebate) {
        this.orderFinanceRebate = orderFinanceRebate;
    }

    public double getOrderIntegralRebate() {
        return orderIntegralRebate;
    }

    public void setOrderIntegralRebate(double orderIntegralRebate) {
        this.orderIntegralRebate = orderIntegralRebate;
    }

    public long getStatisticsTime() {
        return statisticsTime;
    }

    public void setStatisticsTime(long statisticsTime) {
        this.statisticsTime = statisticsTime;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }

    public Object getSortNo() {
        return sortNo;
    }

    public void setSortNo(Object sortNo) {
        this.sortNo = sortNo;
    }


}
