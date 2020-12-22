package jpush.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zsp.jpush.receiver.BaseJpushCustomReceiver;

import jpush.kit.JpushCustomReceiverKit;

/**
 * Created on 2020/12/22
 *
 * @author zsp
 * @desc 极光推送自定接收器
 */
public class JpushCustomReceiver extends BaseJpushCustomReceiver {
    private final JpushCustomReceiverKit jpushCustomReceiverKit;

    public JpushCustomReceiver() {
        this.jpushCustomReceiverKit = new JpushCustomReceiverKit();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            jpushCustomReceiverKit.sendMessage(context, message);
        }
    }
}
