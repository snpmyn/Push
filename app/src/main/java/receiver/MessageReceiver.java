package receiver;

import android.content.Context;
import android.widget.Toast;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import util.Logger;
import util.TagAliasOperatorHelper;

/**
 * @decs: 消息接收器
 * 3.0.7后新增回调方式。
 * <p>
 * 1.新消息回调方式中相关回调类。
 * 2.新tag与alias操作回调会于开发者自定该类子类中触发。
 * 3.手机号设置回调会于开发者自定该类子类中触发。
 * 4.3.3.0后通该类处理事件后，原通自定接收器接收事件，将不再回调至自定接收器，而是回调至JPushMessageReceiver，否仍回调至自定接收器。
 * <p>
 * 该类为回调父类，开发者需继承该类并于清单文件配对应实现类，接口操作结果会于所配类下法回调。
 * @author: 郑少鹏
 * @date: 2019/5/31 14:53
 */
public class MessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "MessageReceiver";

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
        Logger.d(TAG, "onTagOperatorResult");
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context, jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
        Logger.d(TAG, "onCheckTagOperatorResult");
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
        Logger.d(TAG, "onAliasOperatorResult");
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {

        // 命名待定

        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
        Logger.d(TAG, "onMobileNumberOperatorResult");
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Toast.makeText(context, customMessage.message, Toast.LENGTH_SHORT).show();
        super.onMessage(context, customMessage);
        Logger.d(TAG, "onMessage");
    }
}
