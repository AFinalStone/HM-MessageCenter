package com.hm.iou.msg;

import android.content.Context;

import com.google.gson.Gson;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.CommuniqueMsgBean;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.sharedata.event.CommBizEvent;

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

    public static final String EXTRA_KEY_GET_MSG_NO_READ_NUM = "getMsgCenterNoReadNum";
    public static final String EXTRA_KEY_GET_MSG_NO_READ_NUM_SUCCESS = "getMsgCenterNoReadNumSuccess";
    public static final String EXTRA_KEY_INSTER_COMMUNIQUE_MSG = "InsertCommuniqueMsg";

    private Disposable mListDisposable;

    public static MsgCenterAppLike getInstance() {
        if (mApp == null) {
            throw new RuntimeException("MsgCenterAppLike should init first.");
        }
        return mApp;
    }

    private static MsgCenterAppLike mApp;

    private Context mContext;

    public void onCreate(Context context) {
        mContext = context;
        mApp = this;
        EventBus.getDefault().register(this);
    }

    public void onDestroy() {
        DataUtil.clearMsgListCache(mContext);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取消息中心未读消息数量
     */
    private void getMsgCenterNoReadNum() {
        if (mListDisposable != null && !mListDisposable.isDisposed()) {
            mListDisposable.dispose();
        }
        mListDisposable = MsgApi.getMessages()
                .map(RxUtil.<List<MsgDetailBean>>handleResponse())
                .subscribe(new Consumer<List<MsgDetailBean>>() {
                    @Override
                    public void accept(List<MsgDetailBean> list) throws Exception {
                        DataUtil.addMsgListToCache(mContext, list);
                        int numNoRead = DataUtil.getNoReadMsgNum(mContext);
                        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int numNoRead = DataUtil.getNoReadMsgNum(mContext);
                        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
                    }
                });
    }

    /**
     * 获取消息中心未读消息数量
     *
     * @param -commBizEvent.key    == getMsgCenterNoReadNum
     * @param -commBizEvent.cotent ture的时候从服务端获取最新的消息，否则直接从缓存中获取
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusgetMsgCenterNoReadNum(CommBizEvent commBizEvent) {
        if (EXTRA_KEY_GET_MSG_NO_READ_NUM.equals(commBizEvent.key)) {
            if ("true".equals(commBizEvent.content)) {
                getMsgCenterNoReadNum();
            } else {
                int numNoRead = DataUtil.getNoReadMsgNum(mContext);
                EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
            }
        }
    }

    /**
     * 把官方公告插入到缓存中
     *
     * @param -commBizEvent.key    == InsertCommuniqueMsg
     * @param -commBizEvent.cotent CommuniqueMsgBean对象的json字符串
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusInsertCommuniqueMsg(CommBizEvent commBizEvent) {
        if (EXTRA_KEY_INSTER_COMMUNIQUE_MSG.equals(commBizEvent.key)) {
            try {
                CommuniqueMsgBean communiqueMsgBean = new Gson().fromJson(commBizEvent.content, CommuniqueMsgBean.class);
                DataUtil.addCommuniqueMsgToCache(mContext, communiqueMsgBean);
            } catch (Exception e) {
            }
        }
    }


}
