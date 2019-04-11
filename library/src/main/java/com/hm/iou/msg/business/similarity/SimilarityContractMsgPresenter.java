package com.hm.iou.msg.business.similarity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.ContractMsgBean;
import com.hm.iou.msg.bean.RemindBackMsgBean;
import com.hm.iou.msg.bean.SimilarityContractMsgBean;
import com.hm.iou.msg.bean.req.GetContractListReq;
import com.hm.iou.msg.bean.req.GetRemindBackListReq;
import com.hm.iou.msg.bean.req.GetSimilarityContractListReq;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取消息
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class SimilarityContractMsgPresenter extends MvpActivityPresenter<SimilarityContractMsgContract.View> implements SimilarityContractMsgContract.Presenter {


    public SimilarityContractMsgPresenter(@NonNull Context context, @NonNull SimilarityContractMsgContract.View view) {
        super(context, view);
    }

    @Override
    public void init() {
        mView.showInitLoading();
        GetSimilarityContractListReq req = new GetSimilarityContractListReq();
        MsgApi.getSimilarityContractList(req)
                .compose(getProvider().<BaseResponse<List<SimilarityContractMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<SimilarityContractMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<SimilarityContractMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<SimilarityContractMsgBean> list) {
                        mView.hideInitLoading();
                        mView.enableRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) list);
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
        GetSimilarityContractListReq req = new GetSimilarityContractListReq();
        MsgApi.getSimilarityContractList(req)
                .compose(getProvider().<BaseResponse<List<SimilarityContractMsgBean>>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<List<SimilarityContractMsgBean>>handleResponse())
                .subscribeWith(new CommSubscriber<List<SimilarityContractMsgBean>>(mView) {
                    @Override
                    public void handleResult(List<SimilarityContractMsgBean> list) {
                        mView.hidePullDownRefresh();
                        if (list == null || list.size() == 0) {
                            mView.showDataEmpty();
                        } else {
                            mView.showMsgList((ArrayList) list);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.hidePullDownRefresh();
                    }
                });
    }


}
