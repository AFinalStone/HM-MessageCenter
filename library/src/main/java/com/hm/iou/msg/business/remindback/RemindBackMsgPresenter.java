package com.hm.iou.msg.business.remindback;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.RemindBackMsgBean;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 待还提醒
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class RemindBackMsgPresenter extends MvpActivityPresenter<RemindBackMsgContract.View> implements RemindBackMsgContract.Presenter {


    public RemindBackMsgPresenter(@NonNull Context context, @NonNull RemindBackMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        MsgApi.getRemindBackList()
                .compose(getProvider().<BaseResponse<List<RemindBackMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<RemindBackMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<RemindBackMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<RemindBackMsgBean> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) list);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.showInitFailed();
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }
                });
    }

    @Override
    public void getMsgList() {
        MsgApi.getRemindBackList()
                .compose(getProvider().<BaseResponse<List<RemindBackMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<RemindBackMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<RemindBackMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<RemindBackMsgBean> list) {
                        mView.hidePullDownRefresh();
                        if (list == null && list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) list);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }
                });
    }


}
