package com.zgzt.pos.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bill99.smartpos.sdk.api.BillPayment;
import com.bill99.smartpos.sdk.api.BillPaymentCallback;
import com.bill99.smartpos.sdk.api.model.BLCPConsumeMsg;
import com.bill99.smartpos.sdk.api.model.BLCashConsumeMsg;
import com.bill99.smartpos.sdk.api.model.BLPaymentRequest;
import com.bill99.smartpos.sdk.api.model.BLScanBSCConsumeMsg;
import com.bumptech.glide.Glide;
import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.module.scanner.ScannerActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zgzt.pos.base.BaseApplication;
import com.zgzt.pos.R;
import com.zgzt.pos.base.Constant;
import com.zgzt.pos.event.GoodsEvent;
import com.zgzt.pos.http.HttpApi;
import com.zgzt.pos.http.HttpCallback;
import com.zgzt.pos.utils.ArithUtils;
import com.zgzt.pos.utils.PreferencesUtil;
import com.zgzt.pos.utils.TimeUtils;
import com.zgzt.pos.utils.ToastUtils;
import com.zgzt.pos.utils.Utils;
import com.zgzt.pos.view.ShowPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 收银台页面
 */
public class CashierDeskActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private TextView title_text;
    private ImageView title_back_btn;
    private TextView user_phone_tv;
    private TextView user_name_tv;
    private LinearLayout goods_item_layout;
    private LinearLayout shopping_guide_btn;
    private TextView shopping_guide;
    private EditText user_input_et;
    private TextView total_money;
    private TextView all_pay_money_tv;
    private TextView total_num;

    private LayoutInflater inflater;
    private ShowPopupWindow showPopupWindow;
    private List<String> guideDatas;    // 导购员数据列表
    private JSONArray shoppingGuides;   //店铺导购员列表
    private String userinput;
    private List<JSONObject> goodsData; //商品信息列表

    private String totalMoney = "0.00";    // 总价
    private String payMoney = "0.00";      // 实付款
    private int totalNum = 0;           // 总数量

    private String orderBelong = "1";       // 1-普通订单，2-赊账订单 默认普通订单
    private JSONArray confirmPosProducts;   // pos机订单商品
    private String shoppingGuideId;         // 导购Id
    private String shoppingGuideName;       // 导购名称
    private String operatorNo;//当前的导购员对应的运营商号
    private String isBackFinance = "1";     // 是否返资金 0-是，1-否
    private String isBackIntegral = "1";    // 是否返积分 0-是，1-否
    private String memberId;               // 会员id
    private String mOrderId;
    private boolean isHaikePay = true;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private int editIndex;
    private static int HAIKE_PAY_CODE = 10109;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_desk);
        mContext = this;
        inflater = LayoutInflater.from(mContext);
        initView();
        initTitle();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != showPopupWindow && showPopupWindow.isShowing()) {
            showPopupWindow.dismiss();
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_text = findViewById(R.id.title_text);
        title_back_btn = findViewById(R.id.title_back_btn);
        user_phone_tv = findViewById(R.id.user_phone_tv);
        user_name_tv = findViewById(R.id.user_name_tv);
        goods_item_layout = findViewById(R.id.goods_item_layout);
        shopping_guide_btn = findViewById(R.id.shopping_guide_btn);
        shopping_guide = findViewById(R.id.shopping_guide);
        user_input_et = findViewById(R.id.user_input_et);
        total_money = findViewById(R.id.total_money);
        all_pay_money_tv = findViewById(R.id.all_pay_money_tv);
        total_num = findViewById(R.id.total_num);
        shopping_guide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerShowPopupWindow();
            }
        });
        user_input_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    queryMember();
                }
                return false;
            }
        });
        findViewById(R.id.ok_btn).setOnClickListener(this);
        findViewById(R.id.scan_btn).setOnClickListener(this);
        findViewById(R.id.code_input_et).setOnClickListener(this);
        findViewById(R.id.clear_goods_btn).setOnClickListener(this);
        findViewById(R.id.balance_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn:
                // 查询会员信息
                queryMember();
                break;
            case R.id.scan_btn:
                // 扫码
                startActivity(new Intent(this, ScannerActivity.class));
                break;
            case R.id.code_input_et:
                // 搜索商品
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("type", "search");
                intent.putExtra("whId", PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID));
                startActivity(intent);
                break;
            case R.id.clear_goods_btn:
                // 清空商品
                clearGoods();
                break;
            case R.id.balance_btn:
                if (TextUtils.isEmpty(user_phone_tv.getText()) || TextUtils.isEmpty(user_name_tv.getText())) {
                    ToastUtils.showShort(BaseApplication.mContext, "请先绑定会员信息");
                } else {
                    // 结算
                    try {
                        if (TextUtils.isEmpty(mOrderId) || mOrderId.equals("null")) {
                            if (goodsData.size() > 0) {
                                balance();
                            } else {
                                ToastUtils.showShort(BaseApplication.mContext, "请先选择商品");
                            }
                        } else {
                            if (isHaikePay) {
                                goHaiKePay();
                            } else {
                                goPay();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        title_text.setText("收银台");
        title_back_btn.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        guideDatas = new ArrayList<>();
        goodsData = new ArrayList<>();
        getClerkData();
        initPay();
    }

    /**
     * 初始化支付
     */
    private void initPay() {
        BillPayment.initPayment(this, new BillPaymentCallback() {
            @Override
            public void onSuccess(String successData) {

            }

            @Override
            public void onFailed(String failedData) {
                ToastUtils.showShort(BaseApplication.mContext, failedData);
            }

            @Override
            public void onCancel(String cancelData) {
                ToastUtils.showShort(BaseApplication.mContext, cancelData);
            }
        });
    }

    /**
     * 处理显示的popupWindow
     */
    private void handlerShowPopupWindow() {
        if (null != showPopupWindow && showPopupWindow.isShowing()) {
            showPopupWindow.dismiss();
        } else {
            if (showPopupWindow == null) {
                showPopupWindow = new ShowPopupWindow()
                        .builder(CashierDeskActivity.this, shopping_guide_btn, R.layout.popup_window_layout)
                        .setOnPopupWindowItemClickListener(new ShowPopupWindow.OnPopupWindowItemClickListener() {
                            @Override
                            public void onItemClick(String item, int position) {
                                shopping_guide.setText(item);
                                try {
                                    JSONObject object = shoppingGuides.getJSONObject(position);
                                    shoppingGuideId = object.getString("id");
                                    shoppingGuideName = object.getString("name");
                                    operatorNo = object.getString("operatorNo");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                showPopupWindow.dismiss();
                            }
                        })
                        .setList(guideDatas);
            }
            showPopupWindow.show();
        }
    }

    /**
     * 验证手机号码
     */
    private void queryMember() {
        userinput = user_input_et.getText().toString().trim();
        if (TextUtils.isEmpty(userinput)) {
            ToastUtils.showShort(BaseApplication.mContext, "请输入手机号码");
            return;
        }
        if (!Utils.isMobileExact(userinput)) {
            ToastUtils.showShort(BaseApplication.mContext, "手机号码格式错误");
            return;
        }
        getVipData();
    }

    /**
     * 获取导购员列表
     */
    private void getClerkData() {
        HttpApi.getClerkData(PreferencesUtil.getInstance(mContext).getString(Constant.WAREHOUSE_ID), new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    setShoppingGuideData(new JSONObject(String.valueOf(result)).getJSONArray("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IOException e) {

            }
        });
    }

    /**
     * 设置店员数据
     */
    private void setShoppingGuideData(JSONArray result) throws JSONException {
        int len = result.length();
        shoppingGuides = result;
        for (int i = 0; i < len; i++) {
            JSONObject object = result.getJSONObject(i);
            if (i == 0) {
                shopping_guide.setText(object.optString("name", ""));
                shoppingGuideId = object.getString("id");
                shoppingGuideName = object.getString("name");
                operatorNo = object.getString("operatorNo");
            }
            guideDatas.add(object.optString("name", ""));
        }
    }

    /**
     * 获取导购员列表
     */
    private void getVipData() {
        HttpApi.getVipData(operatorNo, userinput, new HttpCallback() {
            @Override
            public void onResponse(Object result) {
                try {
                    String data = new JSONObject(String.valueOf(result)).getString("result");
                    if (TextUtils.isEmpty(data)) {
                        ToastUtils.showShort(BaseApplication.mContext, "未查到用户信息");
                    } else {
                        JSONObject object = new JSONObject(String.valueOf(result)).getJSONObject("result");
                        setDataToUser(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IOException e) {

            }
        });
    }

    /**
     * 设置会员数据
     */
    private void setDataToUser(JSONObject result) throws JSONException {
        user_phone_tv.setText("" + result.getString("accountId"));
        user_name_tv.setText("" + result.getString("nickName"));
        memberId = result.getString("id");
    }

    /**
     * 清空商品
     */
    private void clearGoods() {
        mOrderId = "";
        goodsData.clear();
        goods_item_layout.removeAllViews();
        totalMoney = "0.00";
        payMoney = "0.00";
        totalNum = 0;
        setButtomPrice();
    }

    /**
     * 计算价格
     */
    private void setButtomPrice() {
        total_money.setText("合计金额：" + totalMoney);
        all_pay_money_tv.setText(payMoney);
        total_num.setText("数量合计：" + totalNum);
    }

    /**
     * 确认结算
     */
    private void balance() throws JSONException {
        confirmPosProducts = new JSONArray();
        int len = goodsData.size();
        for (int i = 0; i < len; i++) {
            JSONObject item = new JSONObject();
            JSONObject object = goodsData.get(i);

            item.put("productId", object.getString("productId"));
            item.put("skuId", object.getString("id"));
            item.put("dealPrice", object.getString("discountPrice"));
            item.put("purchaseNum", object.getString("purchaseNum"));

            confirmPosProducts.put(item);
        }
        //提交订单
        HttpApi.confirmorder(orderBelong, confirmPosProducts, shoppingGuideId, shoppingGuideName, PreferencesUtil.getInstance(mContext).getString(Constant.USER_ID),
                PreferencesUtil.getInstance(mContext).getString(Constant.LOGIN_NAME), isBackFinance, isBackIntegral, memberId, operatorNo, new HttpCallback() {
                    @Override
                    public void onResponse(Object result) {
                        try {
                            JSONObject object = new JSONObject(String.valueOf(result));
                            mOrderId = object.getString("result");
                            if (TextUtils.isEmpty(mOrderId) || mOrderId.equals("null")) {
                                ToastUtils.showShort(BaseApplication.mContext, object.getString("message"));
                            } else {
                                if (isHaikePay) {
                                    goHaiKePay();
                                } else {
                                    goPay();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IOException e) {

                    }
                });
    }

    /**
     * 支付
     */
    private void goPay() {
        View payHeaderView = inflater.inflate(R.layout.pay_header, null);
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet
                .BottomListSheetBuilder(this, false)
                .addHeaderView(payHeaderView)
                .addItem(R.drawable.unionpay, "银行卡支付", "yhkPay")
                .addItem(R.drawable.qrcode_1, "扫码支付", "scan")
                .addItem(R.drawable.cash, "现金支付", "money")
                .addItem(R.drawable.receivables, "赊账", "shezhang")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        createPay(tag);
                    }
                })
                .build();

        TextView payMoneyTv = payHeaderView.findViewById(R.id.pay_header_money_tv);
        TextView orderCodeTv = payHeaderView.findViewById(R.id.order_header_code_tv);
        payMoneyTv.setText(payMoney);
        orderCodeTv.setText("订单编号：" + mOrderId);

        qmuiBottomSheet.show();
    }

    /**
     * 海科支付
     */
    private void goHaiKePay() {
        View payHeaderView = inflater.inflate(R.layout.pay_header, null);
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet
                .BottomListSheetBuilder(this, false)
                .addHeaderView(payHeaderView)
                .addItem(R.drawable.unionpay, "银行卡支付", "yhkPay")
                .addItem(R.drawable.qrcode_1, "扫码支付", "scan")
//                .addItem(R.mipmap.cash, "现金支付", "money")
                .addItem(R.drawable.receivables, "赊账", "shezhang")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        createPay(tag);
                    }
                })
                .build();

        TextView payMoneyTv = payHeaderView.findViewById(R.id.pay_header_money_tv);
        TextView orderCodeTv = payHeaderView.findViewById(R.id.order_header_code_tv);
        payMoneyTv.setText(payMoney);
        orderCodeTv.setText("订单编号：" + mOrderId);

        qmuiBottomSheet.show();
    }

    /**
     * 构建支付参数，调起支付
     *
     * @param tag
     */
    private void createPay(String tag) {
        if ("yhkPay".equals(tag)) {
            // 银联卡支付
            if (isHaikePay) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(
                        "com.landicorp.android.unionpay",
                        "com.landicorp.android.unionpay.MainActivity"));
                intent.putExtra("transName", "消费");
                intent.putExtra("orderNo", mOrderId);
                String amout = ArithUtils.mul(payMoney, "100", 0);
                int zeroNum = 12 - amout.length();//传递的金额一定要12位数字，不足的需要在前面补0
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < zeroNum; i++) {
                    buffer.append("0");
                }
                buffer.append(amout);
//                intent.putExtra("amount", buffer.toString());
                intent.putExtra("amount", "000000000001");//测试数据
                startActivityForResult(intent, HAIKE_PAY_CODE);

            } else {
                //封装请求消息
                BLCPConsumeMsg sdkMsg = new BLCPConsumeMsg();
                //交易号
                sdkMsg.transId = "zgzt" + TimeUtils.getNowMills();
                //商户订单号
                sdkMsg.orderId = mOrderId;
                //商品名称
                sdkMsg.merchName = "信赢名流商品";
                //金额
                sdkMsg.amt = ArithUtils.mul(payMoney, "100", 0);
                //传入参数
                final BLPaymentRequest<BLCPConsumeMsg> params = new BLPaymentRequest<>();
                params.data = sdkMsg;
                BillPayment.startPayment(CashierDeskActivity.this, params, new BillPaymentCallback() {
                    @Override
                    public void onSuccess(String successData) {
                        ToastUtils.showShort(BaseApplication.mContext, successData);
                        initPayView();
                    }

                    @Override
                    public void onFailed(String failedData) {
                        ToastUtils.showShort(BaseApplication.mContext, failedData);
                    }

                    @Override
                    public void onCancel(String cancelData) {
                        ToastUtils.showShort(BaseApplication.mContext, cancelData);
                    }
                });
            }

        } else if ("scan".equals(tag)) {
            // 扫码支付
            if (isHaikePay) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(
                        "com.landicorp.android.unionpay",
                        "com.landicorp.android.unionpay.MainActivity"));
                intent.putExtra("transName", "二维码支付主扫");
                intent.putExtra("orderNo", mOrderId);
                String amout = ArithUtils.mul(payMoney, "100", 0);
                int zeroNum = 12 - amout.length();//传递的金额一定要12位数字，不足的需要在前面补0
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < zeroNum; i++) {
                    buffer.append("0");
                }
                buffer.append(amout);
//                intent.putExtra("amount", buffer.toString());
                intent.putExtra("amount", "000000000001");//测试数据
                startActivityForResult(intent, HAIKE_PAY_CODE);
            } else {
                //封装请求消息
                BLScanBSCConsumeMsg sdkMsg = new BLScanBSCConsumeMsg();
                //交易号
                sdkMsg.transId = "zgzt" + TimeUtils.getNowMills();
                //商户订单号
                sdkMsg.orderId = mOrderId;
                //商品名称
                sdkMsg.merchName = "信赢名流";
                //金额
                sdkMsg.amt = ArithUtils.mul(payMoney, "100", 0);
                final BLPaymentRequest<BLScanBSCConsumeMsg> params = new BLPaymentRequest<>();
                params.data = sdkMsg;
                BillPayment.startPayment(CashierDeskActivity.this, params, new BillPaymentCallback() {
                    @Override
                    public void onSuccess(String successData) {
                        ToastUtils.showShort(BaseApplication.mContext, successData);
                        initPayView();
                    }

                    @Override
                    public void onFailed(String failedData) {
                        ToastUtils.showShort(BaseApplication.mContext, failedData);
                    }

                    @Override
                    public void onCancel(String cancelData) {
                        ToastUtils.showShort(BaseApplication.mContext, cancelData);
                    }
                });
            }

        } else if ("money".equals(tag)) {

            //封装请求消息
            BLCashConsumeMsg sdkMsg = new BLCashConsumeMsg();
            //币种
            sdkMsg.cur = "CNY";
            //支付交易号
            sdkMsg.transId = "zgzt" + TimeUtils.getNowMills();
            //订单号
            sdkMsg.orderId = mOrderId;
            //金额
            sdkMsg.amt = ArithUtils.mul(payMoney, "100", 0);
            BLPaymentRequest<BLCashConsumeMsg> params = new BLPaymentRequest<>();
            params.data = sdkMsg;
            BillPayment.startPayment(CashierDeskActivity.this, params, new BillPaymentCallback() {
                @Override
                public void onSuccess(String successData) {
                    ToastUtils.showShort(BaseApplication.mContext, successData);
                    initPayView();
                }

                @Override
                public void onFailed(String failedData) {
                    ToastUtils.showShort(BaseApplication.mContext, failedData);
                }

                @Override
                public void onCancel(String cancelData) {
                    ToastUtils.showShort(BaseApplication.mContext, cancelData);
                }
            });

        } else if ("shezhang".equals(tag)) {
            ToastUtils.showShort(BaseApplication.mContext, "赊账");
            initPayView();
        }
    }

    /**
     * 打印界面
     */
    private void initPayView() {
        clearMember();
        QMUIDialog qmuiDialog = new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("打印提示")
                .setMessage("是否打印购物商品信息？")
                .addAction("不打印", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "打印", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        doPrint(goodsData, mOrderId, totalMoney, payMoney, totalNum);
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle);
        qmuiDialog.show();
        qmuiDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clearGoods();
            }
        });
    }

    private void clearMember() {
        user_phone_tv.setText("");
        user_name_tv.setText("");
        user_input_et.setText("");
    }

    /**
     * 构造打印数据，调起打印
     */
    private void doPrint(List<JSONObject> goodsData, String mOrderId, String totalMoney, String payMoney, int totalNum) {
        try {
            JSONObject printData = new JSONObject();
            JSONArray spos = new JSONArray();

            JSONObject title = new JSONObject();
            title.put("content‐type", "txt");
            title.put("size", 3);
            title.put("content", "信赢名流购物清单\r");
            title.put("position", "center");
            title.put("bold", "1");

            String[] dates = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd HH:mm")).split(" ");

            JSONObject titleContent = new JSONObject();
            titleContent.put("content‐type", "txt");
            titleContent.put("size", 2);
            titleContent.put("content", "日  期：" + dates[0] +
                    "  时间：" + dates[1] +
                    "\n订单号：" + mOrderId +
                    "\n收  银：" + PreferencesUtil.getInstance(mContext).getString(Constant.LOGIN_NAME) + "\n");

            JSONObject line = new JSONObject();
            line.put("content‐type", "txt");
            line.put("size", 2);
            line.put("content", "--------------------------------\r");

            JSONObject newLine = new JSONObject();
            newLine.put("content‐type", "txt");
            newLine.put("size", 2);
            newLine.put("content", "\r");

            spos.put(title);
            spos.put(newLine);
            spos.put(titleContent);
            spos.put(line);

            int len = goodsData.size();
            for (int i = 0; i < len; i++) {
                JSONObject item = goodsData.get(i);
                // 商品内容
                JSONObject content = new JSONObject();
                content.put("content‐type", "txt");
                content.put("size", 2);
                String price = item.getString("price");
                String discountPrice = item.getString("discountPrice");
                String discount = ArithUtils.compareTo(price, discountPrice) == 0 ? "" : "\n优 惠 价：￥" + item.getString("discountPrice");
                content.put("content", "商品名称：" + item.getString("productName") +
                        "\n商品规格：" + item.getString("attrVal") +
                        "\n零 售 价：￥" + price +
                        discount +
                        "\n数    量：* " + item.getString("purchaseNum") +
                        "\n金    额：￥" + ArithUtils.mul(item.getString("discountPrice"), item.getString("purchaseNum")) + "\n\r");
                content.put("position", "left");
                spos.put(content);
            }

            //小票底部汇总价格
            JSONObject buttom = new JSONObject();
            buttom.put("content‐type", "txt");
            buttom.put("size", 2);

            String yhje = ArithUtils.compareTo(ArithUtils.sub(totalMoney, payMoney), "0") == 0 ? "" : "优惠金额：-￥" + ArithUtils.sub(totalMoney, payMoney) + "\n";

            buttom.put("content", "数量小计：" + totalNum + "\n" +
                    "总 价 格：￥" + totalMoney + "\n" +
                    "实    付：￥" + payMoney + "\n" + yhje
            );

            //广告区域
//            JSONObject ad = new JSONObject();
//            ad.put("content‐type", "txt");
//            ad.put("size", 3);
//            ad.put("content", "这里是广告区域\r");
//            ad.put("position", "center");
//            ad.put("bold", "1");

            //小票底部空白
            JSONObject blank = new JSONObject();
            blank.put("content‐type", "txt");
            blank.put("size", 2);
            blank.put("content", "\n\n\n\n\r");

            spos.put(line);
            spos.put(buttom);
//            spos.put(line);
//            spos.put(ad);
            spos.put(blank);

            printData.put("spos", spos);

            BillPayment.print(this, printData, null, new BillPaymentCallback() {
                @Override
                public void onSuccess(String successData) {
                }

                @Override
                public void onFailed(String failedData) {
                    ToastUtils.showShort(BaseApplication.mContext, failedData);
                }

                @Override
                public void onCancel(String cancelData) {
                    ToastUtils.showShort(BaseApplication.mContext, cancelData);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addGoods(GoodsEvent item) {
        if (item.getAction() == 1) {
            goodsData.add(item.getItem());
            addGoodsItem(item.getItem());
        } else {
            try {
                update(item.getItem());
                editIndex = -1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addGoodsItem(final JSONObject item) {
        final View itemRoot = inflater.inflate(R.layout.item_goods, null);
        ImageView itemImgIv = itemRoot.findViewById(R.id.item_img);                                 // 商品图片
        TextView itemNameTv = itemRoot.findViewById(R.id.item_name);                                // 商品名称
        TextView itemSkuTv = itemRoot.findViewById(R.id.item_sku);                                  // 商品SKU
        TextView itemPricePayTv = itemRoot.findViewById(R.id.item_price_pay);                       // 实付款
        TextView itemPriceTv = itemRoot.findViewById(R.id.item_price);                              // 商品价格
        TextView itemNumTv = itemRoot.findViewById(R.id.item_num);                                  // 商品数量
        ImageView itemEditBtn = itemRoot.findViewById(R.id.item_edit_btn);                          // 编辑按钮
        TextView itemDelBtn = itemRoot.findViewById(R.id.item_del_btn);                             // 删除按钮

        itemPriceTv.setPaintFlags(itemPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        final int index = goods_item_layout.getChildCount();
        itemEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(CashierDeskActivity.this, GoodsInfoActivity.class);
                    intent.putExtra("data", item.toString());
                    intent.putExtra("price", goodsData.get(index).getString("discountPrice"));
                    intent.putExtra("num", goodsData.get(index).getInt("purchaseNum"));
                    intent.putExtra("action", 2);
                    startActivity(intent);
                    editIndex = index;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Glide.with(this)
                    .applyDefaultRequestOptions(BaseApplication.options110)
                    .load(item.getString("skuImgUrl"))
                    .thumbnail(0.5f)
                    .into(itemImgIv);
            itemSkuTv.setText(item.optString("attrVal", ""));

            final String itemPricePay = item.optString("discountPrice", "0.00");
            final String itemPrice = item.optString("price", "0.00");
            final int itemNum = item.optInt("purchaseNum", 1);

            totalNum += itemNum;
            payMoney = ArithUtils.add(payMoney, ArithUtils.mul(itemPricePay, itemNum + ""));
            totalMoney = ArithUtils.add(totalMoney, ArithUtils.mul(itemPrice, itemNum + ""));
            itemNameTv.setText(item.getString("productName"));
            itemPricePayTv.setText("￥" + itemPricePay);
            itemPriceTv.setText("￥" + itemPrice);
            itemNumTv.setText("X" + itemNum);
            setButtomPrice();

            itemDelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goods_item_layout.removeView(itemRoot);
                    totalNum -= itemNum;
                    payMoney = ArithUtils.sub(payMoney, ArithUtils.mul(itemPricePay, itemNum + ""));
                    totalMoney = ArithUtils.sub(totalMoney, ArithUtils.mul(itemPrice, itemNum + ""));
                    goodsData.remove(item);
                    setButtomPrice();
                    mOrderId = "";
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mOrderId = "";
        goods_item_layout.addView(itemRoot);
    }

    private void update(JSONObject item) throws JSONException {
        if (editIndex > -1) {
            JSONObject oldItem = goodsData.get(editIndex);

            int oldNum = oldItem.getInt("purchaseNum");                                       // 原来的数量
            String oldPricePay = oldItem.getString("discountPrice");                          // 原来的实付价格
            String oldPrice = oldItem.getString("price");                                     // 原来的商品价格

            int newNum = item.getInt("purchaseNum");                                          // 新的数量
            String newPricePay = item.getString("discountPrice");                             // 新的实付价格
            String newPrice = item.getString("price");                                        // 新的商品价格

            totalNum += (newNum - oldNum);                                                         //新的总数量

            payMoney = ArithUtils.add(payMoney, ArithUtils.sub(ArithUtils.mul(newPricePay, newNum + ""), ArithUtils.mul(oldPricePay, oldNum + "")));
            totalMoney = ArithUtils.add(totalMoney, ArithUtils.sub(ArithUtils.mul(newPrice, newNum + ""), ArithUtils.mul(oldPrice, oldNum + "")));

            goodsData.get(editIndex).put("purchaseNum", newNum);
            goodsData.get(editIndex).put("price", newPrice);
            goodsData.get(editIndex).put("discountPrice", newPricePay);
            TextView numTv = goods_item_layout.getChildAt(editIndex).findViewById(R.id.item_num);
            TextView itemPriceTv = goods_item_layout.getChildAt(editIndex).findViewById(R.id.item_price);
            TextView itemPricePayTv = goods_item_layout.getChildAt(editIndex).findViewById(R.id.item_price_pay);
            numTv.setText("X " + newNum);
            itemPriceTv.setText("￥ " + newPrice);
            itemPricePayTv.setText("￥ " + newPricePay);
            setButtomPrice();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HAIKE_PAY_CODE) {
//            Bundle b = data.getExtras();
//            Object[] lstName = b.keySet().toArray();
//            String Str = "";
//            for (int i = 0; i < lstName.length; i++) {
//                String keyName = lstName[i].toString();
//                Str = Str += "[" + keyName + "=" + String.valueOf(b.get(keyName)) + "]";
//            }
//            new AlertDialog.Builder(this).setMessage(Str).show();
            String reason = data.getStringExtra("reason");
            switch (resultCode) {
                case Activity.RESULT_OK:
                    ToastUtils.showShort(BaseApplication.mContext, reason);
                    initPayView();
                    break;
                case Activity.RESULT_CANCELED:
                    if (reason != null) {
                        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }


    }

}
