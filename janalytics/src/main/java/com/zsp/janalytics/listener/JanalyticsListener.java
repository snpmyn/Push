package com.zsp.janalytics.listener;

import com.zsp.janalytics.value.JanalyticsEnum;

/**
 * Created on 2019/9/6.
 *
 * @author 郑少鹏
 * @desc 极光统计监听
 */
public interface JanalyticsListener {
    /**
     * 回调
     *
     * @param janalyticsEnum 极光统计枚举
     * @param code           码
     * @param msg            消息
     */
    void callback(JanalyticsEnum janalyticsEnum, int code, String msg);
}
