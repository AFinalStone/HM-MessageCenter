package com.hm.iou.msg;

import com.hm.iou.sharedata.event.CommBizEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author syl
 * @time 2018/5/19 下午2:59
 */

public class EventBusHelper {

    /**
     * 成功获取了消息中心的未读消息列表
     *
     * @param noReadNum 未读消息数量
     */
    public static void postEventBusGetMsgNoReadNumSuccess(String noReadNum) {
        EventBus.getDefault().post(new CommBizEvent(MsgCenterAppLike.EXTRA_KEY_GET_MSG_LIST_SUCCESS, noReadNum));
    }

}
