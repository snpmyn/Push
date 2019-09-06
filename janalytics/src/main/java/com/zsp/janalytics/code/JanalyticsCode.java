package com.zsp.janalytics.code;

import android.content.Context;

import com.zsp.janalytics.R;
import com.zsp.utilone.toast.ToastUtils;

/**
 * Created on 2018/4/28.
 *
 * @author 郑少鹏
 * @desc 极光统计状态码
 */
public class JanalyticsCode {
    /**
     * 消息显示
     *
     * @param context 上下文
     * @param code    码
     */
    public static void messageShow(Context context, int code) {
        switch (code) {
            case 1001:
                ToastUtils.shortShow(context, "accountId为关键参数，不可null或空字符串");
                break;
            case 1002:
                ToastUtils.shortShow(context, "没绑accountID时调解绑接口");
                break;
            case 1003:
                ToastUtils.shortShow(context, "10s内请求频率不可超30次");
                break;
            case 1004:
                ToastUtils.shortShow(context, "accountId不可超255字符");
                break;
            case 1005:
                ToastUtils.shortShow(context, "先调init()初始SDK");
                break;
            case 1101:
                ToastUtils.shortShow(context, "具体原因详查");
                break;
            default:
                ToastUtils.shortShow(context, context.getString(R.string.unknownError));
                break;
        }
    }
}
