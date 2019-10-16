package com.zsp.push;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.janalytics.code.JanalyticsCode;
import com.zsp.janalytics.kit.JanalyticsKit;
import com.zsp.janalytics.listener.JanalyticsListener;
import com.zsp.janalytics.value.JanalyticsEnum;
import com.zsp.jpush.kit.JpushKit;
import com.zsp.jpush.kit.LocalBroadcastManagerKit;
import com.zsp.utilone.permission.SoulPermissionUtils;
import com.zsp.utilone.toast.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jiguang.analytics.android.api.Currency;
import timber.log.Timber;

/**
 * @decs: 主页
 * @author: 郑少鹏
 * @date: 2019/5/31 12:11
 */
public class MainActivity extends AppCompatActivity implements JanalyticsListener {
    /**
     * SoulPermissionUtils
     */
    private SoulPermissionUtils soulPermissionUtils;
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
        ButterKnife.bind(this);
        initConfiguration();
        startLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        JpushKit.onResume(this);
        JpushKit.requestPermission(this);
        JanalyticsKit.onPageStart(this, this.getClass().getCanonicalName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        JpushKit.onPause(this);
        JanalyticsKit.onPageEnd(this, this.getClass().getCanonicalName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManagerKit.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private void initConfiguration() {
        soulPermissionUtils = new SoulPermissionUtils();
    }

    private void startLogic() {
        soulPermissionUtils.checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, soulPermissionUtils,
                true, new SoulPermissionUtils.CheckAndRequestPermissionCallBack() {
                    @Override
                    public void onPermissionOk() {
                        // 事件
                        event();
                        // 注册消息接收器
                        registerMessageReceiver();
                    }

                    @Override
                    public void onPermissionDeniedNotRationaleInMiUi(String s) {
                        ToastUtils.shortShow(MainActivity.this, s);
                    }

                    @Override
                    public void onPermissionDeniedNotRationaleWithoutLoopHint(String s) {

                    }
                });
    }

    @OnClick({R.id.mainActivityMbIdentifyAccount, R.id.mainActivityMbDetachAccount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            // 鉴定账户
            case R.id.mainActivityMbIdentifyAccount:
                identifyAccount();
                break;
            // 分离账户
            case R.id.mainActivityMbDetachAccount:
                JanalyticsKit.detachAccount(getApplicationContext(), this);
                break;
            default:
                break;
        }
    }

    /**
     * 事件
     */
    private void event() {
        // 计数事件
        Map<String, String> countExtMap = new HashMap<>(1);
        countExtMap.put("计数key", "计数value");
        JanalyticsKit.onCountEvent(this, "push_count_event_id", countExtMap);
        JanalyticsKit.onCountEvent(this, "push_count_event_id", "计数key", "计数value");
        // 计算事件
        Map<String, String> calculateExtMap = new HashMap<>(1);
        calculateExtMap.put("计算key", "计算value");
        JanalyticsKit.onCalculateEvent(this, "push_calculate_event_id", 0.0D, calculateExtMap);
        JanalyticsKit.onCalculateEvent(this, "push_calculate_event_id", 0.0D, "计算key", "计算value");
        // 登录事件
        Map<String, String> loginExtMap = new HashMap<>(1);
        loginExtMap.put("登录key", "登录value");
        JanalyticsKit.onLoginEvent(this, "login", true, loginExtMap);
        JanalyticsKit.onLoginEvent(this, "login", true,
                "登录keyOne", "登录valueOne", "登录keyTwo", "登录valueTwo");
        // 注册事件
        Map<String, String> registerExtMap = new HashMap<>(1);
        registerExtMap.put("注册key", "注册value");
        JanalyticsKit.onRegisterEvent(this, "register", true, registerExtMap);
        JanalyticsKit.onRegisterEvent(this, "register", true,
                "注册keyOne", "注册valueOne", "注册keyTwo", "注册valueTwo");
        // 浏览事件
        Map<String, String> browseExtMap = new HashMap<>(1);
        browseExtMap.put("浏览key", "浏览value");
        JanalyticsKit.onBrowseEvent(this, "push_browse_content_id", "今日新闻",
                "热点", 2000.0F, browseExtMap);
        JanalyticsKit.onBrowseEvent(this, "push_browse_content_id", "今日新闻",
                "热点", 2000.0F, "浏览key", "浏览value");
        // 购买事件
        Map<String, String> purchaseExtMap = new HashMap<>(1);
        purchaseExtMap.put("购买key", "购买value");
        JanalyticsKit.onPurchaseEvent(this, "push_purchase_goods_id", "短袖",
                100.0D, true, Currency.CNY, "衣服", 1, purchaseExtMap);
        JanalyticsKit.onPurchaseEvent(this, "push_purchase_goods_id", "短袖",
                100.0D, true, Currency.CNY, "衣服", 1, "购买key", "购买value");
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

    /**
     * 消息接收器
     */
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
                Timber.e(e);
            }
        }
    }

    /**
     * 鉴定账户
     */
    private void identifyAccount() {
        JanalyticsKit.identifyAccount(getApplicationContext(),
                "2",
                System.currentTimeMillis(),
                "本人",
                1,
                2,
                "19880920",
                "13673545415",
                "snpmyn@126.com",
                "1001",
                "1002",
                "1003",
                "key",
                "value",
                this);
    }

    /**
     * 回调
     *
     * @param janalyticsEnum 极光统计枚举
     * @param code           码
     * @param msg            消息
     */
    @Override
    public void callback(JanalyticsEnum janalyticsEnum, int code, String msg) {
        if (code == 0) {
            ToastUtils.shortShow(this, janalyticsEnum == JanalyticsEnum.IDENTIFY ? "鉴定账户成功" : "分离用户成功");
        } else {
            JanalyticsCode.messageShow(this, code);
        }
    }
}
