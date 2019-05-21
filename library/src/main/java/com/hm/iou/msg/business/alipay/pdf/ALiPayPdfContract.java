package com.hm.iou.msg.business.alipay.pdf;


import com.hm.iou.base.mvp.BaseContract;

/**
 * Created by hjy on 2018/6/5.
 */

public interface ALiPayPdfContract {

    interface View extends BaseContract.BaseView {


        void showShareDialog(String content);

    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 获取合同的分享地址
         *
         * @param iouId
         * @param contractId
         */
        void getShareUrl(String iouId, String contractId);

    }

}