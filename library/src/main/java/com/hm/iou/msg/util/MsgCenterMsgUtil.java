package com.hm.iou.msg.util;

import android.content.Context;

import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.EventBusHelper;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.UnReadMsgNumBean;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by syl on 2019/4/16.
 */

public class MsgCenterMsgUtil {

    private static Disposable mListDisposable;
    private static String mTopHeadRedFlagCount;    //导航栏上红点标记数字

    public static void setTopHeadRedFlagCount(String topHeadRedFlagCount) {
        MsgCenterMsgUtil.mTopHeadRedFlagCount = topHeadRedFlagCount;
    }

    public static String getTopHeadRedFlagCount() {
        return mTopHeadRedFlagCount;
    }

    /**
     * 从服务端获取消息中心未读消息数量
     */
    public static void getMsgCenterNoReadNumFromServer(final Context context) {
        if (mListDisposable != null && !mListDisposable.isDisposed()) {
            mListDisposable.dispose();
        }
        mListDisposable = MsgApi.getUnReadMsgNum()
                .map(RxUtil.<UnReadMsgNumBean>handleResponse())
                .subscribe(new Consumer<UnReadMsgNumBean>() {
                    @Override
                    public void accept(UnReadMsgNumBean unReadMsgNumBean) throws Exception {
                        int numNoRead = unReadMsgNumBean.getButlerMessageNumber()
                                + unReadMsgNumBean.getContractNumber()
                                + unReadMsgNumBean.getSimilarContractNumber()
                                + unReadMsgNumBean.getWaitRepayNumber();
                        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
                        CacheDataUtil.setNoReadMsgNum(context, unReadMsgNumBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getMsgCenterNoReadNumFromCache(context);
                    }
                });
    }

    /**
     * 从缓存中获取消息中心未读消息数量
     */
    public static UnReadMsgNumBean getMsgCenterNoReadNumFromCache(Context context) {
        UnReadMsgNumBean unReadMsgNumBean = CacheDataUtil.getNoReadMsgNum(context);
        int numNoRead = 0;
        if (unReadMsgNumBean != null) {
            numNoRead = unReadMsgNumBean.getButlerMessageNumber()
                    + unReadMsgNumBean.getContractNumber()
                    + unReadMsgNumBean.getSimilarContractNumber()
                    + unReadMsgNumBean.getWaitRepayNumber();
        }
        EventBusHelper.postEventBusGetMsgNoReadNumSuccess(String.valueOf(numNoRead));
        return unReadMsgNumBean;
    }

}
