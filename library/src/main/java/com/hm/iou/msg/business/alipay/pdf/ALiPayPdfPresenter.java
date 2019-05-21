package com.hm.iou.msg.business.alipay.pdf;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hm.iou.base.mvp.MvpActivityPresenter;

/**
 * @author syl
 * @time 2019/5/21 7:03 PM
 */

public class ALiPayPdfPresenter extends MvpActivityPresenter<ALiPayPdfContract.View> implements ALiPayPdfContract.Presenter {

    private String mShareContent;

    public ALiPayPdfPresenter(@NonNull Context context, @NonNull ALiPayPdfContract.View view) {
        super(context, view);
    }


    @Override
    public void getShareUrl(String iouId, String contractId) {
        mView.showShareDialog("pdf分享");
    }
}