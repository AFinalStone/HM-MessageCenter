package com.hm.iou.msg.business.similarity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.SimilarityContractMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetSimilarityContractListResBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.GetSimilarContractMessageReqBean;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.similarity.view.ISimilarityContractMsgItem;
import com.hm.iou.msg.dict.ModuleType;
import com.hm.iou.msg.im.IMHelper;
import com.hm.iou.msg.util.CacheDataUtil;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.event.IouAddEvent;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class SimilarityContractMsgPresenter extends MvpActivityPresenter<SimilarityContractMsgContract.View> implements SimilarityContractMsgContract.Presenter {

    private boolean mIsFirstPullData;

    private List<SimilarityContractMsgDbData> mTempUnreadList;

    public SimilarityContractMsgPresenter(@NonNull Context context, @NonNull SimilarityContractMsgContract.View view) {
        super(context, view);
    }

    /**
     * 从缓存中拉取数据
     */
    private void getListFromCache(final List<SimilarityContractMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<ISimilarityContractMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<ISimilarityContractMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                List<SimilarityContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(SimilarityContractMsgDbData.class);
                List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<ISimilarityContractMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<ISimilarityContractMsgItem>>() {
                    @Override
                    public void accept(List<ISimilarityContractMsgItem> resultList) throws Exception {
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
            numNoRead = unReadMsgNumBean.getButlerMessageNumber()
                    + unReadMsgNumBean.getContractNumber()
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
        GetSimilarContractMessageReqBean reqBean = new GetSimilarContractMessageReqBean();
        String pullTime = CacheDataUtil.getLastSimilarityContractListMsgPullTime(mContext);
        if (TextUtils.isEmpty(pullTime)) {
            mIsFirstPullData = true;
        } else {
            mIsFirstPullData = false;
            reqBean.setLastReqDate(pullTime);
        }
        MsgApi.getSimilarityContractList(reqBean)
                .compose(getProvider().<BaseResponse<GetSimilarityContractListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetSimilarityContractListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetSimilarityContractListResBean>(mView) {
                    @Override
                    public void handleResult(GetSimilarityContractListResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastSimilarityContractListMsgPullTime(mContext, pullTime);
                        List<SimilarityContractMsgDbData> list = resBean == null ? null : resBean.getList();
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
        GetSimilarContractMessageReqBean req = new GetSimilarContractMessageReqBean();
        req.setLastReqDate(CacheDataUtil.getLastSimilarityContractListMsgPullTime(mContext));
        MsgApi.getSimilarityContractList(req)
                .compose(getProvider().<BaseResponse<GetSimilarityContractListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetSimilarityContractListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetSimilarityContractListResBean>(mView) {
                    @Override
                    public void handleResult(GetSimilarityContractListResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastSimilarityContractListMsgPullTime(mContext, pullTime);
                        final List<SimilarityContractMsgDbData> list = resBean == null ? null : resBean.getList();
                        Flowable.create(new FlowableOnSubscribe<List<ISimilarityContractMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<ISimilarityContractMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                                List<SimilarityContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(SimilarityContractMsgDbData.class);
                                List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(listCache);
                                e.onNext(resultList);
                            }
                        }, BackpressureStrategy.ERROR)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(getProvider().<List<ISimilarityContractMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Consumer<List<ISimilarityContractMsgItem>>() {
                                    @Override
                                    public void accept(List<ISimilarityContractMsgItem> resultList) throws Exception {
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
    public void makeSingleMsgHaveRead(final ISimilarityContractMsgItem item, final int position) {
        MsgApi.makeSingleMsgHaveRead(item.getIMsgId(), item.getIMsgType())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        SimilarityContractMsgDbData dbData = MsgCenterDbHelper.getMsgByMsgId(SimilarityContractMsgDbData.class, item.getIMsgId());
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
    public void includeAllSimilarity() {
        mView.showLoadingView();
        Flowable.create(new FlowableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(FlowableEmitter<List<String>> e) throws Exception {
                List<SimilarityContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(SimilarityContractMsgDbData.class);
                List<String> listId = new ArrayList<>();
                for (int i = 0; i < listCache.size(); i++) {
                    listId.add(listCache.get(i).getJusticeId());
                }
                e.onNext(listId);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<String>, Publisher<BaseResponse<Integer>>>() {
                    @Override
                    public Publisher<BaseResponse<Integer>> apply(List<String> list) throws Exception {
                        return MsgApi.includeAllSimilarity(list);
                    }
                })
                .flatMap(new Function<BaseResponse<Integer>, Publisher<BaseResponse<Integer>>>() {
                    @Override
                    public Publisher<BaseResponse<Integer>> apply(BaseResponse<Integer> integerBaseResponse) throws Exception {
                        //读取所有的未读消息列表
                        mTempUnreadList = MsgCenterDbHelper.getUnReadMsgList(SimilarityContractMsgDbData.class);

                        MakeMsgTypeAllHaveReadReqBean reqBean = new MakeMsgTypeAllHaveReadReqBean();
                        reqBean.setLastReqDate(CacheDataUtil.getLastSimilarityContractListMsgPullTime(mContext));
                        reqBean.setType(ModuleType.SIMILARITY_CONTRACT_MSG.getTypeValue());
                        return MsgApi.makeTypeMsgHaveRead(reqBean);
                    }
                })
                .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Integer>handleResponse())
                .subscribeWith(new CommSubscriber<Integer>(mView) {
                    @Override
                    public void handleResult(Integer integer) {
                        mView.dismissLoadingView();
                        Logger.d("未读消息清除完毕");
                        if (mTempUnreadList != null && mTempUnreadList.size() > 0) {
                            for (SimilarityContractMsgDbData dbData : mTempUnreadList) {
                                dbData.setHaveRead(true);
                            }
                            MsgCenterDbHelper.saveOrUpdateMsgList(mTempUnreadList);
                        }

                        List<SimilarityContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(SimilarityContractMsgDbData.class);
                        List<ISimilarityContractMsgItem> resultList = DataChangeUtil.changeSimilarityContractMsgDbDataToISimilarityContractMsgItem(listCache);
                        mView.showMsgList(resultList);
                        mView.showLoadMoreEnd();
                        EventBus.getDefault().post(new IouAddEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.dismissLoadingView();
                    }
                });
    }

    @Override
    public void deleteMsg(final ISimilarityContractMsgItem item) {
        if (item.isHaveRead()) {
            MsgCenterDbHelper.deleteMsgByMsgId(SimilarityContractMsgDbData.class, item.getIMsgId());
            mView.removeData(item.getIMsgId());
        } else {
            mView.showLoadingView();
            MsgApi.makeSingleMsgHaveRead(item.getIMsgId(), item.getIMsgType())
                    .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                    .map(RxUtil.<Object>handleResponse())
                    .subscribeWith(new CommSubscriber<Object>(mView) {
                        @Override
                        public void handleResult(Object o) {
                            mView.dismissLoadingView();
                            MsgCenterDbHelper.deleteMsgByMsgId(SimilarityContractMsgDbData.class, item.getIMsgId());
                            mView.removeData(item.getIMsgId());

                            updateCacheRedDotCount(1);
                        }

                        @Override
                        public void handleException(Throwable throwable, String s, String s1) {
                            mView.dismissLoadingView();
                        }
                    });
        }
    }

    @Override
    public void clearAllReadData() {
        MsgCenterDbHelper.deleteAllReadMsgData(SimilarityContractMsgDbData.class);
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
            int c = unReadMsgData.getSimilarContractNumber() - d;
            unReadMsgData.setSimilarContractNumber(c < 0 ? 0 : c);
            CacheDataUtil.setNoReadMsgNum(mContext, unReadMsgData);
        }
    }

}
