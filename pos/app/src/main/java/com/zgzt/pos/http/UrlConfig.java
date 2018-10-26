package com.zgzt.pos.http;

/**
 * Created by zixing
 * Date 2018/10/21.
 * desc ：
 */

public class UrlConfig {

    //基础url
    public static final String BASE_URL = "http://api.2025123.com.cn/";

    //获取token
    public static final String TOKEN_URL = "oauth2/token";
    //登录
    public static final String LOGIN_URL = "business/services/pointofsales/pointofsaleslogin";
    //查询会员
    public static final String SEARCH_VIP_LIST = "business/services/member/modelbyaccountmobile/";
    //提交订单
    public static final String CONFIR_MORDER = "business/services/order/confirmorder";
    //支付信息列表
    public static final String PAY_INFO_LIST = "business/services/ordermemberstatistics/plistby";
    //每日支付店员信息
    public static final String DAY_PAY_CLERK = "business/services/pointofsales/getuserlistbywarehouse/";
    //每日支付明细列表
    public static final String DAY_PAY_DETAILED_LIST = "business/services/msorder/pliststoreorder";
    //商品搜索
    public static final String SEARCH_GOODS = "business/services/whproductstock/stocksearchlist";
    //商品详情
    public static final String GOODS_INFO = "business/services/product/detail/b2c";
    //查询仓库列表
    public static final String SEARCH_STOCK_LIST = "business/services/warehouse/plistby";
    //提交调拨单
    public static final String CONFIRM_WHREQ = "business/services/whrequisition/confirmwhreq";
    //PLISTBY_WHREQ
    public static final String PLISTBY_WHREQ = "business/services/whrequisition/plistby";
    //获取入库单信息
    public static final String STOCK_IN_BILL_INFO = "business/services/whrequisition/model/";
    //获取入库单商品信息
    public static final String STOCK_IN_GOODS_INFO = "business/services/whrequisitionline/plistwhreqline";
    //确认收货
    public static final String CONFIRM_COLLECT_GOODS = "business/services/whrequisition/updatewhreq";



}
