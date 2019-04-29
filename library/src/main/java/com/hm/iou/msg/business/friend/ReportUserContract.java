package com.hm.iou.msg.business.friend;


import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.friend.view.ReportUserActivity;

import java.util.List;

/**
 * Created by AFinalStone on 2017年12月4日 17:23:10
 */
public interface ReportUserContract {

    interface View extends BaseContract.BaseView {
        /**
         * 显示初始化View
         */
        void showInitLoadingView();

        /**
         * 隐藏初始化View
         */
        void hideInitLoadingView();

        /**
         * 显示初始化失败
         */
        void showInitLoadingFailed(String msg);

        /**
         * 显示数据
         */
        void showData(List<ReportUserActivity.IReasonItem> listData);
    }

    interface Presenter extends BaseContract.BasePresenter {

        void getReportList();

        void reportUser(String friendId, int feedbackId, String filePath, String memo);

    }

}
