package jpush.kit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zsp.utilone.activity.ActivitySuperviseManager;
import com.zsp.utilone.toast.ToastUtils;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import jpush.activity.JpushDisplayActivity;
import jpush.value.JpushConstant;

/**
 * Created on 2019/6/27.
 *
 * @author 郑少鹏
 * @desc MessageReceiverKit
 */
public class MessageReceiverKit {
    /**
     * OnMessage执行
     *
     * @param context       上下文
     * @param customMessage 自定消息
     */
    public void onMessageExecute(Context context, CustomMessage customMessage) {
        ToastUtils.shortShow(context, customMessage.message);
    }

    /**
     * OnNotifyMessageOpened执行
     *
     * @param notificationMessage 通知消息
     */
    public void onNotifyMessageOpenedExecute(NotificationMessage notificationMessage) {
        Activity activity = ActivitySuperviseManager.getTopActivityInstance();
        if (activity != null) {
            Intent intent = new Intent(activity, JpushDisplayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(JpushConstant.NOTIFICATION_TITLE, notificationMessage.notificationTitle);
            intent.putExtra(JpushConstant.NOTIFICATION_CONTENT, notificationMessage.notificationContent);
            activity.startActivity(intent);
        }
    }
}
