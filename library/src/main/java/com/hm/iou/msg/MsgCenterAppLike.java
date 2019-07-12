package com.hm.iou.msg;

import android.content.Context;

import com.hm.iou.database.IouDbHelper;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.IouData;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.MsgCenterMsgUtil;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.event.IouDeleteEvent;
import com.hm.iou.sharedata.event.LoginSuccEvent;
import com.hm.iou.sharedata.event.LogoutEvent;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * @author syl
 * @time 2018/6/1 下午5:48
 */
public class MsgCenterAppLike {

    public static final String EXTRA_KEY_GET_NO_READ_NUM = "MsgCenter_getNoReadNum";
    public static final String EXTRA_KEY_GET_NO_READ_NUM_SUCCESS = "MsgCenter_getNoReadNumSuccess";
    public static final String EXTRA_KEY_REFRESH_IM_TOKEN_AND = "MsgCenter_refresh_im_token_and_login";

    private static MsgCenterAppLike mApp;

    public static MsgCenterAppLike getInstance() {
        if (mApp == null) {
            throw new RuntimeException("MsgCenterAppLike should init first.");
        }
        return mApp;
    }

    private Context mContext;
    private MixPushConfig mPushConfig;

    public void onCreate(Context context) {
        mContext = context;
        mApp = this;
        EventBus.getDefault().register(this);
        //初始化IM
        IMHelper.getInstance(mContext).initIM();
    }

    public void setPushConfig(MixPushConfig config) {
        mPushConfig = config;
    }

    public MixPushConfig getPushConfig() {
        return mPushConfig;
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
        IMHelper.getInstance(mContext).login();
    }

    /**
     * 刷新用户IM token并登陆
     *
     * @param -commBizEvent.key == MsgCenter_refresh_im_token
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRefreshTokenAndLogin(CommBizEvent commBizEvent) {
        if (EXTRA_KEY_REFRESH_IM_TOKEN_AND.equals(commBizEvent.key)) {
            IMHelper.getInstance(mContext).refreshTokenAndLogin();
        }
    }

    /**
     * 用户退出
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventLogout(LogoutEvent event) {
        CacheDataUtil.clearAllCache(mContext);
        IMHelper.getInstance(mContext).logout();
    }

    /**
     * 用户隐藏了借条，这个时候把疑似合同中已经读取的消息，且被用户删除了的疑似合同消息删除掉
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventDelete(IouDeleteEvent event) {
        List<SimilarityContractMsgDbData> result = MsgCenterDbHelper.getMsgList(SimilarityContractMsgDbData.class, "is_have_read = ?", "1");
        if (result != null && !result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                SimilarityContractMsgDbData item = result.get(i);
                if (item != null) {
                    String justiceId = item.getJusticeId();
                    IouData iouData = IouDbHelper.queryIOUByJusticeId(justiceId);
                    if (iouData == null) {
                        MsgCenterDbHelper.deleteSimilarityContractByJustId(justiceId);
                    }
                }
            }
        }

    }


}
