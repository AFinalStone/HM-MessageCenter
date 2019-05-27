package com.hm.iou.msg.business.hmmsg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.HmMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetHMMsgListResBean;
import com.hm.iou.msg.bean.req.GetHMMsgListReq;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.hmmsg.view.IHmMsgItem;
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
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class HmMsgListPresenter extends MvpActivityPresenter<HmMsgListContract.View> implements HmMsgListContract.Presenter {

    private boolean mIsFirstPullData;


    public HmMsgListPresenter(@NonNull Context context, @NonNull HmMsgListContract.View view) {
        super(context, view);
    }

    /**
     * 从缓存中拉取数据
     */
    private void getListFromCache(final List<HmMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<IHmMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<IHmMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                List<HmMsgDbData> listCache = MsgCenterDbHelper.getMsgList(HmMsgDbData.class);
                List<IHmMsgItem> resultList = DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<IHmMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<IHmMsgItem>>() {
                    @Override
                    public void accept(List<IHmMsgItem> resultList) throws Exception {
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
        long unReadMsg = MsgCenterDbHelper.getMsgUnReadNum(HmMsgDbData.class);
        mView.setBottomClearIconVisible(unReadMsg > 0);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetHMMsgListReq reqBean = new GetHMMsgListReq();
        String pullTime = CacheDataUtil.getLastHMListMsgPullTime(mContext);
        if (TextUtils.isEmpty(pullTime)) {
            mIsFirstPullData = true;
        } else {
            mIsFirstPullData = false;
            reqBean.setLastReqDate(pullTime);
        }
        MsgApi.getHmMsgList(reqBean)
                .compose(getProvider().<BaseResponse<GetHMMsgListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetHMMsgListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetHMMsgListResBean>(mView) {
                    @Override
                    public void handleResult(GetHMMsgListResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastHMListMsgPullTime(mContext, pullTime);
                        List<HmMsgDbData> list = resBean == null ? null : resBean.getList();
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
        GetHMMsgListReq req = new GetHMMsgListReq();
        req.setLastReqDate(CacheDataUtil.getLastHMListMsgPullTime(mContext));
        MsgApi.getHmMsgList(req)
                .compose(getProvider().<BaseResponse<GetHMMsgListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetHMMsgListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetHMMsgListResBean>(mView) {
                    @Override
                    public void handleResult(GetHMMsgListResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastHMListMsgPullTime(mContext, pullTime);
                        final List<HmMsgDbData> list = resBean == null ? null : resBean.getList();
                        Flowable.create(new FlowableOnSubscribe<List<IHmMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IHmMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                                List<HmMsgDbData> listCache = MsgCenterDbHelper.getMsgList(HmMsgDbData.class);
                                List<IHmMsgItem> resultList = DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(listCache);
                                e.onNext(resultList);
                            }
                        }, BackpressureStrategy.ERROR)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(getProvider().<List<IHmMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Consumer<List<IHmMsgItem>>() {
                                    @Override
                                    public void accept(List<IHmMsgItem> resultList) throws Exception {
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
    public void makeSingleMsgHaveRead(final IHmMsgItem item, final int position) {
        MsgApi.makeSingleMsgHaveRead(item.getIMsgId(), item.getIMsgType())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        HmMsgDbData dbData = MsgCenterDbHelper.getMsgByMsgId(HmMsgDbData.class, item.getIMsgId());
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
        reqBean.setLastReqDate(CacheDataUtil.getLastAliPayListMsgPullTime(mContext));
        reqBean.setType(ModuleType.HM_MSG.getTypeValue());
        MsgApi.makeTypeMsgHaveRead(reqBean)
                .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Integer>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        List<HmMsgDbData> listCache = MsgCenterDbHelper.getMsgList(HmMsgDbData.class);
                        if (listCache != null && listCache.size() > 0) {
                            for (HmMsgDbData dbData : listCache) {
                                dbData.setHaveRead(true);
                            }
                        }
                        MsgCenterDbHelper.saveOrUpdateMsgList(listCache);
                        List<IHmMsgItem> resultList = DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(listCache);
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
