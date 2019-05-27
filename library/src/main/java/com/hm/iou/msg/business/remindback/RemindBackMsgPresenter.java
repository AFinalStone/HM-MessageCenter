package com.hm.iou.msg.business.remindback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.RemindBackMsgDbData;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetRemindBackListMsgResBean;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.remindback.view.IRemindBackMsgItem;
import com.hm.iou.msg.dict.ModuleType;
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
 * 待还提醒
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class RemindBackMsgPresenter extends MvpActivityPresenter<RemindBackMsgContract.View> implements RemindBackMsgContract.Presenter {

    private boolean mIsFirstPullData;

    public RemindBackMsgPresenter(@NonNull Context context, @NonNull RemindBackMsgContract.View view) {
        super(context, view);
    }

    /**
     * 从缓存中拉取数据
     */
    private void getListFromCache(final List<RemindBackMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<IRemindBackMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<IRemindBackMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                List<RemindBackMsgDbData> listCache = MsgCenterDbHelper.getMsgList(RemindBackMsgDbData.class);
                List<IRemindBackMsgItem> resultList = DataChangeUtil.changeRemindBackMsgDbDataToIRemindBackMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<IRemindBackMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<IRemindBackMsgItem>>() {
                    @Override
                    public void accept(List<IRemindBackMsgItem> resultList) throws Exception {
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
                        getUnReadMsgNum();

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

    /**
     * 获取未读消息数量
     */
    private void getUnReadMsgNum() {
        long unReadMsg = MsgCenterDbHelper.getMsgUnReadNum(RemindBackMsgDbData.class);
        mView.setBottomClearIconVisible(unReadMsg > 0);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetRemindBackListReq reqBean = new GetRemindBackListReq();
        String pullTime = CacheDataUtil.getLasRemindBackPullTime(mContext);
        if (TextUtils.isEmpty(pullTime)) {
            mIsFirstPullData = true;
        } else {
            mIsFirstPullData = false;
            reqBean.setLastReqDate(pullTime);
        }
        MsgApi.getRemindBackList(reqBean)
                .compose(getProvider().<BaseResponse<GetRemindBackListMsgResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetRemindBackListMsgResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetRemindBackListMsgResBean>(mView) {
                    @Override
                    public void handleResult(GetRemindBackListMsgResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastRemindBackPullTime(mContext, pullTime);
                        List<RemindBackMsgDbData> list = resBean == null ? null : resBean.getList();
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

                });
    }

    @Override
    public void getMsgList() {
        GetRemindBackListReq req = new GetRemindBackListReq();
        req.setLastReqDate(CacheDataUtil.getLasRemindBackPullTime(mContext));
        MsgApi.getRemindBackList(req)
                .compose(getProvider().<BaseResponse<GetRemindBackListMsgResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetRemindBackListMsgResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetRemindBackListMsgResBean>(mView) {
                    @Override
                    public void handleResult(GetRemindBackListMsgResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastRemindBackPullTime(mContext, pullTime);
                        final List<RemindBackMsgDbData> list = resBean == null ? null : resBean.getList();
                        Flowable.create(new FlowableOnSubscribe<List<IRemindBackMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IRemindBackMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                                List<RemindBackMsgDbData> listCache = MsgCenterDbHelper.getMsgList(RemindBackMsgDbData.class);
                                List<IRemindBackMsgItem> resultList = DataChangeUtil.changeRemindBackMsgDbDataToIRemindBackMsgItem(listCache);
                                e.onNext(resultList);
                            }
                        }, BackpressureStrategy.ERROR)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(getProvider().<List<IRemindBackMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Consumer<List<IRemindBackMsgItem>>() {
                                    @Override
                                    public void accept(List<IRemindBackMsgItem> resultList) throws Exception {
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
    public void makeSingleMsgHaveRead(final IRemindBackMsgItem item, final int position) {
        MsgApi.makeSingleMsgHaveRead(item.getIMsgId(), item.getIMsgType())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        RemindBackMsgDbData dbData = MsgCenterDbHelper.getMsgByMsgId(RemindBackMsgDbData.class, item.getIMsgId());
                        dbData.setHaveRead(true);
                        MsgCenterDbHelper.saveOrUpdateMsg(dbData);
                        item.setHaveRead(true);
                        mView.notifyItem(item, position);
                        getUnReadMsgNum();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }

    @Override
    public void makeTypeMsgHaveRead() {
        MakeMsgTypeAllHaveReadReqBean reqBean = new MakeMsgTypeAllHaveReadReqBean();
        reqBean.setLastReqDate(CacheDataUtil.getLasRemindBackPullTime(mContext));
        reqBean.setType(ModuleType.REMIND_BACK_MSG.getTypeValue());
        MsgApi.makeTypeMsgHaveRead(reqBean)
                .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Integer>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        List<RemindBackMsgDbData> listCache = MsgCenterDbHelper.getMsgList(RemindBackMsgDbData.class);
                        if (listCache != null && listCache.size() > 0) {
                            for (RemindBackMsgDbData dbData : listCache) {
                                dbData.setHaveRead(true);
                            }
                        }
                        MsgCenterDbHelper.saveOrUpdateMsgList(listCache);
                        List<IRemindBackMsgItem> resultList = DataChangeUtil.changeRemindBackMsgDbDataToIRemindBackMsgItem(listCache);
                        mView.showMsgList(resultList);
                        mView.showLoadMoreEnd();
                        mView.setBottomClearIconVisible(false);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }
}
