package com.zsp.jpush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.zsp.jpush.value.JpushEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

/**
 * @decs: 极光推送自定接收器
 * 不自定亦不于清单文件配接收器：
 * 1.收推送自定消息不被处理
 * 2.正常收通知（点默打开应用主页）
 * <p>
 * 注：
 * 3.3.0+ 用新消息回调方式。
 * 仍需该 Receiver 接回调，则用新回调方式不重写对应回调法，或重写回调法且调 super。
 * 无需该 Receiver 接回调，则用新回调方式重写对应回调法且不调 super。
 * <p>
 * 3.5.0+ 通知点击默认行为变更如下：
 * 不重写 onNotifyMessageOpened 且没配 Action 点击跳首页。
 * @author: 郑少鹏
 * @date: 2019/5/31 16:17
 */
public class BaseJpushCustomReceiver extends BroadcastReceiver {
    /**
     * 消息
     */
    public Message message;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Timber.d("[JpushCustomReceiver] onReceive - %s, extras: %s", intent.getAction(), printBundle(bundle));
                message = Message.obtain();
                message.obj = bundle;
                if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] Registration Id: %s", bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));
                    message.arg1 = JpushEnum.ACTION_REGISTRATION_ID.getCode();
                } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] 推来的自定消息：%s", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                    message.arg1 = JpushEnum.ACTION_MESSAGE_RECEIVED.getCode();
                } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] 推来的通知");
                    Timber.d("[JpushCustomReceiver] 推来的通知 ID：%s", bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
                    message.arg1 = JpushEnum.ACTION_NOTIFICATION_RECEIVED.getCode();
                } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] 点击打开通知");
                    message.arg1 = JpushEnum.ACTION_NOTIFICATION_OPENED.getCode();
                } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] RICH PUSH CALLBACK: %s", bundle.getString(JPushInterface.EXTRA_EXTRA));
                    message.arg1 = JpushEnum.ACTION_RICHPUSH_CALLBACK.getCode();
                } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                    Timber.d("[JpushCustomReceiver] %s connected state change to %s", intent.getAction(), intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false));
                    message.arg1 = JpushEnum.ACTION_CONNECTION_CHANGE.getCode();
                } else {
                    Timber.d("[JpushCustomReceiver] Unhandled intent - %s", intent.getAction());
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

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
}
