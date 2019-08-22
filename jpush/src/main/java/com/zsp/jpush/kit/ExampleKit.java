package com.zsp.jpush.kit;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.zsp.jpush.value.JpushMagic;
import com.zsp.utilone.thread.ThreadManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

/**
 * @decs: ExampleKit
 * @author: 郑少鹏
 * @date: 2019/5/31 15:05
 */
public class ExampleKit {
    private static final String KEY_APP_KEY = "JPUSH_APP_KEY";
    /**
     * "+"或数字开头
     * 后面内容仅含"-"和数字
     */
    private final static String MOBILE_NUMBER_CHARS = "^[+0-9][-0-9]+$";
    /**
     * Pattern
     */
    private static Pattern p1 = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
    private static Pattern p2 = Pattern.compile("[\\x20-\\x7E]+");

    /**
     * 手机号有效否
     *
     * @param string 字符串
     * @return 手机号有效否
     */
    public static boolean isValidMobileNumber(String string) {
        if (TextUtils.isEmpty(string)) {
            return true;
        }
        Pattern p = Pattern.compile(MOBILE_NUMBER_CHARS);
        Matcher m = p.matcher(string);
        return m.matches();
    }

    /**
     * Tag和Alias有效否
     * <p>
     * Tag和Alias仅数字、英文和中文。
     *
     * @param string 字符串
     * @return Tag和Alias有效否
     */
    public static boolean isValidTagAndAlias(String string) {
        Matcher m = p1.matcher(string);
        return m.matches();
    }

    /**
     * AppKey
     *
     * @param context 上下文
     * @return AppKey
     */
    public static String appKey(Context context) {
        Bundle metaData;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != JpushMagic.INT_TWENTY_FOUR) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {
            Timber.e(e);
        }
        return appKey;
    }

    static void showToast(final String toast, final Context context) {
        ThreadManager.stepScheduledExecutorService().execute(() -> {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            Looper.loop();
        });
    }

    static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private static boolean isReadableAscii(CharSequence string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        try {
            return p2.matcher(string).matches();
        } catch (Throwable e) {
            return true;
        }
    }

    public static String getDeviceId(Context context) {
        return JPushInterface.getUdid(context);
    }
}
