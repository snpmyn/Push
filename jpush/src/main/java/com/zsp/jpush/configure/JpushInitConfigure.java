package com.zsp.jpush.configure;

import android.app.Application;

import com.zsp.jpush.kit.JpushKit;

import timber.log.Timber;

/**
 * Created on 2019/6/26.
 *
 * @author 郑少鹏
 * @desc 极光推送初始化配置
 */
public class JpushInitConfigure {
    public static void initJpush(Application application, boolean debug) {
        // 调试模式
        JpushKit.setDebugMode(debug);
        // 初始
        JpushKit.init(application);
        Timber.d("RegistrationID: %s", JpushKit.getRegistrationId(application));
    }
}
