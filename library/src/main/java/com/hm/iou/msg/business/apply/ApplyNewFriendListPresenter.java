package com.hm.iou.msg.business.apply;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.ApplyApplyNewFriendBean;
import com.hm.iou.msg.bean.req.GetApplyNewFriendListReq;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class ApplyNewFriendListPresenter extends MvpActivityPresenter<ApplyNewFriendListContract.View> implements ApplyNewFriendListContract.Presenter {


    public ApplyNewFriendListPresenter(@NonNull Context context, @NonNull ApplyNewFriendListContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetApplyNewFriendListReq req = new GetApplyNewFriendListReq();
        MsgApi.getApplyNewFriendList(req)
                .compose(getProvider().<BaseResponse<List<ApplyApplyNewFriendBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ApplyApplyNewFriendBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ApplyApplyNewFriendBean>>(mView) {
                    @Override
                    public void handleResult(List<ApplyApplyNewFriendBean> list) {
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
        GetApplyNewFriendListReq req = new GetApplyNewFriendListReq();
        MsgApi.getApplyNewFriendList(req)
                .compose(getProvider().<BaseResponse<List<ApplyApplyNewFriendBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ApplyApplyNewFriendBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ApplyApplyNewFriendBean>>(mView) {
                    @Override
                    public void handleResult(List<ApplyApplyNewFriendBean> list) {
                        mView.hidePullDownRefresh();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
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
