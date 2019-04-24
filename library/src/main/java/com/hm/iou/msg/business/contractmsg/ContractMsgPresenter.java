package com.hm.iou.msg.business.contractmsg;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.business.contractmsg.view.IContractMsgItem;
import com.hm.iou.msg.event.UpdateMsgCenterUnReadMsgNumEvent;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

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


    public ContractMsgPresenter(@NonNull Context context, @NonNull ContractMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        MsgApi.getContractMsgList()
                .compose(getProvider().<BaseResponse<List<ContractMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ContractMsgDbData>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(final List<ContractMsgDbData> list) {
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                        Flowable.create(new FlowableOnSubscribe<List<IContractMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IContractMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateContractMsgList(list);
                                List<ContractMsgDbData> listCache = MsgCenterDbHelper.getContractMsgList();
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
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.showInitFailed(s1);
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
        MsgApi.getContractMsgList()
                .compose(getProvider().<BaseResponse<List<ContractMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ContractMsgDbData>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(final List<ContractMsgDbData> list) {
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                        Flowable.create(new FlowableOnSubscribe<List<IContractMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IContractMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateContractMsgList(list);
                                List<ContractMsgDbData> listCache = MsgCenterDbHelper.getContractMsgList();
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
                    }
                });
    }


}
