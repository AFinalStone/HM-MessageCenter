package com.hm.iou.msg;

import android.content.Context;

import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.msg.event.UpdateHeadRedFlagEvent;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


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

    private Disposable mListDisposable;
    private Context mContext;
    private String mTopHeadRedFlagCount;    //导航栏上红点标记数字

    public void onCreate(Context context) {
        mContext = context;
        mApp = this;
        EventBus.getDefault().register(this);
    }

    public String getTopHeadRedFlagCount() {
        return mTopHeadRedFlagCount;
    }

    /**
     * 获取消息中心未读消息数量
     */
    public void getMsgCenterNoReadNum() {
        if (mListDisposable != null && !mListDisposable.isDisposed()) {
            mListDisposable.dispose();
        }
        mListDisposable = MsgApi.getMessages()
                .map(RxUtil.<List<MsgDetailBean>>handleResponse())
                .subscribe(new Consumer<List<MsgDetailBean>>() {
                    @Override
                    public void accept(List<MsgDetailBean> list) throws Exception {
                        CacheDataUtil.addMsgListToCache(list);
                        getMsgCenterNoReadNumFromCache();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getMsgCenterNoReadNumFromCache();
                    }
                });
    }

    /**
     * 从缓存中获取消息中心未读消息数量
     */
    public void getMsgCenterNoReadNumFromCache() {
        long numNoRead = CacheDataUtil.getNoReadMsgNum();
        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
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
                getMsgCenterNoReadNum();
            } else {
                getMsgCenterNoReadNumFromCache();
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
            mTopHeadRedFlagCount = commBizEvent.content;
        }
    }

    /**
     * 用户退出
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventLogout(LogoutEvent event) {
        CacheDataUtil.clearMsgListCache();
    }

}
