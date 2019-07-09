package com.hm.iou.msg.business.contractmsg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetContractMsgListResBean;
import com.hm.iou.msg.bean.UnReadMsgNumBean;
import com.hm.iou.msg.bean.req.GetContractMsgListReq;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;
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
public class ContractMsgPresenter extends MvpActivityPresenter<ContractMsgContract.View> implements ContractMsgContract.Presenter {

    private boolean mIsFirstPullData;


    public ContractMsgPresenter(@NonNull Context context, @NonNull ContractMsgContract.View view) {
        super(context, view);
    }

    /**
     * 从缓存中拉取数据
     */
    private void getListFromCache(final List<ContractMsgDbData> list) {
        Flowable.create(new FlowableOnSubscribe<List<IContractMsgItem>>() {
            @Override
            public void subscribe(FlowableEmitter<List<IContractMsgItem>> e) throws Exception {
                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                List<ContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(ContractMsgDbData.class);
                List<IContractMsgItem> resultList = DataChangeUtil.changeContractMsgDbDataToIContractMsgItem(listCache);
                e.onNext(resultList);
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getProvider().<List<IContractMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<IContractMsgItem>>() {
                    @Override
                    public void accept(List<IContractMsgItem> resultList) throws Exception {
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
        GetContractMsgListReq reqBean = new GetContractMsgListReq();
        String pullTime = CacheDataUtil.getLastContractMsgPullTime(mContext);
        if (TextUtils.isEmpty(pullTime)) {
            mIsFirstPullData = true;
        } else {
            mIsFirstPullData = false;
            reqBean.setLastReqDate(pullTime);
        }
        MsgApi.getContractMsgList(reqBean)
                .compose(getProvider().<BaseResponse<GetContractMsgListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetContractMsgListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetContractMsgListResBean>(mView) {
                    @Override
                    public void handleResult(GetContractMsgListResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastContractMsgPullTime(mContext, pullTime);
                        List<ContractMsgDbData> list = resBean == null ? null : resBean.getList();
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
        GetContractMsgListReq req = new GetContractMsgListReq();
        req.setLastReqDate(CacheDataUtil.getLastContractMsgPullTime(mContext));
        MsgApi.getContractMsgList(req)
                .compose(getProvider().<BaseResponse<GetContractMsgListResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetContractMsgListResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetContractMsgListResBean>(mView) {
                    @Override
                    public void handleResult(GetContractMsgListResBean resBean) {
                        String pullTime = resBean == null ? "" : resBean.getLastReqDate();
                        CacheDataUtil.saveLastContractMsgPullTime(mContext, pullTime);
                        final List<ContractMsgDbData> list = resBean == null ? null : resBean.getList();
                        Flowable.create(new FlowableOnSubscribe<List<IContractMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IContractMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateMsgList(list);
                                List<ContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(ContractMsgDbData.class);
                                List<IContractMsgItem> resultList = DataChangeUtil.changeContractMsgDbDataToIContractMsgItem(listCache);
                                e.onNext(resultList);
                            }
                        }, BackpressureStrategy.ERROR)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .compose(getProvider().<List<IContractMsgItem>>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Consumer<List<IContractMsgItem>>() {
                                    @Override
                                    public void accept(List<IContractMsgItem> resultList) throws Exception {
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
    public void makeSingleMsgHaveRead(final IContractMsgItem item, final int position) {
        MsgApi.makeSingleMsgHaveRead(item.getIMsgId(), item.getIMsgType())
                .compose(getProvider().<BaseResponse<Object>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Object>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        ContractMsgDbData dbData = MsgCenterDbHelper.getMsgByMsgId(ContractMsgDbData.class, item.getIMsgId());
                        dbData.setHaveRead(true);
                        MsgCenterDbHelper.saveOrUpdateMsg(dbData);
                        item.setHaveRead(true);
                        mView.updateData(item);
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
        reqBean.setLastReqDate(CacheDataUtil.getLastContractMsgPullTime(mContext));
        reqBean.setType(ModuleType.CONTRACT_MSG.getTypeValue());
        MsgApi.makeTypeMsgHaveRead(reqBean)
                .compose(getProvider().<BaseResponse<Integer>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<Integer>handleResponse())
                .subscribeWith(new CommSubscriber<Object>(mView) {
                    @Override
                    public void handleResult(Object o) {
                        Logger.d("未读消息清除完毕");
                        List<ContractMsgDbData> listCache = MsgCenterDbHelper.getMsgList(ContractMsgDbData.class);
                        if (listCache != null && listCache.size() > 0) {
                            for (ContractMsgDbData dbData : listCache) {
                                dbData.setHaveRead(true);
                            }
                        }
                        MsgCenterDbHelper.saveOrUpdateMsgList(listCache);
                        List<IContractMsgItem> resultList = DataChangeUtil.changeContractMsgDbDataToIContractMsgItem(listCache);
                        mView.showMsgList(resultList);
                        mView.showLoadMoreEnd();

                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }

    @Override
    public void deleteMsg(final IContractMsgItem item) {
        if (item.isHaveRead()) {
            MsgCenterDbHelper.deleteMsgByMsgId(ContractMsgDbData.class, item.getIMsgId());
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
                            MsgCenterDbHelper.deleteMsgByMsgId(ContractMsgDbData.class, item.getIMsgId());
                            mView.removeData(item.getIMsgId());
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
        MsgCenterDbHelper.deleteAllReadMsgData(ContractMsgDbData.class);
        getListFromCache(null);
    }
}
