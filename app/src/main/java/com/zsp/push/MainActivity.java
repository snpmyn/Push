package com.zsp.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kit.LocalBroadcastManagerKit;

/**
 * @decs: 主页
 * @author: 郑少鹏
 * @date: 2019/5/31 12:11
 */
public class MainActivity extends AppCompatActivity {
    /**
     * action
     */
    public static final String ACTION_MESSAGE_RECEIVED = "com.zsp.push.ACTION_MESSAGE_RECEIVED";
    /**
     * 前台否
     */
    public static boolean isForeground = false;
    /**
     * 消息接收器
     */
    private MessageReceiver messageReceiver;
    /**
     * key
     */
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerMessageReceiver();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManagerKit.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    /**
     * 注册消息接收器
     */
    public void registerMessageReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MESSAGE_RECEIVED);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        LocalBroadcastManagerKit.getInstance(this).registerReceiver(messageReceiver, intentFilter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                    String message = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(KEY_MESSAGE + " : ").append(message).append("\n");
                    if (!TextUtils.isEmpty(extras)) {
                        stringBuilder.append(KEY_EXTRAS + " : ").append(extras).append("\n");
                    }
                    Toast.makeText(MainActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
