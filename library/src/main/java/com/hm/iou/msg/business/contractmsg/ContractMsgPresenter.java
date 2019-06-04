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
import com.hm.iou.msg.bean.req.GetContractMsgListReq;
import com.hm.iou.msg.bean.req.MakeMsgTypeAllHaveReadReqBean;
import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;
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
        long unReadMsg = MsgCenterDbHelper.getMsgUnReadNum(ContractMsgDbData.class);
        mView.setBottomClearIconVisible(unReadMsg > 0);
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
                        mView.notifyItem(item, position);
                        getUnReadMsgNum();
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
                        mView.setBottomClearIconVisible(false);
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {

                    }
                });
    }


}
