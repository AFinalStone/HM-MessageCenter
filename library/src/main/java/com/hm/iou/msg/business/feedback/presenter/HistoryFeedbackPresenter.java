package com.hm.iou.msg.business.feedback.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.FeedbackListItemBean;
import com.hm.iou.msg.business.feedback.HistoryFeedbackContract;
import com.hm.iou.msg.business.feedback.view.IFeedbackListItem;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by hjy on 18/4/28.<br>
 */

public class HistoryFeedbackPresenter extends MvpActivityPresenter<HistoryFeedbackContract.View> implements HistoryFeedbackContract.Presenter {

    private Disposable mListDisposable;
    private List<FeedbackListItemBean> mDataList;

    public HistoryFeedbackPresenter(@NonNull Context context, @NonNull HistoryFeedbackContract.View view) {
        super(context, view);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void refresh() {
        if (mListDisposable != null && !mListDisposable.isDisposed()) {
            mListDisposable.dispose();
        }
        mListDisposable = MsgApi.getFeedbackList()
                .compose(getProvider().<BaseResponse<List<FeedbackListItemBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<FeedbackListItemBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<FeedbackListItemBean>>(mView) {
                    @Override
                    public void handleResult(List<FeedbackListItemBean> list) {
                        mView.hidePullDownRefresh();
                        mDataList = list;

                        if (mDataList == null || mDataList.isEmpty()) {
                            mView.showDataEmpty();
                        } else {
                            mView.showFeedbackList((ArrayList)list);
                            mView.showLoading(false);
                            mView.showLoadMoreEnd();
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                        if (mDataList == null || mDataList.isEmpty()) {
                            mView.showDataLoadFail();
                        } else {
                            ToastUtil.showMessage(mContext, "网络不给力");
                        }
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }
                });
    }
}
