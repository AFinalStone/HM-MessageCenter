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
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.GetHMMsgListReq;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.hmmsg.view.IHmMsgItem;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.msg.im.IMHelper;
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
                List<HmMsgDbData> listCache = MsgCenterDbHelper.getHmMsgList();
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
                            mView.setBottomMoreIconVisible(false);
                        } else {
                            mView.showMsgList(resultList);
                            mView.setBottomMoreIconVisible(true);
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
                        mView.setBottomMoreIconVisible(false);
                    }
                });
    }

    /**
     * 获取未读消息数量
     */
    private void getUnReadMsgNum() {
        UnReadMsgNumBean unReadMsgNumBean = CacheDataUtil.getNoReadMsgNum(mContext);
        int numNoRead = 0;
        if (unReadMsgNumBean != null) {
            numNoRead = unReadMsgNumBean.getContractNumber()
                    + unReadMsgNumBean.getSimilarContractNumber()
                    + unReadMsgNumBean.getFriendMessageNumber()
                    + unReadMsgNumBean.getWaitRepayNumber()
                    + unReadMsgNumBean.getAlipayReceiptNumber()
                    + IMHelper.getInstance(mContext).getTotalUnReadMsgCount();
        }
        mView.showRedDot(numNoRead);
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
                        if (list != null) {
                            for (HmMsgDbData data : list) {
                                data.convertImgListToString();
                            }
                        }
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

        getUnReadMsgNum();
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
                                if (list != null) {
                                    for (HmMsgDbData data : list) {
                                        data.convertImgListToString();
                                    }
                                }
                                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                                List<HmMsgDbData> listCache = MsgCenterDbHelper.getHmMsgList();
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
                                            mView.setBottomMoreIconVisible(false);
                                        } else {
                                            mView.showMsgList(resultList);
                                            mView.setBottomMoreIconVisible(true);
                                            mView.showLoadMoreEnd();
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.hidePullDownRefresh();
                                        mView.showDataEmpty();
                                        mView.setBottomMoreIconVisible(false);
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
                        mView.updateData(item);

                        updateCacheRedDotCount(1);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }
                });
    }

    @Override
    public void makeTypeMsgHaveRead() {
        MakeMsgTypeAllHaveReadReqBean reqBean = new MakeMsgTypeAllHaveReadReqBean();
        reqBean.setLastReqDate(CacheDataUtil.getLastHMListMsgPullTime(mContext));
        reqBean.setType(ModuleType.HM_MSG.getTypeValue());

        final List<HmMsgDbData> unReadList = MsgCenterDbHelper.getUnReadMsgList(HmMsgDbData.class);
        Logger.d("未读消息数 = " + (unReadList != null ? unReadList.size() : 0));

        mView.showLoadingView();
        MsgApi.makeTypeMsgHaveRead(reqBean)
                .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Integer>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        mView.dismissLoadingView();
                        if (unReadList != null && !unReadList.isEmpty()) {
                            for (HmMsgDbData data : unReadList) {
                                data.setHaveRead(true);
                            }
                            MsgCenterDbHelper.saveOrUpdateMsgList(unReadList);
                            updateCacheRedDotCount(unReadList.size());
                        }

                        List<HmMsgDbData> listCache = MsgCenterDbHelper.getHmMsgList();
                        List<IHmMsgItem> resultList = DataChangeUtil.changeHmMsgDbDataToIHmMsgItem(listCache);
                        mView.showMsgList(resultList);
                        mView.showLoadMoreEnd();
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void deleteMsg(IHmMsgItem item) {

    }

    @Override
    public void clearAllReadData() {
        MsgCenterDbHelper.deleteAllReadMsgData(HmMsgDbData.class);
        getListFromCache(null);
    }

    /**
     * 减少缓存里的红点数
     *
     * @param d
     */
    private void updateCacheRedDotCount(int d) {
        //更新缓存红点数
        UnReadMsgNumBean unReadMsgData = CacheDataUtil.getNoReadMsgNum(mContext);
        if (unReadMsgData != null) {
            int c = unReadMsgData.getButlerMessageNumber() - d;
            unReadMsgData.setButlerMessageNumber(c < 0 ? 0 : c);
            CacheDataUtil.setNoReadMsgNum(mContext, unReadMsgData);
        }
    }

}
