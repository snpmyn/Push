package util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2017/9/19.
 *
 * @author 郑少鹏
 * @desc 活动管理和退出应用
 * Application：
 * 结合{@link application.App#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)}。
 * {@link application.App#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)}之onActivityCreated当{@link AppCompatActivity#onCreate(Bundle, PersistableBundle)}时执行，android:launchMode="singleTask"时不执行。
 * @link application.App#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)}onActivityDestroyed当{@link AppCompatActivity#finish()} (Bundle, PersistableBundle)}时执行，android:launchMode="singleTask"时不执行。
 * <p>
 * 基类：
 * 基类之onCreate推当前Activity至Activity管理容器，需时遍历容器并finish所有Activity。
 */
public class ActivitySuperviseUtils {
    private static final List<Activity> ACTIVITIES = Collections.synchronizedList(new LinkedList<>());

    /**
     * 添Activity至堆栈
     *
     * @param activity 活动
     */
    public static void pushActivity(Activity activity) {
        ACTIVITIES.add(activity);
        Logger.e("活动数", String.valueOf(ACTIVITIES.size()));
        for (int i = 0; i < ACTIVITIES.size(); i++) {
            Logger.e("概览", ACTIVITIES.get(i).getClass().getSimpleName());
        }
        Logger.e("推入", ACTIVITIES.get(ACTIVITIES.size() - 1).getClass().getSimpleName());
    }

    /**
     * 结束指定Activity
     *
     * @param activity 活动
     */
    public static void finishActivity(Activity activity) {
        if (ACTIVITIES == null || ACTIVITIES.isEmpty()) {
            return;
        }
        if (activity != null) {
            ACTIVITIES.remove(activity);
            activity.finish();
        }
    }

    /**
     * 栈顶Activity实例
     *
     * @return 栈顶Activity实例
     */
    public static Activity getTopActivityInstance() {
        Activity mBaseActivity;
        synchronized (ACTIVITIES) {
            final int size = ACTIVITIES.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = ACTIVITIES.get(size);
        }
        return mBaseActivity;
    }
}
