package com.hm.iou.msg.business.alipay;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.AliPayMsgDbData;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.req.GetAliPayMsgListReq;
import com.hm.iou.msg.business.alipay.view.IAliPayMsgItem;
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


    public AliPayMsgPresenter(@NonNull Context context, @NonNull AliPayMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetAliPayMsgListReq req = new GetAliPayMsgListReq();
        MsgApi.getAliPayMsgList(req)
                .compose(getProvider().<BaseResponse<List<AliPayMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<AliPayMsgDbData>>handleResponse())
                .subscribeWith(new CommSubscriber<List<AliPayMsgDbData>>(mView) {
                    @Override
                    public void handleResult(final List<AliPayMsgDbData> list) {
                        Flowable.create(new FlowableOnSubscribe<List<IAliPayMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IAliPayMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateAliPayMsgList(list);
                                List<AliPayMsgDbData> listCache = MsgCenterDbHelper.getAliPayMsgList();
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
        GetAliPayMsgListReq req = new GetAliPayMsgListReq();
        MsgApi.getAliPayMsgList(req)
                .compose(getProvider().<BaseResponse<List<AliPayMsgDbData>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<AliPayMsgDbData>>handleResponse())
                .subscribeWith(new CommSubscriber<List<AliPayMsgDbData>>(mView) {
                    @Override
                    public void handleResult(final List<AliPayMsgDbData> list) {
                        Flowable.create(new FlowableOnSubscribe<List<IAliPayMsgItem>>() {
                            @Override
                            public void subscribe(FlowableEmitter<List<IAliPayMsgItem>> e) throws Exception {
                                MsgCenterDbHelper.saveOrUpdateAliPayMsgList(list);
                                List<AliPayMsgDbData> listCache = MsgCenterDbHelper.getAliPayMsgList();
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
                    }
                });
    }
}
