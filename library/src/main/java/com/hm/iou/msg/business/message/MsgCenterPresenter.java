package com.hm.iou.msg.business.message;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.msg.business.message.view.IMsgItem;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class MsgCenterPresenter extends MvpActivityPresenter<MsgCenterContract.View> implements MsgCenterContract.Presenter {

    public MsgCenterPresenter(@NonNull Context context, @NonNull MsgCenterContract.View view) {
        super(context, view);
    }


    private List<MsgDetailBean> clearTimeOutData(List<MsgDetailBean> list) {

        for (MsgDetailBean bean : list) {

        }
        return list;
    }

    @Override
    public void getMsgList() {
        MsgApi.getMessages()
                .compose(getProvider().<BaseResponse<List<MsgDetailBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<MsgDetailBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<MsgDetailBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgDetailBean> list) {
                        List<MsgDetailBean> listCache = DataUtil.readMsgListFromCache(mContext);
                        listCache.addAll(list);
                        DataUtil.cacheMsgList(mContext, listCache);
                        mView.showMsgList((ArrayList) listCache);
                        mView.hidePullDownRefresh();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }
                });
    }
}
