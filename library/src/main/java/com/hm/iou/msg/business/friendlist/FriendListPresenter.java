package com.hm.iou.msg.business.friendlist;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FriendBean;
import com.hm.iou.msg.bean.req.GetFriendListReq;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class FriendListPresenter extends MvpActivityPresenter<FriendListContract.View> implements FriendListContract.Presenter {


    public FriendListPresenter(@NonNull Context context, @NonNull FriendListContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetFriendListReq req = new GetFriendListReq();
        MsgApi.getFriendList(req)
                .compose(getProvider().<BaseResponse<List<FriendBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<FriendBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<FriendBean>>(mView) {
                    @Override
                    public void handleResult(List<FriendBean> list) {
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
        GetFriendListReq req = new GetFriendListReq();
        MsgApi.getFriendList(req)
                .compose(getProvider().<BaseResponse<List<FriendBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<FriendBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<FriendBean>>(mView) {
                    @Override
                    public void handleResult(List<FriendBean> list) {
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
