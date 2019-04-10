package com.hm.iou.msg.business.hmmsg;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.CacheDataUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.HmMsgBean;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class HmMsgListPresenter extends MvpActivityPresenter<HmMsgListContract.View> implements HmMsgListContract.Presenter {

    private List<HmMsgBean> mMsgListData;

    public HmMsgListPresenter(@NonNull Context context, @NonNull HmMsgListContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        Flowable.just(0)
                .map(new Function<Integer, List<HmMsgBean>>() {
                    @Override
                    public List<HmMsgBean> apply(Integer integer) throws Exception {
                        List<HmMsgBean> listCache = CacheDataUtil.readMsgListFromCacheData();
                        return listCache;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<HmMsgBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeWith(new CommSubscriber<List<HmMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgBean> list) {
                        mMsgListData = list;
                        getInitData();
                    }

                    @Override
                    public void handleException(Throwable throwable, String code, String msg) {
                        getInitData();
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

    @Override
    public void getMsgListFromServer() {
        //重新获取未读消息数量
        MsgApi.getMessages()
                .compose(getProvider().<BaseResponse<List<HmMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<HmMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<HmMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgBean> list) {
                        if (mMsgListData == null) {
                            mMsgListData = new ArrayList<>();
                        }
                        if (list != null) {
                            CacheDataUtil.addMsgListToCache(list);
                            mMsgListData.addAll(list);
                        }
                        if (mMsgListData.isEmpty()) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) mMsgListData);
                        }
                        mView.hidePullDownRefresh();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                        ToastUtil.showMessage(mContext, "网络不给力");
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

    private void getInitData() {
        MsgApi.getMessages()
                .compose(getProvider().<BaseResponse<List<HmMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<HmMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<HmMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<HmMsgBean> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (mMsgListData == null) {
                            mMsgListData = new ArrayList<>();
                        }
                        if (list != null) {
                            CacheDataUtil.addMsgListToCache(list);
                            mMsgListData.addAll(list);
                        }
                        if (mMsgListData.isEmpty()) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) mMsgListData);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
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
    public void markHaveRead(int position) {
        HmMsgBean data = mMsgListData.get(position);
        data.setRead(true);
        CacheDataUtil.updateMsgItemToCache(data);
        mView.refreshItem(position);
    }

}
