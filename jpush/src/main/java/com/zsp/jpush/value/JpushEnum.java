package com.zsp.jpush.value;

/**
 * Created on 2020/12/22
 *
 * @author zsp
 * @desc 极光推送枚举
 */
public enum JpushEnum {
    /**
     * ACTION_REGISTRATION_ID
     */
    ACTION_REGISTRATION_ID(100),
    /**
     * ACTION_MESSAGE_RECEIVED
     */
    ACTION_MESSAGE_RECEIVED(101),
    /**
     * ACTION_NOTIFICATION_RECEIVED
     */
    ACTION_NOTIFICATION_RECEIVED(102),
    /**
     * ACTION_NOTIFICATION_OPENED
     */
    ACTION_NOTIFICATION_OPENED(103),
    /**
     * ACTION_RICHPUSH_CALLBACK
     */
    ACTION_RICHPUSH_CALLBACK(104),
    /**
     * ACTION_CONNECTION_CHANGE
     */
    ACTION_CONNECTION_CHANGE(105);
    /**
     * 码
     */
    private final int code;

    /**
     * constructor
     *
     * @param code 码
     */
    JpushEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
