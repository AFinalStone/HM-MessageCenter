package com.hm.iou.msg.business.alipay.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.AliPayMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetAliPayListMsgResBean;
import com.hm.iou.msg.bean.req.GetAliPayMsgListReq;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.alipay.list.view.IAliPayMsgItem;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class AliPayMsgPresenter extends MvpActivityPresenter<AliPayMsgContract.View> implements AliPayMsgContract.Presenter {

    private boolean mIsFirstPullData;

    public AliPayMsgPresenter(@NonNull Context context, @NonNull AliPayMsgContract.View view) {
        super(context, view);
    }

    /**
     * 从缓存中拉取数据
     */
    private void getListFromCache(final List<AliPayMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<IAliPayMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<IAliPayMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                List<AliPayMsgDbData> listCache = MsgCenterDbHelper.getMsgList(AliPayMsgDbData.class);
                List<IAliPayMsgItem> resultList = DataChangeUtil.changeAliPayDbDataToIAliPayItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<IAliPayMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<IAliPayMsgItem>>() {
                    @Override
                    public void accept(List<IAliPayMsgItem> resultList) throws Exception {
                        //关闭动画
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (resultList == null || resultList.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(resultList);
                            mView.showLoadMoreEnd();
                            mView.scrollToBottom();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //关闭动画
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        mView.showDataEmpty();
                    }
                });
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetAliPayMsgListReq req = new GetAliPayMsgListReq();
        String pullTime = CacheDataUtil.getLastAliPayListMsgPullTime(mContext);
        if (TextUtils.isEmpty(pullTime)) {
            mIsFirstPullData = true;
        } else {
            mIsFirstPullData = false;
            req.setLastReqDate(pullTime);
        }
        MsgApi.getAliPayMsgList(req)
                .compose(getProvider().<BaseResponse<GetAliPayListMsgResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetAliPayListMsgResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetAliPayListMsgResBean>(mView) {
                    @Override
                    public void handleResult(GetAliPayListMsgResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastAliPayListMsgPullTime(mContext, pullTime);
                        List<AliPayMsgDbData> list = resBean == null ? null : resBean.getList();
                        getListFromCache(list);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        if (mIsFirstPullData) {
                            mView.showInitFailed(s1);
                        } else {
                            getListFromCache(null);
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

    @Override
    public void getMsgList() {
        GetAliPayMsgListReq req = new GetAliPayMsgListReq();
        req.setLastReqDate(CacheDataUtil.getLastAliPayListMsgPullTime(mContext));
        MsgApi.getAliPayMsgList(req)
                .compose(getProvider().<BaseResponse<GetAliPayListMsgResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetAliPayListMsgResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetAliPayListMsgResBean>(mView) {
                    @Override
                    public void handleResult(GetAliPayListMsgResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastAliPayListMsgPullTime(mContext, pullTime);
                        final List<AliPayMsgDbData> list = resBean == null ? null : resBean.getList();
                        Flowable.create(new FlowableOnSubscribe<List<IAliPayMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IAliPayMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                                List<AliPayMsgDbData> listCache = MsgCenterDbHelper.getMsgList(AliPayMsgDbData.class);
                                List<IAliPayMsgItem> resultList = DataChangeUtil.changeAliPayDbDataToIAliPayItem(listCache);
                                e.onNext(resultList);
                            }
                        }, BackpressureStrategy.ERROR)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(getProvider().<List<IAliPayMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Consumer<List<IAliPayMsgItem>>() {
                                    @Override
                                    public void accept(List<IAliPayMsgItem> resultList) throws Exception {
                                        //关闭动画
                                        mView.hidePullDownRefresh();
                                        if (resultList == null || resultList.size() == 0) {
                                            mView.showDataEmpty();
                                        } else {
                                            mView.showMsgList(resultList);
                                            mView.showLoadMoreEnd();
                                        }

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.hidePullDownRefresh();
                                        mView.showDataEmpty();
                                    }
                                });
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }
                });
    }

    @Override
    public void makeSingleMsgHaveRead(final IAliPayMsgItem item, final int position) {
        MsgApi.makeSingleMsgHaveRead(item.getIMsgId(), item.getIMsgType())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        List<AliPayMsgDbData> listCache = MsgCenterDbHelper.getMsgList(AliPayMsgDbData.class);
                        AliPayMsgDbData dbData = listCache.get(position);
                        dbData.setHaveRead(true);
                        MsgCenterDbHelper.saveOrUpdateMsg(dbData);
                        item.setHaveRead(true);
                        mView.notifyItem(item, position);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }

    @Override
    public void makeTypeMsgHaveRead() {
        //TODO
        MakeMsgTypeAllHaveReadReqBean reqBean = new MakeMsgTypeAllHaveReadReqBean();
        reqBean.setLastReqDate(CacheDataUtil.getLastAliPayListMsgPullTime(mContext));

        MsgApi.makeTypeMsgHaveRead(reqBean)
                .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Integer>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        List<AliPayMsgDbData> listCache = MsgCenterDbHelper.getMsgList(AliPayMsgDbData.class);
                        if (listCache != null && listCache.size() > 0) {
                            for (AliPayMsgDbData dbData : listCache) {
                                dbData.setHaveRead(true);
                            }
                        }
                        MsgCenterDbHelper.saveOrUpdateMsgList(listCache);
                        List<IAliPayMsgItem> resultList = DataChangeUtil.changeAliPayDbDataToIAliPayItem(listCache);
                        mView.showMsgList(resultList);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }
}
