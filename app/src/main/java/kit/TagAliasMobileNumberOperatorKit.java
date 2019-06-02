package kit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import util.Logger;
import value.Magic;

/**
 * @decs: TagAliasMobileNumberOperatorKit
 * @author: 郑少鹏
 * @date: 2019/5/31 14:59
 */
public class TagAliasMobileNumberOperatorKit {
    private static final String TAG = "TagAliasMobileNumberOperatorKit";
    /**
     * 增
     */
    private static final int ACTION_ADD = 1;
    /**
     * 覆
     */
    private static final int ACTION_SET = 2;
    /**
     * 删部分
     */
    private static final int ACTION_DELETE = 3;
    /**
     * 删所有
     */
    private static final int ACTION_CLEAN = 4;
    /**
     * 查
     */
    private static final int ACTION_GET = 5;
    private static final int ACTION_CHECK = 6;
    private static final int DELAY_SEND_ACTION = 1;
    private static final int DELAY_SET_MOBILE_NUMBER_ACTION = 2;
    private static TagAliasMobileNumberOperatorKit mInstance;
    private static int sequence = 1;
    private Context context;
    private SparseArray<Object> setActionCache = new SparseArray<>();
    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj instanceof TagAliasBean) {
                        Logger.i(TAG, "on delay time");
                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        setActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            handleAction(context, sequence, tagAliasBean);
                        } else {
                            Logger.e(TAG, "#unexcepted - context was null");
                        }
                    } else {
                        Logger.w(TAG, "#unexcepted - msg obj was incorrect");
                    }
                    break;
                case DELAY_SET_MOBILE_NUMBER_ACTION:
                    if (msg.obj instanceof String) {
                        Logger.i(TAG, "retry set mobile number");
                        sequence++;
                        String mobileNumber = (String) msg.obj;
                        setActionCache.put(sequence, mobileNumber);
                        if (context != null) {
                            handleAction(context, sequence, mobileNumber);
                        } else {
                            Logger.e(TAG, "#unexcepted - context was null");
                        }
                    } else {
                        Logger.w(TAG, "#unexcepted - msg obj was incorrect");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private TagAliasMobileNumberOperatorKit() {

    }

    public static TagAliasMobileNumberOperatorKit getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasMobileNumberOperatorKit.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasMobileNumberOperatorKit();
                }
            }
        }
        return mInstance;
    }

    private void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public Object get(int sequence) {
        return setActionCache.get(sequence);
    }

    public Object remove(int sequence) {
        return setActionCache.get(sequence);
    }

    private void put(int sequence, Object tagAliasBean) {
        setActionCache.put(sequence, tagAliasBean);
    }

    private void handleAction(Context context, int sequence, String mobileNumber) {
        put(sequence, mobileNumber);
        Logger.d(TAG, "sequence:" + sequence + ",mobileNumber:" + mobileNumber);
        JPushInterface.setMobileNumber(context, sequence, mobileNumber);
    }

    /**
     * 处理
     *
     * @param context      上下文
     * @param sequence     序列
     * @param tagAliasBean TagAlias实体
     */
    private void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            Logger.w(TAG, "tagAliasBean was null");
            return;
        }
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {
                case ACTION_GET:
                    JPushInterface.getAlias(context, sequence);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                    break;
                default:
                    Logger.w(TAG, "unSupport alias action type");
            }
        } else {
            switch (tagAliasBean.action) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_CHECK:
                    // 一次仅check一tag
                    Object[] objects = tagAliasBean.tags.toArray();
                    if (objects != null && objects.length > 0) {
                        String tag = (String) objects[0];
                        JPushInterface.checkTagBindState(context, sequence, tag);
                    }
                    break;
                case ACTION_GET:
                    JPushInterface.getAllTags(context, sequence);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    Logger.w(TAG, "unSupport tag action type");
            }
        }
    }

    private boolean retryActionIfNeeded(int errorCode, TagAliasBean tagAliasBean) {
        if (!ExampleKit.isConnected(context)) {
            Logger.w(TAG, "no network");
            return false;
        }
        // 错误码6002超时
        // 错误码6014服务器繁忙
        // 建延迟重试
        if (errorCode == Magic.INT_SIX_THOUSAND_TWO || errorCode == Magic.INT_SIX_THOUSAND_FOURTEEN) {
            Logger.d(TAG, "need retry");
            if (tagAliasBean != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasBean;
                delaySendHandler.sendMessageDelayed(message, 1000 * 60);
                String logs = getRetryStr(tagAliasBean.isAliasAction, tagAliasBean.action, errorCode);
                ExampleKit.showToast(logs, context);
                return true;
            }
        }
        return false;
    }

    private boolean retrySetMobileNumberActionIfNeeded(int errorCode, String mobileNumber) {
        if (!ExampleKit.isConnected(context)) {
            Logger.w(TAG, "no network");
            return false;
        }
        // 错误码6002超时,
        // 错误码6024服务器内错
        // 建稍后重试
        if (errorCode == Magic.INT_SIX_THOUSAND_TWO || errorCode == Magic.INT_SIX_THOUSAND_TWENTY_FOUR) {
            Logger.d(TAG, "need retry");
            Message message = new Message();
            message.what = DELAY_SET_MOBILE_NUMBER_ACTION;
            message.obj = mobileNumber;
            delaySendHandler.sendMessageDelayed(message, 1000 * 60);
            String str = "Failed to set mobile number due to %s. Try again after 60s.";
            str = String.format(Locale.ENGLISH, str, (errorCode == 6002 ? "timeout" : "server internal error”"));
            ExampleKit.showToast(str, context);
            return true;
        }
        return false;
    }

    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
        String str = "Failed to %s %s due to %s. Try again after 60s.";
        str = String.format(Locale.ENGLISH, str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"), (errorCode == 6002 ? "timeout" : "server too busy"));
        return str;
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_ADD:
                return "add";
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
            case ACTION_GET:
                return "get";
            case ACTION_CLEAN:
                return "clean";
            case ACTION_CHECK:
                return "check";
            default:
                break;
        }
        return "unknown operation";
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        Logger.i(TAG, "tags size:" + jPushMessage.getTags().size());
        init(context);
        // 据sequence从之前操作缓存获缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ExampleKit.showToast("获缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "action - modify tag Success,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tags success";
            Logger.i(TAG, logs);
            ExampleKit.showToast(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags";
            if (jPushMessage.getErrorCode() == Magic.INT_SIX_THOUSAND_EIGHTEEN) {
                // tag数超限，需先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            if (!retryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ExampleKit.showToast(logs, context);
            }
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onCheckTagOperatorResult, sequence:" + sequence + ",check tag:" + jPushMessage.getCheckTag());
        init(context);
        // 据sequence从之前操作缓存获缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ExampleKit.showToast("获缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "tagBean:" + tagAliasBean);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            Logger.i(TAG, logs);
            ExampleKit.showToast(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            if (!retryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ExampleKit.showToast(logs, context);
            }
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);
        // 据sequence从之前操作缓存获缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ExampleKit.showToast("获缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "action - modify alias Success,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " alias success";
            Logger.i(TAG, logs);
            ExampleKit.showToast(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            if (!retryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ExampleKit.showToast(logs, context);
            }
        }
    }

    /**
     * 手机号回调
     *
     * @param context      上下文
     * @param jPushMessage JPush消息
     */
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG, "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            Logger.i(TAG, "action - set mobile number Success,sequence:" + sequence);
            setActionCache.remove(sequence);
        } else {
            String logs = "Failed to set mobile number, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            if (!retrySetMobileNumberActionIfNeeded(jPushMessage.getErrorCode(), jPushMessage.getMobileNumber())) {
                ExampleKit.showToast(logs, context);
            }
        }
    }

    public static class TagAliasBean {
        int action;
        Set<String> tags;
        String alias;
        boolean isAliasAction;

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }
}
