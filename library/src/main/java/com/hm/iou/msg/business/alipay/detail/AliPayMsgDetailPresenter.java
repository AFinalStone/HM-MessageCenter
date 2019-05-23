package com.hm.iou.msg.business.alipay.detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hm.iou.base.mvp.MvpActivityPresenter;
import com.hm.iou.base.utils.CommSubscriber;
import com.hm.iou.base.utils.RxUtil;
import com.hm.iou.msg.api.MsgApi;
import com.hm.iou.msg.bean.GetAliPayMsgDetailResBean;
import com.hm.iou.msg.bean.req.GetAliPayMsgDetailReqBean;
import com.hm.iou.sharedata.model.BaseResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * 支付宝详情页
 *
 * @author syl
 * @time 2018/5/30 下午6:47
 */
public class AliPayMsgDetailPresenter extends MvpActivityPresenter<AliPayMsgDetailContract.View> implements AliPayMsgDetailContract.Presenter {


    public AliPayMsgDetailPresenter(@NonNull Context context, @NonNull AliPayMsgDetailContract.View view) {
        super(context, view);
    }

    @Override
    public void getDetail() {
        mView.showInitLoading();
        GetAliPayMsgDetailReqBean reqBean = new GetAliPayMsgDetailReqBean();
        MsgApi.getAliPayMsgDetail(reqBean)
                .compose(getProvider().<BaseResponse<GetAliPayMsgDetailResBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .map(RxUtil.<GetAliPayMsgDetailResBean>handleResponse())
                .subscribeWith(new CommSubscriber<GetAliPayMsgDetailResBean>(mView) {
                    @Override
                    public void handleResult(GetAliPayMsgDetailResBean detail) {
                        mView.hideInitLoading();
                        if (detail == null) {
                            mView.closeCurrPage();
                            return;
                        }
                        StringBuffer sb = new StringBuffer();
                        String contractId = detail.getJusticeId();
                        if (detail.isDeleted()) {
                            String deleteTime = detail.getOperatorDate();
                            if (!TextUtils.isEmpty(deleteTime)) {
                                deleteTime = deleteTime.replaceAll("\\.", "-");
                            } else {
                                deleteTime = "";
                            }
                            sb.append("删除时间：").append(deleteTime)
                                    .append("\n用户名称：").append(detail.getOperatorName())
                                    .append("\n关联合同：").append(contractId);
                            mView.showFileHaveDelete(sb.toString());
                            return;
                        }
                        String email = detail.getSenderMail();
                        String time = detail.getCreateTime();
                        if (!TextUtils.isEmpty(time)) {
                            time = time.replaceAll("\\.", "-");
                        } else {
                            time = "";
                        }
                        sb.append("关联合同：").append(contractId)
                                .append("\n发送邮箱：").append(email)
                                .append("\n关联时间：").append(time);
                        mView.showContentText(sb.toString());
                        if (1 == detail.getAppliyReceiptStatus()) {
                            mView.showTitle("关联成功", 0xFF578525);
                            mView.showSeeBtn(detail.getPdfUrl());
                        } else if (2 == detail.getAppliyReceiptStatus()) {
                            mView.showTitle("没有发现附件", 0xFFBD0400);
                            mView.showHelpBtn(email, contractId);
                        } else if (3 == detail.getAppliyReceiptStatus()) {
                            mView.showTitle("附件不合规", 0xFFBD0400);
                            mView.showHelpBtn(email, contractId);
                        } else if (4 == detail.getAppliyReceiptStatus()) {
                            mView.showTitle("非关联回单", 0xFFBD0400);
                            mView.showHelpBtn(email, contractId);
                        }
                    }

                    @Override
                    public void handleException(Throwable throwable, String s, String s1) {
                        mView.showInitFailed(s1);
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
}
