package com.zsp.janalytics.configure;

import android.app.Application;

import com.zsp.janalytics.kit.JanalyticsKit;

/**
 * Created on 2019/9/4.
 *
 * @author 郑少鹏
 * @desc 极光统计初始化配置
 */
public class JanalyticsInitConfigure {
    public static void initJanalytics(Application application, boolean debug) {
        // 调试模式
        JanalyticsKit.setDebugMode(debug);
        // 初始
        JanalyticsKit.init(application);
        // 开CrashLog日志上报
        JanalyticsKit.initCrashHandler(application);
    }
}
