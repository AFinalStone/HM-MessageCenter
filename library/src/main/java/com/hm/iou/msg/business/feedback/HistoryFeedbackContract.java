package com.hm.iou.msg.business.feedback;

import com.hm.iou.base.mvp.BaseContract;
import com.hm.iou.msg.business.feedback.view.IFeedbackListItem;

import java.util.List;

/**
 * Created by hjy on 2018/5/29.
 */

public interface HistoryFeedbackContract {


    interface View extends BaseContract.BaseView {

        /**
         * 显示全部数据加载完毕，没有更多数据了
         */
        void showLoadMoreEnd();

        /**
         * 隐藏下拉刷新View
         */
        void hidePullDownRefresh();

        /**
         * 显示正在加载中
         *
         * @param show true-显示，false-隐藏
         */
        void showLoading(boolean show);

        /**
         * 显示数据为空
         */
        void showDataEmpty();

        /**
         * 显示数据加载失败
         */
        void showDataLoadFail();

        void showFeedbackList(List<IFeedbackListItem> list);
    }

    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 下拉刷新获取数据
         */
        void refresh();

    }

}
