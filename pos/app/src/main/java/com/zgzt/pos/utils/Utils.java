package com.zgzt.pos.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zixing on 2017/12/20.
 * <p>
 * desc ：
 */

public class Utils {
    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    static WeakReference<Activity> sTopActivityWeakRef;
    static List<Activity> sActivityList = new LinkedList<>();

    private static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            sActivityList.add(activity);
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
        }
    };

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Application app) {
        Utils.sApplication = app;
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    private static void setTopActivityWeakRef(Activity activity) {
        if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

    /**中文转Unicode编码
     * @param str 字符串
     * @return 如果是中文则返回对应的Unicode编码字符串
     */
    public static String chinaToUnicode(String str){
        String result="";

        if (isContainsChinese(str)){

            for (int i = 0; i < str.length(); i++){
                int chr1 = (char) str.charAt(i);
                if(chr1 >= 19968 && chr1 <= 171941){//汉字范围 \u4e00-\u9fa5 (中文)
                    result+="\\u" + Integer.toHexString(chr1);
                }else{
                    result+=str.charAt(i);
                }
            }
        }else {
            result = str;
        }
        return result;
    }

    /**
     * 判断字符串中是否含有中文
     * @param str 字符串
     * @return true 有 false 无
     */

    public static boolean isContainsChinese(String str)
    {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find())    {
            flg = true;
        }
        return flg;
    }

    /**
     * 手机号码，中间4位星号替换
     *
     * @param phone 手机号
     * @return 星号替换的手机号
     */
    public static String phoneNoHide(String phone) {
        // 括号表示组，被替换的部分$n表示第n组的内容
        // 正则表达式中，替换字符串，括号的意思是分组，在replace()方法中，
        // 参数二中可以使用$n(n为数字)来依次引用模式串中用括号定义的字串。
        // "(\d{3})\d{4}(\d{4})", "$1****$2"的这个意思就是用括号，
        // 分为(前3个数字)中间4个数字(最后4个数字)替换为(第一组数值，保持不变$1)(中间为*)(第二组数值，保持不变$2)
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
    /**
     * 银行卡号，保留最后4位，其他星号替换
     *
     * @param cardId 卡号
     * @return 星号替换的银行卡号
     */
    public static String cardIdHide(String cardId) {
        return cardId.replaceAll("\\d{15}(\\d{3})", "**** **** **** **** $1");
    }

    /**
     * 身份证号，中间10位星号替换
     *
     * @param id 身份证号
     * @return 星号替换的身份证号
     */
    public static String idHide(String id) {
        return id.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1** **** ****$2");
    }


    /**
     * 验证手机号（简单）
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobileSimple(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_MOBILE_SIMPLE, input);
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }
    /**
     * 验证手机号（精确）
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobileExact(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_MOBILE_EXACT, input);
    }

    /**
     * 验证电话号码
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isTel(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_TEL, input);
    }

    /**
     * 验证身份证号码15位
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard15(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_ID_CARD15, input);
    }

    /**
     * 验证身份证号码18位
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard18(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_ID_CARD18, input);
    }

    /**
     * 验证邮箱
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isEmail(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_EMAIL, input);
    }

    /**
     * 验证URL
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isURL(final CharSequence input) {
        return isMatch(RegexConstants.REGEX_URL, input);
    }


    /**
     * 打开软键盘
     *
     * @param mEditText
     * @param mContext
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext 上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 判断当前软键盘是否打开
     *
     * @param activity
     * @return
     */
    public static boolean isSoftInputShow(Activity activity) {

        // 虚拟键盘隐藏 判断view是否为空
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            // 隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
//       inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);

            return inputmanger.isActive() && activity.getWindow().getCurrentFocus() != null;
        }
        return false;
    }

}
