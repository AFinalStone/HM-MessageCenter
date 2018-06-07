package com.hm.iou.msg.demo;

import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.sharedata.event.CommBizEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author syl
 * @time 2018/5/19 下午2:59
 */

public class EventBusHelper {

    /**
     * 获取了消息中心的未读消息列表
     *
     * @param ifRefresh 是否从服务端刷新数据
     */
    public static void postEventBusGetMsgNoReadNum(boolean ifRefresh) {
        if (ifRefresh) {
            EventBus.getDefault().post(new CommBizEvent(MsgCenterAppLike.EXTRA_KEY_GET_NO_READ_NUM, "true"));
        } else {
            EventBus.getDefault().post(new CommBizEvent(MsgCenterAppLike.EXTRA_KEY_GET_NO_READ_NUM, "false"));
        }
    }

}
