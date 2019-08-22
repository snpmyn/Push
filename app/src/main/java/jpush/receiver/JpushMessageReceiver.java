package jpush.receiver;

import android.app.Notification;
import android.content.Context;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import jpush.kit.JpushMessageReceiverKit;
import timber.log.Timber;

/**
 * @decs: 极光推送消息接收器
 * 3.0.7+新增回调方式。
 * <p>
 * 1.新消息回调方式相关回调类。
 * 2.新tag与alias操作回调于开发者自定该类子类中触发。
 * 3.手机号设置回调于开发者自定该类子类中触发。
 * 4.新回调方式同旧自定Receiver兼容，配该Receiver后默亦发广播至旧Receiver。
 * 重写onMessage、onNotifyMessageArrived、onNotifyMessageOpened、onMultiActionClicked需调super才发广播至旧Receiver。
 * <p>
 * 该类为回调父类，开发者需继承该类并于清单文件配对应实现类，接口操作结果于所配类下法回调。
 * @author: 郑少鹏
 * @date: 2019/5/31 14:53
 */
public class JpushMessageReceiver extends JPushMessageReceiver {
    private JpushMessageReceiverKit jpushMessageReceiverKit;

    public JpushMessageReceiver() {
        super();
        this.jpushMessageReceiverKit = new JpushMessageReceiverKit();
    }

    @Override
    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
        Timber.d("getNotification");
        return super.getNotification(context, notificationMessage);
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Timber.d("【onMessage】收自定消息回调");
        jpushMessageReceiverKit.onMessageExecute(context, customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        Timber.d("【onNotifyMessageOpened】点通知回调");
        jpushMessageReceiverKit.onNotifyMessageOpenedExecute(notificationMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Timber.d("【onNotifyMessageArrived】收通知回调");
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageDismiss(context, notificationMessage);
        Timber.d("【onNotifyMessageDismiss】清通知回调");
    }

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Timber.d("【onRegister】注册成功回调");
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        Timber.d("【onConnected】长连接状回调");
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        super.onCommandResult(context, cmdMessage);
        Timber.d("【onCommandResult】注册失败回调");
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        Timber.d("【onTagOperatorResult】tag增删查改于此法回调结果");
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
        Timber.d("【onCheckTagOperatorResult】查某tag与当前用户绑状于此法回调结果");
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Timber.d("【onAliasOperatorResult】alias相关操作于此法回调结果");
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
        Timber.d("【onMobileNumberOperatorResult】设手机号于此法回调结果");
    }
}
