package com.hm.iou.msg.business.alipay.detail;

import com.hm.iou.base.mvp.BaseContract;

/**
 * @author : syl
 * @Date : 2018/5/30 11:01
 * @E-Mail : shiyaolei@dafy.com
 */
public class AliPayMsgDetailContract {

    public interface View extends BaseContract.BaseView {

        /**
         * 显示初始化动画
         */
        void showInitLoading();

        /**
         * 初始化失败
         */
        void showInitFailed(String msg);

        /**
         * 关闭初始化动画
         */
        void hideInitLoading();

        /**
         * 显示标题和标题颜色
         *
         * @param title
         * @param color
         */
        void showTitle(String title, int color);

        /**
         * 显示内容
         */
        void showContentText(String content);


        /**
         * 文件已经被删除
         */
        void showFileHaveDelete(String content);

        /**
         * 显示查看按钮
         *
         * @param pdfUrl
         */
        void showSeeBtn(String pdfUrl);

        /**
         * 显示帮助按钮
         *
         * @param email
         * @param contractId
         */
        void showHelpBtn(String email, String contractId);
    }

    public interface Presenter extends BaseContract.BasePresenter {

        /**
         * 获取详情
         */
        void getDetail();

    }
}
