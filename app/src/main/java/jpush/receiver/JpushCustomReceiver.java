package jpush.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.zsp.jpush.kit.LocalBroadcastManagerKit;
import com.zsp.push.MainActivity;
import com.zsp.utilone.activity.ActivitySuperviseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import jpush.display.JpushDisplayActivity;
import timber.log.Timber;

/**
 * @decs: 极光推送自定接收器
 * 不自定亦不于清单文件配接收器：
 * 1.收推送自定消息不被处理
 * 2.正常收通知（点默打开应用主页）
 * <p>
 * 注：
 * 3.3.0+用新消息回调方式。
 * 仍需该Receiver接回调，则用新回调方式不重写对应回调法，或重写回调法且调super。
 * 无需该Receiver接回调，则用新回调方式重写对应回调法且不调super。
 * @author: 郑少鹏
 * @date: 2019/5/31 16:17
 */
public class JpushCustomReceiver extends BroadcastReceiver {
    private static final int ACTION_REGISTRATION_ID = 100;
    private static final int ACTION_MESSAGE_RECEIVED = 101;
    private static final int ACTION_NOTIFICATION_RECEIVED = 102;
    private static final int ACTION_NOTIFICATION_OPENED = 103;
    private static final int ACTION_RICHPUSH_CALLBACK = 104;
    private static final int ACTION_CONNECTION_CHANGE = 105;

    /**
     * 打印数据
     *
     * @param bundle 数据
     * @return 字符串
     */
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Timber.d("This message has no Extra data");
                        continue;
                    }
                    try {
                        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                        if (!TextUtils.isEmpty(extra)) {
                            JSONObject json = new JSONObject(extra);
                            Iterator<String> it = json.keys();
                            while (it.hasNext()) {
                                String myKey = it.next();
                                sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(e);
                    }
                    break;
                default:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.get(key));
                    break;
            }
        }
        return sb.toString();
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

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Timber.d("[JpushCustomReceiver] onReceive - %s, extras: %s", intent.getAction(), printBundle(bundle));
                MyHandler myHandler = new MyHandler(context);
                Message message = Message.obtain();
                message.obj = bundle;
                if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] Registration Id: %s", bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));
                    message.arg1 = ACTION_REGISTRATION_ID;
                } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] 推来的自定消息：%s", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                    message.arg1 = ACTION_MESSAGE_RECEIVED;
                } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] 推来的通知");
                    Timber.d("[JpushCustomReceiver] 推来的通知ID：%s", bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
                    message.arg1 = ACTION_NOTIFICATION_RECEIVED;
                } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] 点击打开通知");
                    message.arg1 = ACTION_NOTIFICATION_OPENED;
                } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] RICH PUSH CALLBACK: %s", bundle.getString(JPushInterface.EXTRA_EXTRA));
                    message.arg1 = ACTION_RICHPUSH_CALLBACK;
                } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] %s connected state change to %s", intent.getAction(), intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false));
                    message.arg1 = ACTION_CONNECTION_CHANGE;
                } else {
                    Timber.d("[JpushCustomReceiver] Unhandled intent - %s", intent.getAction());
                }
                myHandler.sendMessage(message);
            }
        } catch (Exception e) {
            Timber.e(e);
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

    /**
     * MyHandler
     * <p>
     * 引用关系链Looper -> MessageQueue -> Message -> Handler -> Activity。
     * 此时退Activity因存上引用关系致垃圾回收器将无法回收Activity而内存泄漏。
     * <p>
     * 静态内部类+弱引用。
     * 静态内部类默不持外部类引用，故改静态内部类即可。同时此处弱引用持Activity引用。
     * <p>
     * Activity退移所有信息后Handler与Activity生命周期同步。
     */
    private static class MyHandler extends Handler {
        private WeakReference<Context> weakReference;

        MyHandler(Context context) {
            weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Context context = weakReference.get();
            int arg1 = msg.arg1;
            Bundle bundle = (Bundle) msg.obj;
            switch (arg1) {
                case ACTION_REGISTRATION_ID:
                    // send the registration Id to your server...
                    break;
                case ACTION_MESSAGE_RECEIVED:
                    messageReceived(context, bundle);
                    break;
                case ACTION_NOTIFICATION_RECEIVED:
                    // ACTION_NOTIFICATION_RECEIVED
                    break;
                case ACTION_NOTIFICATION_OPENED:
                    notificationOpened(bundle);
                    break;
                case ACTION_RICHPUSH_CALLBACK:
                    // 据JPushInterface.EXTRA_EXTRA内容处理（如打开新页、打开网页等）
                    break;
                case ACTION_CONNECTION_CHANGE:
                    // ACTION_CONNECTION_CHANGE
                    break;
                default:
                    break;
            }
        }
    }
}
