package application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.zsp.janalytics.configure.JanalyticsInitConfigure;
import com.zsp.jpush.configure.JpushInitConfigure;
import com.zsp.push.BuildConfig;
import com.zsp.utilone.activity.ActivitySuperviseManager;
import com.zsp.utilone.timber.configure.TimberInitConfigure;

/**
 * Created on 2019/5/31.
 *
 * @author 郑少鹏
 * @desc 应用
 */
public class PushApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化配置
        initConfiguration();
    }

    /**
     * 初始化配置
     */
    private void initConfiguration() {
        // timber
        TimberInitConfigure.initTimber(BuildConfig.DEBUG);
        // 全局监听Activity生命周期
        registerActivityListener();
        // 极光推送
        JpushInitConfigure.initJpush(this, BuildConfig.DEBUG);
        // 极光统计
        JanalyticsInitConfigure.initJanalytics(this, BuildConfig.DEBUG);
    }

    /**
     * Activity全局监听
     */
    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                // 添监听到创事件Activity至集合
                ActivitySuperviseManager.pushActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                // 移监听到销事件Activity出集合
                ActivitySuperviseManager.removeActivity(activity);
            }
        });
    }
}
