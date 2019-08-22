package jpush.display;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.push.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import value.PushConstant;

/**
 * @decs: 极光推送展示页
 * @author: 郑少鹏
 * @date: 2019/5/31 16:09
 */
public class JpushDisplayActivity extends AppCompatActivity {
    @BindView(R.id.jpushDisplayActivityMt)
    MaterialToolbar jpushDisplayActivityMt;
    @BindView(R.id.jpushDisplayActivityTvTitle)
    TextView jpushDisplayActivityTvTitle;
    @BindView(R.id.jpushDisplayActivityTvContent)
    TextView jpushDisplayActivityTvContent;
    /**
     * 通知标题
     */
    private String notificationTitle;
    /**
     * 通知内容
     */
    private String notificationContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush_display);
        ButterKnife.bind(this);
        stepUi();
        initConfiguration();
        setListener();
        startLogic();
    }

    private void stepUi() {
        setSupportActionBar(jpushDisplayActivityMt);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initConfiguration() {
        Intent intent = getIntent();
        if (intent != null) {
            notificationTitle = intent.getStringExtra(PushConstant.NOTIFICATION_TITLE);
            notificationContent = intent.getStringExtra(PushConstant.NOTIFICATION_CONTENT);
        }
    }

    private void setListener() {
        jpushDisplayActivityMt.setNavigationOnClickListener(v -> finish());
    }

    private void startLogic() {
        jpushDisplay();
    }

    /**
     * 极光推送展示
     */
    private void jpushDisplay() {
        jpushDisplayActivityTvTitle.setText(notificationTitle);
        jpushDisplayActivityTvContent.setText(notificationContent);
    }
}