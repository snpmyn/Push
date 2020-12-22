package jpush.kit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.zsp.jpush.kit.LocalBroadcastManagerKit;
import com.zsp.jpush.value.JpushEnum;
import com.zsp.push.MainActivity;
import com.zsp.utilone.activity.ActivitySuperviseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.jpush.android.api.JPushInterface;
import jpush.display.JpushDisplayActivity;
import timber.log.Timber;

/**
 * Created on 2020/12/22
 *
 * @author zsp
 * @desc JpushCustomReceiverKit
 */
public class JpushCustomReceiverKit {
    /**
     * 发送消息
     *
     * @param context 上下文
     * @param message 消息
     */
    public void sendMessage(Context context, Message message) {
        MyHandler myHandler = new MyHandler(context.getMainLooper(), context);
        myHandler.sendMessage(message);
    }

    /**
     * MyHandler
     * <p>
     * 引用关系链 Looper -> MessageQueue -> Message -> Handler -> Activity。
     * 此时退 Activity 因存上引用关系致垃圾回收器将无法回收 Activity 而内存泄漏。
     * <p>
     * 静态内部类 + 弱引用。
     * 静态内部类默不持外部类引用，故改静态内部类即可。同时此处弱引用持 Activity 引用。
     * <p>
     * Activity 退移所有信息后 Handler 与 Activity 生命周期同步。
     */
    private static class MyHandler extends Handler {
        private final WeakReference<Context> weakReference;

        public MyHandler(@NonNull Looper looper, Context context) {
            super(looper);
            this.weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Context context = weakReference.get();
            int arg1 = msg.arg1;
            Bundle bundle = (Bundle) msg.obj;
            if (arg1 == JpushEnum.ACTION_REGISTRATION_ID.getCode()) {
                Timber.d("send the registration Id to your server...");
            } else if (arg1 == JpushEnum.ACTION_MESSAGE_RECEIVED.getCode()) {
                messageReceived(context, bundle);
            } else if (arg1 == JpushEnum.ACTION_NOTIFICATION_RECEIVED.getCode()) {
                Timber.d("ACTION_NOTIFICATION_RECEIVED");
            } else if (arg1 == JpushEnum.ACTION_NOTIFICATION_OPENED.getCode()) {
                notificationOpened(bundle);
            } else if (arg1 == JpushEnum.ACTION_RICHPUSH_CALLBACK.getCode()) {
                Timber.d("据 JPushInterface.EXTRA_EXTRA 内容处理（如打开新页、打开网页等）");
            } else if (arg1 == JpushEnum.ACTION_CONNECTION_CHANGE.getCode()) {
                Timber.d("ACTION_CONNECTION_CHANGE");
            }
        }
    }

    /**
     * 消息被收
     *
     * @param context 上下文
     * @param bundle  数据
     */
    private static void messageReceived(Context context, Bundle bundle) {
        if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent intent = new Intent(MainActivity.ACTION_MESSAGE_RECEIVED);
            intent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!TextUtils.isEmpty(extra)) {
                try {
                    JSONObject extraJson = new JSONObject(extra);
                    if (extraJson.length() > 0) {
                        intent.putExtra(MainActivity.KEY_EXTRAS, extra);
                    }
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
            LocalBroadcastManagerKit.getInstance(context).sendBroadcast(intent);
        }
    }

    /**
     * 通知被打开
     *
     * @param bundle 数据
     */
    private static void notificationOpened(Bundle bundle) {
        Activity activity = ActivitySuperviseManager.getTopActivityInstance();
        if (activity != null) {
            Intent intent = new Intent(activity, JpushDisplayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(bundle);
            /*activity.startActivity(intent);*/
        }
    }
}
