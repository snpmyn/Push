package jpush.configure;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created on 2019/6/26.
 *
 * @author 郑少鹏
 * @desc 极光推送初始化配置
 */
public class JpushInitConfigure {
    public static void initJpush(Application application) {
        // 调试模式
        JPushInterface.setDebugMode(true);
        // 初始
        JPushInterface.init(application);
    }
}
