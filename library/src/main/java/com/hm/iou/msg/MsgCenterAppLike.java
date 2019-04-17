package com.hm.iou.msg;

import android.content.Context;

import com.hm.iou.msg.im.IMInitHelper;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.MsgCenterMsgUtil;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.event.LoginSuccEvent;
import com.hm.iou.sharedata.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author syl
 * @time 2018/6/1 下午5:48
 */
public class MsgCenterAppLike {

    public static final String EXTRA_KEY_GET_NO_READ_NUM = "MsgCenter_getNoReadNum";
    public static final String EXTRA_KEY_GET_NO_READ_NUM_SUCCESS = "MsgCenter_getNoReadNumSuccess";

    private static MsgCenterAppLike mApp;

    public static MsgCenterAppLike getInstance() {
        if (mApp == null) {
            throw new RuntimeException("MsgCenterAppLike should init first.");
        }
        return mApp;
    }

    private Context mContext;

    public void onCreate(Context context) {
        mContext = context;
        mApp = this;
        EventBus.getDefault().register(this);
        //初始化IM
        IMInitHelper.getInstance(mContext).initIM();
    }

    /**
     * 获取消息中心未读消息数量
     *
     * @param -commBizEvent.key    == getMsgCenterNoReadNum
     * @param -commBizEvent.cotent ture的时候从服务端获取最新的消息，否则直接从缓存中获取
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusgetMsgCenterNoReadNum(CommBizEvent commBizEvent) {
        if (EXTRA_KEY_GET_NO_READ_NUM.equals(commBizEvent.key)) {
            if ("true".equals(commBizEvent.content)) {
                MsgCenterMsgUtil.getMsgCenterNoReadNumFromServer(mContext);
            } else {
                MsgCenterMsgUtil.getMsgCenterNoReadNumFromCache(mContext);
            }
        }
    }

    /**
     * 成功获取个人中心红色标记数量
     *
     * @param commBizEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusUserInfoHomeLeftMenuRedFlagCount(CommBizEvent commBizEvent) {
        if ("userInfo_homeLeftMenu_redFlagCount".equals(commBizEvent.key)) {
            String topHeadRedFlagCount = commBizEvent.content;
            MsgCenterMsgUtil.setTopHeadRedFlagCount(topHeadRedFlagCount);
        }
    }

    /**
     * 用户登陆成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogin(LoginSuccEvent event) {
        //初始化IM
        IMInitHelper.getInstance(mContext).initIM();
    }

    /**
     * 用户退出
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventLogout(LogoutEvent event) {
        CacheDataUtil.clearAllCache(mContext);
    }


}
