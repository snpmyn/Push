package util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import value.Magic;

/**
 * @decs: ExampleUtils
 * @author: 郑少鹏
 * @date: 2019/5/31 15:05
 */
public class ExampleUtils {
    public static final String PREFS_NAME = "JPUSH_EXAMPLE";
    public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
    public static final String PREFS_START_TIME = "PREFS_START_TIME";
    public static final String PREFS_END_TIME = "PREFS_END_TIME";
    private static final String TAG = "ExampleUtils";
    private static final String KEY_APP_KEY = "JPUSH_APP_KEY";
    /**
     * "+"或数字开头
     * 后面内容仅含"-"和数字
     */
    private final static String MOBILE_NUMBER_CHARS = "^[+0-9][-0-9]+$";

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
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(string);
        return m.matches();
    }

    /**
     * AppKey
     *
     * @param context 上下文
     * @return AppKey
     */
    public static String appKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != Magic.INT_TWENTITY_FOUR) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {
            Logger.e(TAG, e.getMessage());
        }
        return appKey;
    }

    /**
     * 版本名
     *
     * @param context 上下文
     * @return 版本名
     */
    public static String versionName(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return manager.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    static void showToast(final String toast, final Context context) {
        new Thread(() -> {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }).start();
    }

    static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String getImei(Context context, String imei) {
        String ret = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getDeviceId();
        } catch (Exception e) {
            Logger.e(ExampleUtils.class.getSimpleName(), e.getMessage());
        }
        if (isReadableASCII(ret)) {
            return ret;
        } else {
            return imei;
        }
    }

    private static boolean isReadableASCII(CharSequence string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        try {
            Pattern p = Pattern.compile("[\\x20-\\x7E]+");
            return p.matcher(string).matches();
        } catch (Throwable e) {
            return true;
        }
    }

    public static String getDeviceId(Context context) {
        return JPushInterface.getUdid(context);
    }
}
