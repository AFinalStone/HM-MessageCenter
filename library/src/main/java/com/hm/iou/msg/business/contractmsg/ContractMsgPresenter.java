package com.hm.iou.msg.business.contractmsg;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.database.MsgCenterDbHelper;
import com.hm.iou.database.table.msg.ContractMsgDbData;
import com.hm.iou.logger.Logger;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.event.UpdateMsgCenterUnReadMsgNumEvent;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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
                .map(new Function<List<ContractMsgDbData>, List<ContractMsgDbData>>() {
                    @Override
                    public List<ContractMsgDbData> apply(List<ContractMsgDbData> list) throws Exception {
                        MsgCenterDbHelper.saveOrUpdateContractMsgList(list);
                        List<ContractMsgDbData> resultList = MsgCenterDbHelper.getContractMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        Logger.d("thread_name====" + Thread.currentThread().getName());
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<ContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<ContractMsgDbData> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeContractMsgDbDataToIContractMsgItem(list));
                        }
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.showInitFailed();
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
                .map(new Function<List<ContractMsgDbData>, List<ContractMsgDbData>>() {
                    @Override
                    public List<ContractMsgDbData> apply(List<ContractMsgDbData> list) throws Exception {
                        MsgCenterDbHelper.saveOrUpdateContractMsgList(list);
                        List<ContractMsgDbData> resultList = MsgCenterDbHelper.getContractMsgList();
                        if (resultList == null) {
                            resultList = new ArrayList<>();
                        }
                        Logger.d("thread_name====" + Thread.currentThread().getName());
                        return resultList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommSubscriber<List<ContractMsgDbData>>(mView) {
                    @Override
                    public void handleResult(List<ContractMsgDbData> list) {
                        mView.hidePullDownRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeContractMsgDbDataToIContractMsgItem(list));
                        }
                        EventBus.getDefault().post(new UpdateMsgCenterUnReadMsgNumEvent());
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }
                });
    }


}
