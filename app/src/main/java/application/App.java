package application;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.os.Build;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;
import util.ActivitySuperviseUtils;
import util.Logger;

/**
 * Created on 2019/5/31.
 *
 * @author 郑少鹏
 * @desc Application
 */
public class App extends Application {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * <p>Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.</p>
     *
     * <p>If you override this method, be sure to call {@code super.onCreate()}.</p>
     *
     * <p class="note">Be aware that direct boot may also affect callback order on
     * Android {@link Build.VERSION_CODES#N} and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such {@link ContentProvider}, are
     * disabled until user unlock happens, especially when component callback
     * order matters.</p>
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // 全局监听Activity生命周期
        registerActivityListener();
        // debug否
        JPushInterface.setDebugMode(true);
        // 初始
        JPushInterface.init(this);
        Logger.d("RegistrationID", JPushInterface.getRegistrationID(this));
    }

    /**
     * Activity全局监听
     */
    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // 添监听到创事件Activity至集合
                ActivitySuperviseUtils.pushActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // 移监听到销事件Activity出集合
                ActivitySuperviseUtils.finishActivity(activity);
            }
        });
    }
}
