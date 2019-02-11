package com.hm.iou.msg.business.message;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.mvp.MvpFragmentPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.MsgCenterDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.CacheDataUtil;
import com.hm.iou.msg.MsgCenterAppLike;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.MsgDetailBean;
import com.hm.iou.sharedata.event.CommBizEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.hm.iou.tools.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class MsgCenterPresenter extends MvpFragmentPresenter<MsgCenterContract.View> implements MsgCenterContract.Presenter {

    private List<MsgDetailBean> mMsgListData;
    private String mRedFlagCount;

    public MsgCenterPresenter(@NonNull Context context, @NonNull MsgCenterContract.View view) {
        super(context, view);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        Flowable.just(0)
                .map(new Function<Integer, List<MsgDetailBean>>() {
                    @Override
                    public List<MsgDetailBean> apply(Integer integer) throws Exception {
                        List<MsgDetailBean> listCache = CacheDataUtil.readMsgListFromCacheData();
                        return listCache;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<MsgDetailBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeWith(new CommSubscriber<List<MsgDetailBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgDetailBean> list) {
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

    private void getInitData() {
        MsgApi.getMessages()
                .compose(getProvider().<BaseResponse<List<MsgDetailBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .map(RxUtil.<List<MsgDetailBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<MsgDetailBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgDetailBean> list) {
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
    public void getMsgList() {
        MsgApi.getMessages()
                .compose(getProvider().<BaseResponse<List<MsgDetailBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .map(RxUtil.<List<MsgDetailBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<MsgDetailBean>>(mView) {
                    @Override
                    public void handleResult(List<MsgDetailBean> list) {
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

    @Override
    public void markHaveRead(int position) {
        MsgDetailBean data = mMsgListData.get(position);
        data.setRead(true);
        CacheDataUtil.updateMsgItemToCache(data);
        mView.refreshItem(position);
    }

    @Override
    public void getHeadRedFlagCount() {
        mRedFlagCount = MsgCenterAppLike.getInstance().getTopHeadRedFlagCount();
        mView.updateRedFlagCount(mRedFlagCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvenRedFlagCount(CommBizEvent commBizEvent) {
        if ("userInfo_homeLeftMenu_redFlagCount".equals(commBizEvent.key)) {
            mRedFlagCount = commBizEvent.content;
            mView.updateRedFlagCount(mRedFlagCount);
        }
    }

}
