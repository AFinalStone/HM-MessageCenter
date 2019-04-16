package com.hm.iou.msg.business.contractmsg;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.util.DataChangeUtil;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

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
                .compose(getProvider().<BaseResponse<List<ContractMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ContractMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ContractMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<ContractMsgBean> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeContractMsgBeanToIContractMsgItem(list));
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.showInitFailed();
                    }

                    @Override
                    public boolean isShowCommError() {
                        return false;
                    }

                    @Override
                    public boolean isShowBusinessError() {
                        return false;
                    }
                });
    }

    @Override
    public void getMsgList() {
        MsgApi.getContractMsgList()
                .compose(getProvider().<BaseResponse<List<ContractMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<ContractMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<ContractMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<ContractMsgBean> list) {
                        mView.hidePullDownRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList(DataChangeUtil.changeContractMsgBeanToIContractMsgItem(list));
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }
                });
    }


}
