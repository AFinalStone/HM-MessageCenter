package com.hm.iou.msg;

import android.content.Context;

import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
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

    public static final String EXTRA_KEY_GET_MSG_LIST_SUCCESS = "getMsgCenterNoReadNumSuccess";
    public static final String EXTRA_KEY_GET_MSG_NO_READ_NUM = "getMsgCenterNoReadNum";

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenBusGetMsgCenterNoRead(CommBizEvent commBizEvent) {
        if (EXTRA_KEY_GET_MSG_NO_READ_NUM.equals(commBizEvent.key)) {
            getMsgCenterNoReadNum();
        }
    }


}
