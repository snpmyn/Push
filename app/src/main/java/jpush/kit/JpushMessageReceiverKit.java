package jpush.kit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zsp.jpush.kit.JpushKit;
import com.zsp.utilone.activity.ActivitySuperviseManager;
import com.zsp.utilone.toast.ToastUtils;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import jpush.display.JpushDisplayActivity;
import value.PushConstant;

/**
 * Created on 2019/6/27.
 *
 * @author 郑少鹏
 * @desc JpushMessageReceiverKit
 */
public class JpushMessageReceiverKit {
    /**
     * OnMessage 执行
     *
     * @param customMessage 自定消息
     */
    public void onMessageExecute(Context context, CustomMessage customMessage) {
        ToastUtils.shortShow(context, customMessage.message);
    }

    /**
     * OnNotifyMessageOpened 执行
     *
     * @param notificationMessage 通知消息
     */
    public void onNotifyMessageOpenedExecute(NotificationMessage notificationMessage) {
        Activity activity = ActivitySuperviseManager.getTopActivityInstance();
        if (activity != null) {
            // 上报用户通知被打开
            JpushKit.reportNotificationOpened(activity.getApplicationContext(), notificationMessage.msgId);
            // 跳转
            Intent intent = new Intent(activity, JpushDisplayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(PushConstant.NOTIFICATION_TITLE, notificationMessage.notificationTitle);
            intent.putExtra(PushConstant.NOTIFICATION_CONTENT, notificationMessage.notificationContent);
            activity.startActivity(intent);
        }
    }
}
