package jpush.receiver;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import jpush.kit.MessageReceiverKit;

/**
 * @decs: 消息接收器
 * 3.0.7后新增回调方式。
 * <p>
 * 1.新消息回调方式相关回调类。
 * 2.新tag与alias操作回调于开发者自定该类子类中触发。
 * 3.手机号设置回调于开发者自定该类子类中触发。
 * 4.3.3.0后通该类处理事件后，原自定接收器接收事件，将不再回调至自定接收器，而回调至JPushMessageReceiver，否仍回调至自定接收器。
 * <p>
 * 该类为回调父类，开发者需继承该类并于清单文件配对应实现类，接口操作结果会于所配类下法回调。
 * @author: 郑少鹏
 * @date: 2019/5/31 14:53
 */
public class MessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "MessageReceiver";
    private MessageReceiverKit messageReceiverKit;

    public MessageReceiver() {
        super();
        this.messageReceiverKit = new MessageReceiverKit();
    }

    @Override
    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
        Log.d(TAG, "getNotification");
        return super.getNotification(context, notificationMessage);
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.d(TAG, "【onMessage】收自定消息回调");
        messageReceiverKit.onMessageExecute(context, customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        Log.d(TAG, "【onNotifyMessageOpened】点通知回调");
        messageReceiverKit.onNotifyMessageOpenedExecute(notificationMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Log.d(TAG, "【onNotifyMessageArrived】收通知回调");
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageDismiss(context, notificationMessage);
        Log.d(TAG, "【onNotifyMessageDismiss】清通知回调");
    }

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Log.d(TAG, "【onRegister】注册成功回调");
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        Log.d(TAG, "【onConnected】长连接状回调");
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        super.onCommandResult(context, cmdMessage);
        Log.d(TAG, "【onCommandResult】注册失败回调");
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        Log.d(TAG, "【onTagOperatorResult】tag增删查改于此法回调结果");
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
        Log.d(TAG, "【onCheckTagOperatorResult】查某tag与当前用户绑状于此法回调结果");
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Log.d(TAG, "【onAliasOperatorResult】alias相关操作于此法回调结果");
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
        Log.d(TAG, "【onMobileNumberOperatorResult】设手机号于此法回调结果");
    }
}
